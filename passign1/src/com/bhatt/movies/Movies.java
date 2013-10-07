package com.bhatt.movies;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class Movies {

	public static String[] movies = new String[100];
	public static BiMap<Integer, Integer> movieMap = HashBiMap.create();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		init();

	}
	
	public static void init(){
		readData("recsys-data-movie-titles.csv");
	}

	private static void readData(String filename) {
		try {
			File file = new File(filename);
			BufferedReader in = new BufferedReader(new FileReader(file));
			String line;
			String[] word;
			int val = 0;
			while ((line = in.readLine()) != null) {
				//System.out.println(line);
				word = line.split(",");
				movieMap.put(val, Integer.parseInt(word[0]));
				
				movies[val] = word[1];
				val++;
			}
			System.out.println("size:" + val);
			// updateDetails(file);
			System.out.println("4:" + movies[4]);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}
	
	
}
