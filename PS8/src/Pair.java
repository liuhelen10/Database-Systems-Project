/**
 * Helen Liu
 * 30245078
 * hliu14@cmc.edu
 */

import java.util.ArrayList;

public class Pair {

	private String key;
	private ArrayList<Integer> location;
	
	public Pair() {
		key = null;
		location = new ArrayList<Integer>();
	}
	
	public Pair(String key, ArrayList<Integer> location) {
		this.key = key;
		this.location = location;
	}
	
	public String getKey() {
		return key;
	}
	
	public ArrayList<Integer> getLocation() {
		return location;
	}
	
}
