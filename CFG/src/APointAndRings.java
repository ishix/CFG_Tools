import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.ArrayList;

public class APointAndRings extends CFGGraph{
	// nodes[0] is the center node
	// the i-ring's nodes are indexed from portId[i] 
	// to portId[i] + ringLen[i] - 1
	protected int[] portId; // the nodes connected to the center
	protected int[] ringLen;
	
	// The first line contains the center's number of tokens
	// Each other line contains a ring's numbers of tokens, separated by a space
	APointAndRings( String fileName ){
		int nextId = 0, nbNodes = 0;
		ArrayList< int[] > ring_configs = new ArrayList< int[] >();
		try {
			BufferedReader buf = new BufferedReader( new FileReader(fileName) );
			String line = buf.readLine();
			
			// treat the center first
			int value = new Integer(line);
			CFGNode center = new CFGNode(nextId, value);
			nextId++;
			nbNodes++;
			
			// each subsequent line is the config of a ring
			while ( (line = buf.readLine()) != null ) {
				String[] words = line.split(" ");
				int[] config = new int[words.length];
				for ( int i = 0; i < words.length; i++ ) {
					config[i] = new Integer(words[i]);
				}
				ring_configs.add(config);
				nbNodes += config.length;
			}
			
			int nbRings = ring_configs.size();
			portId = new int[nbRings];
			ringLen = new int[nbRings];
			
			nodes = new CFGNode[nbNodes];
			activeNodes = new int[nbNodes];
			
			// add individual nodes to the graph
			nodes[0] = center;
			for ( int i = 0; i < nbRings; i++ ) {
				portId[i] = nextId;
				ringLen[i] = ring_configs.get(i).length;
				for ( int j = 0; j < ringLen[i]; j++ ) {
					CFGNode v = new CFGNode(nextId + j, ring_configs.get(i)[j]);
					nodes[nextId + j] = v;
				}
				nextId += ringLen[i];
			}
			
			// build the arcs
			for ( int i = 0; i < nbRings; i++ ) {
				// connect the port to the center node
				nodes[portId[i]].addNeighbour(nodes[0]);
				nodes[0].addNeighbour(nodes[portId[i]]);
				
				// connect the ring
				for ( int j = 0; j < ringLen[i] - 1; j++ ) {
					nodes[portId[i] + j].addNeighbour(nodes[portId[i] + j + 1]);
					nodes[portId[i] + j + 1].addNeighbour(nodes[portId[i] + j]);
				}
				nodes[portId[i] + ringLen[i] - 1].addNeighbour(nodes[portId[i]]);
				nodes[portId[i]].addNeighbour(nodes[portId[i] + ringLen[i] - 1]);
			}
			
			for ( int i = 0; i < nbNodes; i++ ) {
				activeNodes[i] = nodes[i].isActive();
			}
			
			buf.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* GET INFORMATION */
	int numberOfRings() {
		return portId.length;
	}
	
	int[] getRingLen() {
		return ringLen;
	}
	
	int[] getPortId() {
		return portId;
	}
	
	public void printConfigByRings() {
		System.out.println(nodes[0].tokens);
		for ( int i = 0; i < portId.length; i++ ) {
			for ( int j = 0; j < ringLen[i]; j++ ) {
				System.out.print(nodes[portId[i] + j].tokens + " ");	
			}
			System.out.println();
		}
	}
	/* END GET INFORMATION */
	
	public static void test() {
		String fpath = "/home/ishi/eclipse-workspace/CFG/rings-4_3.txt";
		CFGGraph g1 = new APointAndRings(fpath);
		System.out.println("ADJACENCY MATRIX: ");
		g1.printAdjMat();
		System.out.println("INITIAL CONFIG: ");
		System.out.println(g1.configString());
	}

        /* construct the successor firing digraph dot format */
        
        public String get_sfddot(int period, ArrayList< int[] > firingnodeshistory){
                String sfddot="v; ";
                int endfnh = firingnodeshistory.size()-1;
                for ( int i = 0; i < getPortId().length; i++ ){
                        int arc=0;
                        //arcs in rings
                        for ( int j =0; j < ringLen[i]; j++ ){
                                sfddot+="v_"+i+"_"+j+"; ";
                                //check arc v_i_j+1 -> v_i_j
                                arc=1;
                                for ( int k=0; k < period; k++ ){
                                        if (firingnodeshistory.get(endfnh-k)[portId[i]+j]==1 && firingnodeshistory.get((endfnh-k-1 >= 0) ? endfnh-k-1 : endfnh)[portId[i]+((j+1)%(ringLen[i]))]==0){
                                                arc=0;
                                                break;
                                        }
                                }
                                if (arc==1){
                                        //add arc v_i_j+1 -> v_i_j
                                        sfddot+="v_"+i+"_"+((j+1)%ringLen[i])+" -> v_"+i+"_"+j+"; ";
                                }
                                //check arc v_i_j -> v_i_j+1
                                arc=1;
                                for ( int k=0; k < period; k++ ){
                                        if (firingnodeshistory.get(endfnh-k)[portId[i]+((j+1)%(ringLen[i]))]==1 && firingnodeshistory.get((endfnh-k-1 >= 0) ? endfnh-k-1 : endfnh)[portId[i]+j]==0){
                                                arc=0;
                                                break;
                                        }
                                }
                                if (arc==1){
                                        //add arc v_i_j -> v_i_j+1
                                        sfddot+="v_"+i+"_"+j+" -> v_"+i+"_"+((j+1)%ringLen[i])+"; ";
                                }
                        }
                        //check arc v -> v_i_0
                        arc=1;
                        for ( int k=0; k < period; k++ ){
                                if (firingnodeshistory.get(endfnh-k)[portId[i]]==1 && firingnodeshistory.get((endfnh-k-1 >= 0) ? endfnh-k-1 : endfnh)[0]==0){
                                        arc=0;
                                        break;
                                }
                        }
                        if (arc==1){
                                //add arc v -> v_i_0
                                sfddot+="v -> v_"+i+"_0; ";
                        }
                        //check arc v_i_0 -> v
                        arc=1;
                        for ( int k=0; k < period; k++ ){
                                if (firingnodeshistory.get(endfnh-k)[0]==1 && firingnodeshistory.get((endfnh-k-1 >= 0) ? endfnh-k-1 : endfnh)[portId[i]]==0){
                                        arc=0;
                                        break;
                                }
                        }
                        if (arc==1){
                                //add arc v_i_0 -> v
                                sfddot+="v_"+i+"_0 -> v; ";
                        }
                        
                }
                return sfddot;
        }
}
