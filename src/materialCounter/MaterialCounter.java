package materialCounter;

import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Program to determine what resources and materials a player needs in order
 * to build and/or furnish any house or homestead in Skyrim
 * @author Adam Cooper
 */
public class MaterialCounter {
	
	/**
	 * Main functionality of the program
	 */
	public static void main(String[] args) {
		final Scanner in = new Scanner(System.in);
		House.Location[] locations = House.Location.values();
		int input;
		
		System.out.println("Where would you like the house to be? ");
		for (int i = 0; i < locations.length; i++) {
			System.out.println((i + 1) + ": " + 
					locations[i].name().replaceAll("_", " "));
		}
		
		// until we get a valid input
		do {
			try {
				input = in.nextInt();
			} catch (InputMismatchException e) {
				input = 0;
			}
			in.nextLine();
			in.reset();
		} while (input < 1 || input > locations.length);
		
		House house = new House(House.getLocationByName
				(House.formatToEnum(locations[--input].name())));
		
		System.out.println(house.toString());
		in.close();
	}
	
	
	
	
	/**
	 * Helper method that tallies within a HashMap
	 * @param item The key
	 * @param amt The value to add to the key
	 * @param tracker The HashMap used to tally the values for each key
	 */
	public static void tally(String item, Integer amt, HashMap<String, Integer> tracker) {
		if (!tracker.containsKey(item))
			tracker.put(item, 0);
		tracker.put(item, tracker.get(item) + amt);
	}
}
