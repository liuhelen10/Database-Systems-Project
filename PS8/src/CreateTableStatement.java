/*
 * Helen Liu
 * 30245078
 * hliu14@cmc.edu
 */

import java.util.ArrayList;

public class CreateTableStatement {
	
	protected String name;
	protected ArrayList<String> attrNames;
	protected ArrayList<String> attrTypes;
	
	public CreateTableStatement() {
		this.name = null;
		this.attrNames = new ArrayList<String>();
		this.attrTypes = new ArrayList<String>();
	}
	
	public CreateTableStatement(String name, ArrayList<String> attrNames, ArrayList<String> attrTypes) {
		this.name = name;
		this.attrNames = attrNames;
		this.attrTypes = attrTypes;
	}
	
	public String getName() {
		return name;
	}
	
	public ArrayList<String> getAttrNames() {
		return attrNames;
	}
	
	public ArrayList<String> getAttrTypes() {
		return attrTypes;
	}
	
	
}
