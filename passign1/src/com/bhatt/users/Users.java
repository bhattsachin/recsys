package com.bhatt.users;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Read in all the files
 * 
 * @author bhatt
 * 
 */
public class Users {

	public static String[] users;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		init();
	}

	public static void init() {
		try {
			File file = new File("recsys-data-users.csv");
			BufferedReader in = new BufferedReader(new FileReader(file));
			String line;
			int max = 0;
			String[] word;
			int val = 0;
			while ((line = in.readLine()) != null) {
				System.out.println(line);
				word = line.split(",");
				val = Integer.parseInt(word[0]);
				if (val > max) {
					max = val;
				}
			}
			in.close();
			users = new String[max + 1];
			System.out.println("size:" + max);
			updateUsersDetails(file);
			System.out.println("4:" + users[4]);

		} catch (Exception ex) {
			ex.printStackTrace();

		}
	}

	private static void updateUsersDetails(File file) throws Exception {
		BufferedReader in = new BufferedReader(new FileReader(file));
		String line;
		String[] word;
		while ((line = in.readLine()) != null) {
			word = line.split(",");
			users[Integer.parseInt(word[0])] = word[1];
		}
		in.close();

	}

}
