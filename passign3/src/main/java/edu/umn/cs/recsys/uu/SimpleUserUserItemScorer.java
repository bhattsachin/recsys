package edu.umn.cs.recsys.uu;

import it.unimi.dsi.fastutil.longs.LongSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import org.grouplens.lenskit.basic.AbstractItemScorer;
import org.grouplens.lenskit.data.dao.ItemEventDAO;
import org.grouplens.lenskit.data.dao.UserEventDAO;
import org.grouplens.lenskit.data.event.Rating;
import org.grouplens.lenskit.data.history.History;
import org.grouplens.lenskit.data.history.RatingVectorUserHistorySummarizer;
import org.grouplens.lenskit.data.history.UserHistory;
import org.grouplens.lenskit.vectors.MutableSparseVector;
import org.grouplens.lenskit.vectors.SparseVector;
import org.grouplens.lenskit.vectors.VectorEntry;
import org.grouplens.lenskit.vectors.similarity.CosineVectorSimilarity;

/**
 * User-user item scorer.
 * @author <a href="http://www.grouplens.org">GroupLens Research</a>
 */
public class SimpleUserUserItemScorer extends AbstractItemScorer {
    private final UserEventDAO userDao;
    private final ItemEventDAO itemDao;

    @Inject
    public SimpleUserUserItemScorer(UserEventDAO udao, ItemEventDAO idao) {
        userDao = udao;
        itemDao = idao;
    }

    @Override
    public void score(long user, @Nonnull MutableSparseVector scores) {
        SparseVector userVector = getUserRatingVector(user);

        // TODO Score items for this user using user-user collaborative filtering
        //System.out.println(userVector.size());
        // This is the loop structure to iterate over items to score
        SparseVector userRatings = getUserRatingVector(user);
        MutableSparseVector userRatingMean = userRatings.mutableCopy();
        userRatingMean.add(-userRatings.mean());
        MutableSparseVector neighbourRatingMean;
        
        Map<Long, Double> bestBudies = new HashMap<Long, Double>();
        for (VectorEntry e: scores.fast(VectorEntry.State.EITHER)) {

        		//System.out.println(e.getKey());
        		LongSet neighbours = itemDao.getUsersForItem(e.getKey());
        		//remove the guy himself
        		neighbours.remove(user);
        		double sum=0;
        		
        		for(long neighbour : neighbours){
        			SparseVector neighbourRatings = getUserRatingVector(neighbour);
        			//take mean of all values
        			neighbourRatingMean = neighbourRatings.mutableCopy();
        			neighbourRatingMean.add(-neighbourRatings.mean());
        			
        			
        			//should we exclude myself
        			double similarity = new CosineVectorSimilarity().similarity(neighbourRatingMean, userRatingMean);
        			bestBudies.put(neighbour, similarity);
        			//System.out.println("userid:" + neighbour + " cosineSim=" + similarity);
        		}
        		
        		bestBudies = sortByValue(bestBudies);
        		int count=0;
        		List<Long> top30 = new ArrayList<Long>();
        		for(Map.Entry<Long, Double> entry : bestBudies.entrySet()){
        			//System.out.println(entry.getKey() + " ::: " + entry.getValue());
        			if(!itemDao.getUsersForItem(e.getKey()).contains(entry.getKey())){
        				continue;
        			}
        			if(count>=30)break;
        			top30.add(entry.getKey());
        			count++;
        			//System.out.println(entry.getKey());
        			
        		}
        		//System.out.println("+=====================");
        		
        		double totalCosine = 0;
        		double totalSum=0;
        		double diff;
        		//do something to top 30
        		SparseVector neiRating;
        		for(Long nei : top30){
        			neiRating = getUserRatingVector(nei);
        			if(neiRating.containsKey(e.getKey())){
	        			diff = neiRating.get(e.getKey())-neiRating.mean();
	        			totalCosine += (diff)*bestBudies.get(nei);
	        			totalSum += Math.abs(bestBudies.get(nei));
        			}
        			
        			
        		}
        		
        		double result = userVector.mean() + (totalCosine/totalSum);
        		//System.out.println(result);
        		scores.set(e.getKey(), result);
        	
        }
    }

    /**
     * Get a user's rating vector.
     * @param user The user ID.
     * @return The rating vector.
     */
    private SparseVector getUserRatingVector(long user) {
        UserHistory<Rating> history = userDao.getEventsForUser(user, Rating.class);
        if (history == null) {
            history = History.forUser(user);
        }
        return RatingVectorUserHistorySummarizer.makeRatingVector(history);
    }
    
    private static Map sortByValue(Map map) {
        List list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator() {
             public int compare(Object o1, Object o2) {
                  return ((Comparable) ((Map.Entry) (o2)).getValue())
                 .compareTo(((Map.Entry) (o1)).getValue());
             }
        });

       Map result = new LinkedHashMap();
       for (Iterator it = list.iterator(); it.hasNext();) {
           Map.Entry entry = (Map.Entry)it.next();
           result.put(entry.getKey(), entry.getValue());
       }
       return result;
   } 
}
