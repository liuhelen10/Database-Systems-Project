/*
 * Helen Liu
 * 30245078
 * hliu14@cmc.edu
 */

import java.util.ArrayList;

public class InsertStatement {
	
	protected String tableName;
	protected ArrayList<String> attributes;
	
	public InsertStatement() {
		tableName = "";
		attributes = new ArrayList<String>();
	}
	
	public InsertStatement(String tableName, ArrayList<String> attributes) {
		this.tableName = tableName;
		this.attributes = attributes;
	}
	
	public String getTableName() {
		return tableName;
	}
	
	public ArrayList<String> getAttributes() {
		return attributes;
	}
}
