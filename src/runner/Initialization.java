package runner;

public class Initialization {
	private Initialization(){}
	
	public static void init()
	{
		//initialize all algorithm definitions
		algorithms.textblock.Definition.init();
		algorithms.fullpng.Definition.init();
	}
}
