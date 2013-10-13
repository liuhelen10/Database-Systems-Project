/*
 * Helen Liu
 * 30245078
 * hliu14@cmc.edu
 */

public class CreateDatabaseStatement {
	
	protected String name;
	
	public CreateDatabaseStatement() {
		this.name = "";
	}
	
	public CreateDatabaseStatement(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
