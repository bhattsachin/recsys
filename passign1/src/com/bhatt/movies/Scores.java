package com.bhatt.movies;

import java.util.Arrays;

import com.bhatt.users.Users;

public class Scores {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		init();
		calculateSimpleScore(558);
		calculateSimpleScore(146);
		calculateSimpleScore(581);
		
	}

	public static void init() {
		Ratings.init();
		// for(int i=0;i<Ratings.ratings.length;i++){
		// System.out.println(Arrays.toString(Ratings.ratings[i]));
		// }
	}

	/**
	 * (x and y) / x
	 */
	public static double[] calculateSimpleScore(int movieId) {
		int movieidx = Movies.movieMap.inverse().get(movieId);
		int score = 0;
		double[] finalScore = new double[Movies.movies.length];

		for (int i = 0; i < Movies.movies.length; i++) {
			score = 0;
			for (int j = 0; j < Users.users.length; j++) {
				if (Ratings.ratings[j][movieidx] >= 0
						&& Ratings.ratings[j][i] >= 0) {
					score++;
				}
			}
			finalScore[i] = score;
		}

		double base = finalScore[movieidx];
		for (int i = 0; i < finalScore.length; i++) {
			finalScore[i] = finalScore[i] / base;
		}
		
		double[] finalScoreBackup = finalScore.clone();
		
		//System.out.print(movieId + ",");
		//printTopFive(finalScore);
		//System.out.println("");
		return finalScoreBackup;

	}

	public static void printTopFive(double[] finalScore) {
		double[] finalScoreCopy = finalScore.clone();

		Arrays.sort(finalScore);
		//System.out.println(Arrays.toString(finalScore));
		int idx;
		for(int i=finalScore.length-2;i>finalScore.length-7;i--){
			idx=0;
			for(int j=0;j<finalScoreCopy.length;j++){
				if(finalScore[i]==finalScoreCopy[j]){
					idx = j;
					break;
				}
			}
			System.out.print(Movies.movieMap.get(idx) + "," + (((double)Math.round(finalScore[i]*100))/100) + ",");
		}
		
	}

}
