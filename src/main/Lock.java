package main;

public class Lock {
	private int index=0;
	
	public Lock(){
		this.index=0;
	}
	
	public void setIndex(int index){
		this.index=index;
	}
	public int getIndex(){
		return this.index;
	}
}