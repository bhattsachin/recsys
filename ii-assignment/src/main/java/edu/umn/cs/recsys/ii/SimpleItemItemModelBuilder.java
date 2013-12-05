package edu.umn.cs.recsys.ii;

import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSortedSet;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import org.grouplens.lenskit.collections.LongUtils;
import org.grouplens.lenskit.core.Transient;
import org.grouplens.lenskit.cursors.Cursor;
import org.grouplens.lenskit.data.dao.ItemDAO;
import org.grouplens.lenskit.data.dao.UserEventDAO;
import org.grouplens.lenskit.data.event.Event;
import org.grouplens.lenskit.data.history.RatingVectorUserHistorySummarizer;
import org.grouplens.lenskit.data.history.UserHistory;
import org.grouplens.lenskit.scored.ScoredId;
import org.grouplens.lenskit.scored.ScoredIdListBuilder;
import org.grouplens.lenskit.scored.ScoredIds;
import org.grouplens.lenskit.vectors.ImmutableSparseVector;
import org.grouplens.lenskit.vectors.MutableSparseVector;
import org.grouplens.lenskit.vectors.VectorEntry;
import org.grouplens.lenskit.vectors.similarity.CosineVectorSimilarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="http://www.grouplens.org">GroupLens Research</a>
 */
public class SimpleItemItemModelBuilder implements Provider<SimpleItemItemModel> {
    private final ItemDAO itemDao;
    private final UserEventDAO userEventDao;
    private static final Logger logger = LoggerFactory.getLogger(SimpleItemItemModelBuilder.class);;

    @Inject
    public SimpleItemItemModelBuilder(@Transient ItemDAO idao,
                                      @Transient UserEventDAO uedao) {
        itemDao = idao;
        userEventDao = uedao;
    }

    @Override
    public SimpleItemItemModel get() {
        // Get the transposed rating matrix
        // This gives us a map of item IDs to those items' rating vectors
        Map<Long, ImmutableSparseVector> itemVectors = getItemVectors();

        // Get all items - you might find this useful
        LongSortedSet items = LongUtils.packedSet(itemVectors.keySet());
        // Map items to vectors of item similarities
        Map<Long,MutableSparseVector> itemSimilarities = new HashMap<Long, MutableSparseVector>();
        
        MutableSparseVector vect;
        CosineVectorSimilarity cosVect = new CosineVectorSimilarity();
        // TODO Compute the similarities between each pair of items
        for(long item : items){
        	vect = MutableSparseVector.create(items);
        	for(long other : items){
        		ImmutableSparseVector vec1 = itemVectors.get(item);
        		ImmutableSparseVector vec2 = itemVectors.get(other);
        		//System.out.println((vec1.dot(vec2))/(vec1.norm()*vec2.norm()));
        		vect.set(other,cosVect.similarity(vec1, vec2));
        		//vect.add((vec1.dot(vec2))/(vec1.norm()*vec2.norm()));
        		
        		
        	}
        	itemSimilarities.put(item, vect);
        }
        //for(long itm : items){
        //	 System.out.println("" + itm + "->" + itemSimilarities.get(itm));
        // }
        
        
        Map<Long, List<ScoredId>> m = new HashMap<Long, List<ScoredId>>();
        ScoredIdListBuilder scorelist=null;
        for(long item : items){
        	scorelist = ScoredIds.newListBuilder(); 
        	
        	for(VectorEntry entry : itemSimilarities.get(item)){
        		if(entry.getValue()>0){
        		scorelist.add(entry.getKey(), entry.getValue());
        		}
        		
        	}
        	
        	scorelist.sort(ScoredIds.scoreOrder().reverse());
        	m.put(item, scorelist.finish());
        }

        // It will need to be in a map of longs to lists of Scored IDs to store in the model
        return new SimpleItemItemModel(m);
    }

    /**
     * Load the data into memory, indexed by item.
     * @return A map from item IDs to item rating vectors. Each vector contains users' ratings for
     * the item, keyed by user ID.
     */
    public Map<Long,ImmutableSparseVector> getItemVectors() {
        // set up storage for building each item's rating vector
        LongSet items = itemDao.getItemIds();
        // map items to maps from users to ratings
        Map<Long,Map<Long,Double>> itemData = new HashMap<Long, Map<Long, Double>>();
        for (long item: items) {
            itemData.put(item, new HashMap<Long, Double>());
        }
        // itemData should now contain a map to accumulate the ratings of each item

        // stream over all user events
        Cursor<UserHistory<Event>> stream = userEventDao.streamEventsByUser();
        try {
            for (UserHistory<Event> evt: stream) {
                MutableSparseVector vector = RatingVectorUserHistorySummarizer.makeRatingVector(evt).mutableCopy();
                double mean = vector.mean();
                for(long item : vector.keySet()){
                	//System.out.println("id: " + evt.getUserId() + " val: " + (vector.get(item)-mean) );
                	itemData.get(item).put(evt.getUserId(), vector.get(item)-mean);
                	
                }
                
                
                // vector is now the user's rating vector
                // TODO Normalize this vector and store the ratings in the item data
            }
        } finally {
            stream.close();
        }

        // This loop converts our temporary item storage to a map of item vectors
        Map<Long,ImmutableSparseVector> itemVectors = new HashMap<Long, ImmutableSparseVector>();
        for (Map.Entry<Long,Map<Long,Double>> entry: itemData.entrySet()) {
            MutableSparseVector vec = MutableSparseVector.create(entry.getValue());
            itemVectors.put(entry.getKey(), vec.immutable());
        }
        return itemVectors;
    }
}
