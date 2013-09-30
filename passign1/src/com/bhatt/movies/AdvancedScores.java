package com.bhatt.movies;

import com.bhatt.users.Users;

public class AdvancedScores {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		init();
		calculateAdvancedScore(558);
		calculateAdvancedScore(146);
		calculateAdvancedScore(581);
	}

	public static void init() {
		Ratings.init();
	}

	/**
	 * Uses ((x and y) / x) / ((!x and y) / !x) to compute scores
	 */
	public static void calculateAdvancedScore(int movieid) {
		int movieidx = Movies.movieMap.inverse().get(movieid);
		int score = 0;
		double[] primitiveScores = new double[Movies.movies.length];
		// x!
		int xnot = 0;
		for (int i = 0; i < Movies.movies.length; i++) {
			score = 0;
			for (int j = 0; j < Users.users.length; j++) {
				
				if(movieidx==i){
					if(Ratings.ratings[j][movieidx] < 0)
						xnot++;
				}else{
				
				
				
				if (Ratings.ratings[j][movieidx] < 0) {
					
					if (Ratings.ratings[j][i] >= 0) {
						score++;
					}
					
					
				}
				
				}

			}
			primitiveScores[i] = score;
		}
		
		
		for (int i = 0; i < primitiveScores.length; i++) {
			primitiveScores[i] = (primitiveScores[i])/ (xnot-9);
		}
		
		double[] basicScores = Scores.calculateSimpleScore(movieid);
		
		double[] finalScores = new double[basicScores.length];
		for(int i=0;i<finalScores.length;i++){
			finalScores[i] = basicScores[i]/primitiveScores[i];
		}
		System.out.print(movieid + ",");
		Scores.printTopFive(finalScores);
		System.out.println("");
	}

}
