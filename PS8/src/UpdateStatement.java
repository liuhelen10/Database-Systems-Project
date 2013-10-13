/*
 * Helen Liu
 * 30245078
 * hliu14@cmc.edu
 */

public class UpdateStatement {

	protected String tableName;
	protected EqualExpression set;
	protected EqualExpression where;
	
	public UpdateStatement() {
		tableName = "";
		set = new EqualExpression();
		where = new EqualExpression();
	}
	
	public UpdateStatement(String tableName, EqualExpression set, EqualExpression where) {
		this.tableName = tableName;
		this.set = set;
		this.where = where;
	}
	
	public String getTableName() {
		return tableName;
	}
	
	public EqualExpression getSet() {
		return set;
	}
	
	public EqualExpression getWhere() {
		return where;
	}
	
}
