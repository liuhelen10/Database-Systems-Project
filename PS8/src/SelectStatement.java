import java.util.ArrayList;

/*
 * Helen Liu
 * 30245078
 * hliu14@cmc.edu
 */

public class SelectStatement {

    //protected String select;
	protected ArrayList<String> select;
    //protected String from;
    protected ArrayList<String> from;
    //protected EqualExpression where;
    //protected ArrayList<EqualExpression> where;
    protected ArrayList<EqualExpression> where;
   
    //protected EqualExpression where1;
    //protected EqualExpression where2;

    public SelectStatement () {
        this.select = new ArrayList<String>();
        this.from = new ArrayList<String>();
        this.where = new ArrayList<EqualExpression>();
        //this.from = null;
        //this.where = null;
        //this.where1 = null;
        //this.where2 = null;
    }
    
    public SelectStatement (ArrayList<String> select, ArrayList<String> from, ArrayList<EqualExpression> where) {
        this.select = select;
        this.from = from;
        this.where = where;
        
        //this.where1 = where1;
        //this.where2 = where2;
    }
    
    public ArrayList<String> getSelect() {
    	return select;
    }
    
    public ArrayList<String> getFrom() {
    	return from;
    }
    
    public ArrayList<EqualExpression> getWhere() {
    	return where;
    }
    
}