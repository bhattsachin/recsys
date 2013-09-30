package com.bhatt.movies;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import com.bhatt.users.Users;

public class Ratings {
	public static double ratings[][];

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Users.init();
		Movies.init();
		//initialize ratings matrix size
		ratings = new double[Users.users.length][Movies.movies.length];
		readData("recsys-data-ratings.csv");
		

	}
	
	private static void readData(String filename){
		try {
			File file = new File(filename);
			BufferedReader in = new BufferedReader(new FileReader(file));
			String line;
			String[] word;
			while ((line = in.readLine()) != null) {
				System.out.println(line);
				word = line.split(",");
				ratings[Integer.parseInt(word[0])][Movies.movieMap.inverse().get(Integer.parseInt(word[1]))] = Double.parseDouble(word[2]);
			}
		} catch (Exception ex) {
			ex.printStackTrace();

		}
	}

}
