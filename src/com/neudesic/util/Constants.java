package com.neudesic.util;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.Random;

public class Constants {

	public static Properties eventColumnValueMapper= loadFromFile("event-column-value.properties");
	public static Properties eventCheckoutColumnValueMapper = loadFromFile("event-checkout-column-value.properties");
	public static Properties config = loadFromFile("config.properties");
	public static Random ran = new Random();
	
	private static Properties loadFromFile(String filename) {
		URL url = ClassLoader.getSystemResource(filename);
		// InputStream stream =
		// ConstantUtil.class.getResourceAsStream(filename);
		Properties config = new Properties();
		try {
			config.load(url.openStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("***********  Configuration Details ***********");
		for (Object key : config.keySet()) {
			System.out.println(String.valueOf(key) + "  :  " + config.getProperty(String.valueOf(key)));
		}
		System.out.println("**********  Configuration Details End **********");
		return config;
	}

	public static String getRandomString(String[] array) {			
		return array[ran.nextInt(array.length) + 0];
	}
}
