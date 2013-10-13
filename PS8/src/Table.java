/*
 * Helen Liu
 * 30245078
 * hliu14@cmc.edu
 */

import java.util.ArrayList;


public class Table {
	
	protected String tableName;
	protected ArrayList<String> attrNames;
	protected ArrayList<String> attrTypes;
	protected ArrayList<Object> tuples;
	
	public Table() {
		tableName = "";
		attrNames = new ArrayList<String>();
		attrTypes = new ArrayList<String>();
		tuples = new ArrayList<Object>();
	}
	
	public Table(String tableName, ArrayList<String> attrNames, ArrayList<String> attrTypes, ArrayList<Object> tuples) {
		this.tableName = tableName;
		this.attrNames = attrNames;
		this.attrTypes = attrTypes;
		this.tuples = tuples;
	}
	
	public String getTableName() {
		return tableName;
	}
	
	public ArrayList<String> getAttrNames() {
		return attrNames;
	}
	
	public ArrayList<String> getAttrTypes() {
		return attrTypes;
	}
	
	public ArrayList<Object> getTuples() {
		return tuples;
	}
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public void setAttrNames(ArrayList<String> attrNames) {
		this.attrNames = attrNames;
	}
	
	public void setAttrTypes(ArrayList<String> attrTypes) {
		this.attrTypes = attrTypes;
	}
	
	public void addTuple(Object tuple) {
		tuples.add(tuple);
	}

}
