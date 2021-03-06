import java.util.Collection;
import java.util.TreeSet;

public class CFGNode implements Comparable<CFGNode> {
	protected int id;
	protected TreeSet<CFGNode> neighbours; // TreeSet over ArrayList because duplicates are not allowed
	protected int degree;
	protected int tokens;
	
	/** Constructors */
	public CFGNode(int newId){
		id = newId;
		neighbours = new TreeSet<CFGNode>();
		degree = 0;
		tokens = 0;
	}
	
	/* Create a node with n tokens */
	public CFGNode(int newId, int nTokens) {
		id = newId;
		neighbours = new TreeSet<CFGNode>();
		degree = 0;
		tokens = nTokens;
	}
	
	public CFGNode(int newId, int nTokens, Collection<CFGNode> nodes) {
		id = newId;
		neighbours = new TreeSet<CFGNode>(nodes);
		degree = nodes.size();
		tokens = nTokens;
	}
	
	/** Modifiers */
	public void setTokens(int n) {
		tokens = n;
	}
	
	public void addTokens(int n) {
		tokens += n;
	}
	
	public boolean addNeighbour(CFGNode v) {
		if ( neighbours.add(v) ) {
			degree++;
			return true;
		}
		else {
			return false;
		}
	}
	
	/** firing */
	public int isActive() {
		return ( tokens >= degree ) ? (tokens / degree) : 0;
	}
	
	/* Gives one chip to each neighbour */
	public void fire() {
		//int k = isActive();
		if ( isActive() > 0 ) {
			//System.out.println(id + " is active: " + tokens + " >= " + degree);
			for ( CFGNode v : neighbours ) {
				v.addTokens(1);
			}
			tokens -= degree;
		}
		else {
			//System.out.println(id + " is not active: " + tokens + " < " + degree);
		}
	}
	
	/** test if there is an arc between two vertices */
	public boolean hasNeighbour(CFGNode v) {
		return neighbours.contains(v);
	}

	
	@Override
	public int compareTo(CFGNode o) {
		if ( id > o.id ) {
			return 1;
		}
		else if ( id < o.id ) {
			return -1;
		}
		else {
			return 0;
		}
	}
	

}
