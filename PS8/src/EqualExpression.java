/*
 * Helen Liu
 * 30245078
 * hliu14@cmc.edu
 */

// NEED TO FIX EQUALEXPRESSION TO STORE THE EXPRESSION (=, >, <) 

public class EqualExpression {

    protected String attr;
    protected String op;
    protected String val;
    protected String attr2;
    
    public EqualExpression () {
        this.attr = null;
        this.op = null;
        this.val = null;
        this.attr2 = null;
    }
    
    public String getAttrName() {
        return attr;
    }
    
    public String getOperator(){
    	return op;
    }
    
    public String getAttrVal() {
    	return val;
    }
    
    public String getAttr2Name() {
    	return attr2;
    }
}