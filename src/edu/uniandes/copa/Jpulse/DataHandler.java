/**
 * This is a class that holds all the relevant data for an instance.
 * 
 * Ref.: Lozano, L. and Medaglia, A. L. (2013). 
 * On an exact method for the constrained shortest path problem. Computers & Operations Research. 40 (1):378-384.
 * DOI: http://dx.doi.org/10.1016/j.cor.2012.07.008 
 * 
 * 
 * @author L. Lozano & D. Duque
 * @affiliation Universidad de los Andes - Centro para la Optimización y Probabilidad Aplicada (COPA)
 * @url http://copa.uniandes.edu.co/
 * 
 */
package edu.uniandes.copa.Jpulse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.StringTokenizer;

public class DataHandler {
	
	//Number of labels per node for dominance
	public static final int numLabels = 3;
	// Constant that stays the number of attributes per arc
	public static int num_attributes;
	// Name of the instance
	String CvsInput;
	// Number of arcs
	int NumArcs;
	// Destinantion node
	int LastNode;
	// Source node
	int Source;
	// Number of nodes
	static int NumNodes;
		// All the arcs in the network stored in a vector where Arcs[i][0]= Tail for arc i and Arcs[i][1]= Head for arc i 
	static int[][] Arcs;
	
	//Attributes for each arc
	static int[][] atributes;
	// Data structure for storing the graph
	private PulseGraph Gd;
	
	//static Random r = new Random(0);
	//private int seed;
	
	// The distance attribute for any arc i
	//static int[] Distance;
	// The time attribute for any arc i
	//static int[] Time;
	// The time attribute for any arc i
	//static int[] StDev;
	
	
	// Read data from an instance
	public DataHandler(Settings Instance, int n_attributess) {
		num_attributes = n_attributess;
		CvsInput = Instance.DataFile;
		NumArcs = Instance.NumArcs;
		NumNodes = Instance.NumNodes;
		LastNode = Instance.LastNode;
		Source = Instance.Source;
		//seed = Instance.seed;//System.out.println(seed+"sedd");
		Arcs = new int[Instance.NumArcs][2];
		atributes = new int[Instance.NumArcs][num_attributes];
		//Distance = new int[Instance.NumArcs];
		//Time = new int[Instance.NumArcs];
		//StDev = new int[Instance.NumArcs];
		Gd = new PulseGraph(NumNodes);
	}

		
// This procedure creates the nodes for the graph
	public void upLoadNodes(){
		// All nodes are VertexPulse except the final node
		for (int i = 0; i < NumNodes; i++) {
			if(i!=(LastNode-1)){
				Gd.addVertex(new VertexPulse(i) );
			}
		}
		// The final node is a FinalVertexPulse 
		FinalVertexPulse vv = new FinalVertexPulse(LastNode-1);
		Gd.addFinalVertex(vv);
	}
	
	// This procedure returns a graph
	public PulseGraph getGd()
	{
		return Gd;
	}

	// This procedure reads data from a data file in DIMACS format
	public void ReadDimacs() throws NumberFormatException, IOException {
		//Random weithgen = new Random(seed);
		
		File file = new File(CvsInput);

		BufferedReader bufRdr = new BufferedReader(new FileReader(file));
		String line = null;

		String[] readed = new String[5];

		int row = 0;
		int col = 0;
		
		upLoadNodes();
		
		while ((line = bufRdr.readLine()) != null && row < NumArcs + 1) {
			StringTokenizer st = new StringTokenizer(line, " ");
			while (st.hasMoreTokens()) {
				// get next token and store it in the array
				readed[col] = st.nextToken();
				col++;
			}

			if (row >= 1) {
				Arcs[row - 1][0] = (Integer.parseInt(readed[1]) - 1); // readed[0]
				Arcs[row - 1][1] = (Integer.parseInt(readed[2]) - 1); // readed[1]
				
				int[] atris = new int[num_attributes];
				for (int i = 1; i < num_attributes; i++) { //int i = 0
					atris[i] = Integer.parseInt(readed[2 + i]);
					atributes[row - 1][i] = atris[i];
				}
				//Distance[row - 1] = Integer.parseInt(readed[3]);
				//Time[row - 1] = Integer.parseInt(readed[4]);
				//StDev[row - 1] = Integer.parseInt(readed[5]);
				
				// Add edges to the network
				/**
				 * Gd.addWeightedEdge( Gd.getVertexByID(Arcs[row - 1][0]), Gd.getVertexByID(Arcs[row - 1][1]),Distance[row - 1], Time[row - 1] ,StDev[row - 1],row-1);
				*/
				Gd.addWeightedEdge( Gd.getVertexByID(Arcs[row - 1][0]), Gd.getVertexByID(Arcs[row - 1][1]),atris , row-1);
			}

			col = 0;
			row++;

		}
		
	}
	
}

