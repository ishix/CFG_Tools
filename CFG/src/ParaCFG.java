import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ParaCFG {
	protected CFGGraph graph;
	protected ArrayList< String > history; //record of the game
	protected ArrayList< String > firingnodes; //record of firing nodes
        //record  the firing nodes history in order to compute the successor firing digraph
        protected ArrayList< int[] > firingnodeshistory; 
        protected String sfddot; //record the dot format successor firing digraph
	
	/* Constructor */
	ParaCFG( CFGGraph g ) {
		graph = g;
		history = new ArrayList< String >();
		firingnodes = new ArrayList< String >();
                firingnodeshistory = new ArrayList< int[] >();
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
			String sv = "";
			for ( int i = 0; i < graph.activeNodes.length; i++ ) {
				sv += (graph.activeNodes[i] != 0) ? "1 " : "0 ";
			}
                        //save activeNodes array
                        firingnodeshistory.add(graph.activeNodes.clone());
			firingnodes.add(sv);
			graph.fireAll();
			str = graph.configString();
			int index = history.indexOf(str);
			if ( index == -1 ) {
				history.add(str);
			}
			else {
				int p = history.size() - index;
				history.add(str);
				return p;
			}
		}
		
	}

        /* construct the successor firing digraph dot format */
        public void set_sfddot(int period) {
                sfddot = graph.get_sfddot(period,firingnodeshistory);
        }
	
	/* Verbose */
	public void printHistory() {
		System.out.println("THE ENCOUNTERED CONFIGURATIONS SO FAR:");
		for ( int i = 0; i < history.size(); i++) {
			System.out.println(history.get(i));
		}
	}
	
	public void printfiringnodes() {
		System.out.println("THE FIRING VERTICES SO FAR:");
		for ( int i = 0; i < firingnodes.size(); i++) {
			System.out.println(firingnodes.get(i));
		}
	}

        public static void createDotGraph(String dotFormat,String fileName){
                GraphViz gv=new GraphViz();
                gv.addln(gv.start_graph());
                gv.add(dotFormat);
                gv.addln(gv.end_graph());
                // String type = "gif";
                String type = "pdf";
                // gv.increaseDpi();
                gv.decreaseDpi();
                gv.decreaseDpi();
                File out = new File(fileName+"."+ type); 
                gv.writeGraphToFile( gv.getGraph( gv.getDotSource(), type ), out );
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
		String fpath = "./rings-4_6.txt";
		APointAndRings gr = new APointAndRings(fpath);
		ParaCFG game = new ParaCFG(gr);
		System.out.println("ADJACENCY MATRIX: ");
		gr.printAdjMat();
		//System.out.println("PORTS ARE:");
		
		System.out.println("CONFIGS BY RINGS: ");
		gr.printConfigByRings();
		System.out.println("GAME PERIOD = " + game.computePeriod());
		game.printHistory();
		game.printfiringnodes();
		//System.out.println("JUST TO BE SURE, THIS IS THE NEXT CONFIG: ");
		//System.out.println(gr.configString());
	}
	
	public static void printUsage() {
		try {
			BufferedReader buf = new BufferedReader( new FileReader("../Usage.txt"));
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
		/** DEBUG 
		System.out.println("args.length = " + args.length);
		for ( int i = 0; i < args.length; i++ ) {
			System.out.print(args[i] + " ");
		}
		System.out.println();
		END DEBUG */
		
		if ( args.length < 1 || args.length > 3 ) {
			printUsage();
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
				if ( args[0].equals("--rings") ) {
					ringMode = true;
				}
				inpath = args[1];
				outpath = args[2];
				break;
			default:
				if ( args[0].equals("--rings") ) {
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
			if ( ringMode ) {
				System.out.println("MODE = rings");
			}
			else {
				System.out.println("MODE = generic");
			}
			System.out.println("INPUT FILE = " + inpath);
			//System.out.println("OUTPUT FILE = " + outpath);
			
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
			outbuf.write("" + period + "\n");
			outbuf.write("GAME CONFIGS:\n");
			for ( int i = 0; i < game.history.size(); i++ ) {
				outbuf.write(game.history.get(i) + '\n');
			}
			outbuf.write("FIRING NODES:\n");
			for ( int i = 0; i < game.firingnodes.size(); i++ ) {
				outbuf.write(game.firingnodes.get(i) + '\n');
			}
			outbuf.close();
			System.out.println("Results successfully written in file: " + outpath);
			if ( ringMode ) {
                                //construct successor firing digraph dot format
                                game.set_sfddot(period);
                                //generate pdf
                                createDotGraph(game.sfddot,outpath);
                                System.out.println("Successor firing graph written in file: " + outpath + ".pdf");
                        }
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}	
	}
	
	public static void main(String[] args) {
		//test();
		testRings();
		//run(args);
		//printUsage();
	}
}
