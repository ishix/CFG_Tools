import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class ParaCFG {
	protected CFGGraph graph;
	protected ArrayList< String > history; //record of the game
	protected ArrayList< String > shotvectors; //record of firing nodes
	
	/* Constructor */
	ParaCFG( CFGGraph g ) {
		graph = g;
		history = new ArrayList< String >();
		shotvectors = new ArrayList< String >();
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
			shotvectors.add(Arrays.toString(graph.activeNodes));
			graph.fireAll();
			str = graph.configString();
			int index = history.indexOf(str);
			if ( index == -1 ) {
				history.add(str);
			}
			else {
				history.add(str);
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
	
	public void printShotvectors() {
		System.out.println("THE FRING VERTICES SO FAR:");
		for ( int i = 0; i < shotvectors.size(); i++) {
			System.out.println(shotvectors.get(i));
		}
	}
	
	/* TEST TEST TEST */
	public static void test() {
		//ile directory = new File("./");
		String fpath = "star-3.txt";
				//directory.getAbsolutePath() + "/star-3.txt"; 
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
		String fpath = "./rings-4_3.txt";
		APointAndRings gr = new APointAndRings(fpath);
		ParaCFG game = new ParaCFG(gr);
		System.out.println("ADJACENCY MATRIX: ");
		gr.printAdjMat();
		System.out.println("PORTS ARE:");
		
		System.out.println("CONFIGS BY RINGS: ");
		gr.printConfigByRings();
		System.out.println("GAME PERIOD = " + game.computePeriod());
		game.printHistory();
		game.printShotvectors();
		//System.out.println("JUST TO BE SURE, THIS IS THE NEXT CONFIG: ");
		//System.out.println(gr.configString());
	}
	
	public static void printUsage() {
		try {
			BufferedReader buf = new BufferedReader( new FileReader("./usage.txt"));
			String line;
			while ( (line = buf.readLine()) != null ) {
				System.out.println(line);
			}
			buf.close();
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	public static void run(String[] args) {
		System.out.println("args.length = " + args.length);
		for ( int i = 0; i < args.length; i++ ) {
			System.out.print(args[i] + " ");
		}
		System.out.println();
		
		if ( args.length < 1 || args.length > 3 ) {
			//printUsage();
			return;
		}
		
		String inpath, outpath;
		boolean ringMode = false;
		
		switch ( args.length ) {
			case 1:
				inpath = args[0];
				outpath = inpath + "." + new SimpleDateFormat("yyyyMMddHHmm").format( new Date() );
				break;
			case 3:
				if ( args[0] == "--rings" ) {
					ringMode = true;
				}
				inpath = args[1];
				outpath = args[2];
				break;
			default:
				if ( args[0].equals("--rings") ) {
					System.out.println("RING MODE");
					ringMode = true;
					inpath = args[1];
					outpath = inpath + "." + new SimpleDateFormat("yyyyMMddHHmm").format( new Date() );
				}
				else if ( args[0].equals("--generic") ) {
					System.out.println("GENERIC MODE WITH OUTPUT UNSPECIFIED");
					inpath = args[1];
					outpath = inpath + "." + new SimpleDateFormat("yyyyMMddHHmm").format( new Date() );
				}
				else {
					System.out.println("GENERIC MODE WITH OUTPUT SPECIFIED");
					inpath = args[0];
					outpath = args[1];
				}
		}
		try {
			BufferedWriter outbuf = new BufferedWriter( new FileWriter(outpath) );
			ParaCFG game;
			if ( ringMode ) {
				APointAndRings gr = new APointAndRings(inpath);
				game = new ParaCFG(gr);
			}
			else {
				CFGGraph gr = new CFGGraph(inpath);
				game = new ParaCFG(gr);
			}
			int period = game.computePeriod();
			outbuf.write("GAME PERIOD:\n");
			outbuf.write(period + '\n');
			outbuf.write("GAME CONFIGS:\n");
			for ( int i = 0; i < game.history.size(); i++ ) {
				outbuf.write(game.history.get(i) + '\n');
			}
			outbuf.write("SHOT VECTORS:\n");
			for ( int i = 0; i < game.shotvectors.size(); i++ ) {
				outbuf.write(game.shotvectors.get(i) + '\n');
			}
			outbuf.close();
			System.out.println("Results written in file: " + outpath);
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}	
	}
	
	public static void main(String[] args) {
		//test();
		//testRings();
		run(args);
//		for ( int i = 0; i < args.length; i++ ) {
//			System.out.print(args[i] + " ");
//		}
	}
}
