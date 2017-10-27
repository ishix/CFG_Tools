
public class CFGRing extends CFGGraph{
	
	public CFGRing( int[] config, int firstId ) {
		int n = config.length;
		CFGNode[] v = new CFGNode[n];
		// create nodes and put tokens into nodes
		for (int i = 0; i < n; i++) {
			v[i] = new CFGNode(firstId, config[i]);
			firstId++;
		}
		// add arcs
		for ( int i = 0; i < n - 1; i++ ) {
			v[i].addNeighbour(v[i + 1]);
			v[i + 1].addNeighbour(v[i]);
		}
		v[0].addNeighbour(v[n - 1]);
		v[n - 1].addNeighbour(v[0]);
		
		nodes = v;
		activeNodes = new int[v.length];
		for ( int i = 0; i < v.length; i++ ) {
			activeNodes[i] = v[i].isActive();
		}
	}

}
