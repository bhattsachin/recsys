package com.bhatt.movies;

import java.util.Arrays;

import com.bhatt.users.Users;

public class Scores {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		init();
		calculateSimpleScore(11);

	}
	
	public static void init(){
		Ratings.init();
		//for(int i=0;i<Ratings.ratings.length;i++){
		//	System.out.println(Arrays.toString(Ratings.ratings[i]));
		//}
	}
	/**
	 * (x and y) / x
	 */
	public static void calculateSimpleScore(int movieId){
		int movieidx = Movies.movieMap.inverse().get(movieId);
		int score=0;
		int[] finalScore = new int[Movies.movies.length];
		for(int i=0;i<Movies.movies.length;i++){
			score = 0;
			for(int j=0;j<Users.users.length;j++){
				if(Ratings.ratings[j][movieidx]>=0 && Ratings.ratings[j][i]>=0){
					score++;
				}
			}
			finalScore[i] = score;
		}
		
		System.out.println(Arrays.toString(finalScore));
		
		
	}

}
