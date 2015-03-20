/**
 * This class represents the final node in a network. The pulse procedure is overrided!
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

import java.util.ArrayList;

import umontreal.iro.lecuyer.probdist.NormalDistQuick;

public class FinalVertexPulse extends VertexPulse {
	
	// Node id
	public int id;
	
	// SP stuff
	private EdgePulse reverseEdges;
	/**
	private VertexPulse bLeft;
	private VertexPulse bRigth;
	
	// Bounds
	int minDist;
	int maxTime;
	int minTime;
	int maxDist;
	*/
	
	/**
	 * This matrix contains the sp bounds for each node M_ii correspond to the
	 * sp from the end node to this node of the objective i. M_ij correspond to
	 * the objective consumptions of the objective j when the objective i is
	 * minimum;
	 */
	public int[][] spMatrix;
	private PulseGraph pg;
	private boolean [] inserted;
	
	/**
	int c=0;
	int d=0;
	int sd=0;
	*/
	
	public FinalVertexPulse(int iD, PulseGraph npg) {
		super(iD);
		pg= npg;
		id = iD;
		spMatrix = new int[DataHandler.num_attributes][DataHandler.num_attributes];
		inserted = new boolean[DataHandler.num_attributes];
		for (int i = 0; i < DataHandler.num_attributes; i++) {
			spMatrix[i][i] = infinity;
			inserted[i] = false;
		}
	}

	
	public int  getID()
	{
		return id;
	}
	
	public void addReversedEdge(EdgePulse e)
	{
		if(reverseEdges!=null){
			reverseEdges.addNextCommonTailEdge(e);
		}else
			reverseEdges = e;
	}
	
	
	public EdgePulse findEdgeByTarget(VertexPulse target){
		if(reverseEdges!=null){
			reverseEdges.findEdgebyTarget(target);
		}
		return null;
	}
	public EdgePulse getReversedEdges() {
		//if(reverseEdges!= null){
			return reverseEdges;
		//}return new EdgePulse(1,1,0, this,this , -1);
	}
	
	
	public void pulse(int PCost,int PMean, int PVar, double Alfa,ArrayList<Integer> path) {
		// Add node id to path
		path.add(id);
		double TTB = NormalDistQuick.inverseF(PMean,Math.sqrt(PVar), Alfa);
		// If the path is feasible and updates the primal bound the information on the best solution found is updated
		if(PCost<=PulseGraph.PrimalBound && TTB<=PulseGraph.TimeC){
			
			// Update the best solution known
			PulseGraph.Path.clear();
			PulseGraph.Path.addAll(path);
			PulseGraph.TimeC=TTB;
			PulseGraph.PrimalBound=PCost;
			
		}
		// Remove the node id to backtrack
		path.remove((path.size()-1));
	}

	
}
