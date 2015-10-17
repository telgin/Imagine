package testing;

public class CodeTimer {
	private long startMillis = 0;
	private long endMillis = 0;
	
	public CodeTimer(){}
	
	public void start(){
		startMillis = System.currentTimeMillis();
	}
	
	public void end(){
		endMillis = System.currentTimeMillis();
	}
	
	public long getElapsedTime(){
		return (endMillis - startMillis);
	}
}
