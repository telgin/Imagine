package stats;

public class StateStat extends Stat{

	public StateStat(String name, String initialState) {
		super(name);
		updateProgress(initialState);
	}
	
	public StateStat(String name, Double initialState) {
		super(name);
		updateProgress(initialState);
	}

}
