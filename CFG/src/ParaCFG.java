import java.util.ArrayList;

public class ParaCFG {
	protected CFGGraph graph;
	protected ArrayList< String > history; //record of the game
	
	/* Constructor */
	ParaCFG( CFGGraph g ) {
		graph = g;
		history = new ArrayList< String >();
		// put the initial config into the history and increment the round counter
		history.add(graph.configString());
	}
	
	/* Change initial config */
	public boolean setInitConfig( int[] config ) {
		if ( config.length != graph.size() ) {
			System.out.println("ParaCFG.setInitConfig: Bad config size. No modification made.");
			return false;
		}
		else {
			graph.setConfig(config);
			// reset history
			history = new ArrayList< String >();
			// put the initial config into the history and increment the round counter
			history.add( graph.configString() );
			return true;
		}
	}
	
	/* Run the game and compute the period */
	public int computePeriod() {
		String str;
		while ( true ) {
			graph.fireAll();
			str = graph.configString();
			int index = history.indexOf(str);
			if ( index == -1 ) {
				history.add(str);
			}
			else {
				return history.size() - index;
			}
		}
		
	}
	
	/* Verbose */
	public void printHistory() {
		System.out.println("THE ENCOUNTERED CONFIGURATIONS SO FAR:");
		for ( int i = 0; i < history.size(); i++) {
			System.out.println(history.get(i));
		}
	}
	
	/* TEST TEST TEST */
	public static void test() {
		String fpath = "/home/ishi/eclipse-workspace/CFG/star-3.txt";
		CFGGraph gr = new CFGGraph(fpath);
		ParaCFG game = new ParaCFG(gr);
		System.out.println("ADJACENCY MATRIX: ");
		gr.printAdjMat();
		int[] aconfig = {5, 0, 0, 0};
		game.setInitConfig(aconfig);
		System.out.println("New CONFIG: ");
		System.out.println(gr.configString());
		System.out.println("GAME PERIOD = " + game.computePeriod());
		game.printHistory();
		
	}
	
	public static void testRings() {
		String fpath = "/home/ishi/eclipse-workspace/CFG/rings-4_3.txt";
		APointAndRings gr = new APointAndRings(fpath);
		ParaCFG game = new ParaCFG(gr);
		System.out.println("ADJACENCY MATRIX: ");
		gr.printAdjMat();
		System.out.println("PORTS ARE:");
		
		System.out.println("CONFIGS BY RINGS: ");
		gr.printConfigByRings();
		System.out.println("GAME PERIOD = " + game.computePeriod());
		game.printHistory();
		System.out.println("JUST TO BE SURE, THIS IS THE NEXT CONFIG: ");
		System.out.println(gr.configString());
	}

	public static void main(String[] args) {
		//test();
		testRings();
	}
}
