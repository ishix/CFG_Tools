import java.io.BufferedReader;
import java.io.FileReader;

public class CFGGraph {
	protected CFGNode[] nodes;
	protected int[] activeNodes;
	
	/* Constructors */	
	public CFGGraph() {
		nodes = null;
		activeNodes = null;
	}
	
	public CFGGraph( CFGNode[] v ) {
		nodes = v;
		activeNodes = new int[v.length];
		for ( int i = 0; i < v.length; i++ ) {
			activeNodes[i] = v[i].isActive();
		}
	}
	
	public CFGGraph( String fileName ) {
		try {
			BufferedReader buf = new BufferedReader( new FileReader( fileName ));
			String line;
			
			// first line contains the graph's size
			line = buf.readLine();
			int n = (new Integer(line)).intValue();
			
			// add nodes
			CFGNode[] v = new CFGNode[n];
			for ( int i = 0; i < n; i++ ) {
				v[i] = new CFGNode(i);
			}
			
			// add arcs
			int rowCount = 0, colCount = 0;
			while ( (line = buf.readLine()) != null ) {
				rowCount++;
								 
				String[] words = line.split(" ");
				colCount = words.length;
								
				// safety check
				if ( colCount != n ) {
					System.out.println("Bad matrix file format: number of rows unmatched!");
					nodes = null;
					activeNodes = null;
					buf.close();
					return;
				}
				
				for (int i = 0; i < colCount; i++ ) {
					int value = (new Integer(words[i])).intValue();
					if ( value != 0 ) {
						v[rowCount - 1].addNeighbour(v[i]);
					}
				}
			}
			if ( rowCount != n ) {
				System.out.println("Bad matrix file format: number of rows unmatched!");
				nodes = null;
				activeNodes = null;
			}
			else {
				nodes =  v;
				activeNodes = new int[v.length];
				for ( int i = 0; i < v.length; i++ ) {
					activeNodes[i] = v[i].isActive();
				}
			}
			buf.close();
		}
		catch ( Exception e ) {			
			e.printStackTrace();
		}
	}
	/* END CONSTRUCTORS */
	
	
	/* GET INFORMATION */
	/* get number of nodes */
	public int size() {
		return nodes.length;
	}
	
	/* get number of chips */
	public int nChips() {
		int n = 0;
		for ( CFGNode v : nodes ) {
			n += v.tokens;
		}
		return n;
	}
	
	/* print the graph in adj matrix form */
	public void printAdjMat() {
		String tmp = "";
		for ( CFGNode v : nodes ) {
			for ( CFGNode w : nodes ) {
				tmp += (v.hasNeighbour(w)) ? "1 " : "0 ";
			}
			System.out.println(tmp);
			tmp = "";
		}
	}
	
	/* print current chip configuration */
	public String configString() {
		String tmp = "";
		for (int i = 0; i < nodes.length; i++ ) {
			tmp += nodes[i].tokens + " ";
		}
		return tmp;
	}
	/* END GET INFORMATION */
	
	/* MODIFICATIONS */
	/* give some tokens to a node */
	public boolean setTokens(int nodeId, int nTokens) {
		if ( nodeId < 0 || nodeId > nodes.length ) {
			System.out.println("CFGGraph.setTokens: Node number out of range: " + nodeId + ". No modification made.");
			return false;
		}
		else {
			nodes[nodeId].setTokens(nTokens);
			activeNodes[nodeId] = nodes[nodeId].isActive();
			return true;
		}
	}
	
	/* give some tokens to a node */
	public boolean addTokens(int nodeId, int nTokens) {
		if ( nodeId < 0 || nodeId > nodes.length ) {
			System.out.println("CFGGraph.addTokens: Node number out of range: " + nodeId + ". No modification made.");
			return false;
		}
		else {
			nodes[nodeId].addTokens(nTokens);
			activeNodes[nodeId] = nodes[nodeId].isActive();
			return true;
		}
	}

	/* set new configuration */
	public boolean setConfig(int[] config) {
		if ( config.length != nodes.length ) {
			System.out.println("CFGGraph.setConfig: Bad config length. No modification made.");
			return false;
		}
		else {
			for ( int i = 0; i < config.length; i++ ) {
				nodes[i].setTokens(config[i]);
				activeNodes[i] = nodes[i].isActive();
			}
			return true;
		}
	}
	
	/* update active list */
	public void updateActive() {
		for ( int i = 0; i < nodes.length; i++ ) {
			activeNodes[i] = nodes[i].isActive();
		}
	}
	
	/* fire a single node */
	public void fireANode(int nodeId) {
		if ( nodeId < 0 || nodeId > nodes.length ) {
			System.out.println("CFGGraph.fireANode: Node number out of range: " + nodeId + ". No modification made.");
		}
		else {
			nodes[nodeId].fire();
		}
		updateActive();
	}
	
	/* fire all nodes that can fire */
	public void fireAll() {
		for ( int i = 0; i < nodes.length; i++ ) {
			nodes[i].fire();
		}
		updateActive();
	}
	
	/* END MODIFICATIONS */
	
	// TESTING TIME !
	public static void main(String[] args) {
		String fpath = "/home/ishi/eclipse-workspace/CFG/star-3.txt";
		CFGGraph gr = new CFGGraph(fpath);
		//System.out.println(gr.nodes == null);
		System.out.println("ADJACENCY MATRIX: ");
		gr.printAdjMat();
		System.out.println("INITIAL CONFIG: ");
		System.out.println(gr.configString());
		int[] aconfig = {5, 0, 0, 0};
		gr.setConfig(aconfig);
		System.out.println("New CONFIG: ");
		System.out.println(gr.configString());
		System.out.println("NOW FIRE ALL FIRABLE NODES:");
		gr.fireAll();
		System.out.println(gr.configString());
		System.out.println("NOW FIRE ALL FIRABLE NODES:");
		gr.fireAll();
		System.out.println(gr.configString());
	}
}
