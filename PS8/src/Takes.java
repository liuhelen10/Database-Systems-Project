// This file is generated by CreateTable.java...

public class Takes {

	public int tasid;
	public int tacid;

	public Takes () { 
		tasid = 0;
		tacid = 0;
	}

	public Takes(int tasid, int tacid) {
		this.tasid = tasid;
		this.tacid = tacid;
	}

	public int gettasid () {
		return tasid;
	}
	public int gettacid () {
		return tacid;
	}

	public void settasid (int tasid) {
		this.tasid = tasid;
	}
	public void settacid (int tacid) {
		this.tacid = tacid;
	}

}
