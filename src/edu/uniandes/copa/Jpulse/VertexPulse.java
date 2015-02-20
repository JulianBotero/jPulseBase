/**
 * This class represents a node, contains the pulse main logic.
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

import umontreal.iro.lecuyer.probdist.NormalDist;
import umontreal.iro.lecuyer.probdist.NormalDistQuick;

public class VertexPulse {

	// SP stuff
	public static final int infinity = (int) Double.POSITIVE_INFINITY;
	private EdgePulse reverseEdges;

	// This array contains the indexes for all the outgoing arcs from this node
	ArrayList<Integer> magicIndex;

	public int id;

	public VertexPulse[] left;
	public VertexPulse[] rigth;

	/**
	 * This matrix contains the sp bounds for each node. spMatrix(i,i)
	 * correspond to the sp from the end node to this node of the objective i.
	 * M_ij correspond to the objective consumptions of the objective j when the
	 * objective i is minimum.
	 */
	public int[][] spMatrix;

	public boolean[] inserted;

	// Labels
	private int labels[][];

	// Boolean that tells if the node is visited for first time
	boolean firstTime = true;

	private int usedLabels = 0;

	/**
	 * // Labels double LabelTime1; double LabelTime2; double LabelTime3; double
	 * LabelDist1; double LabelDist2; double LabelDist3;
	 * 
	 * // Boolean that tells if the node is visited for first time boolean
	 * firstTime = true; // Bounds to reach the end node int minDist; int
	 * maxTime; int minTime; int maxDist;
	 * 
	 * private VertexPulse leftDist; private VertexPulse rigthDist; private
	 * VertexPulse leftTime; private VertexPulse rigthTime; private boolean
	 * insertedDist; private boolean insertedTime;
	 */

	public VertexPulse(int iD) {
		id = iD;
		spMatrix = new int[DataHandler.num_attributes][DataHandler.num_attributes];
		left = new VertexPulse[DataHandler.num_attributes];
		rigth = new VertexPulse[DataHandler.num_attributes];
		inserted = new boolean[DataHandler.num_attributes];
		for (int i = 0; i < DataHandler.num_attributes; i++) {
			spMatrix[i][i] = infinity;
			inserted[i] = false;
			left[i] = this;
			rigth[i] = this;
		}

		labels = new int[DataHandler.numLabels][DataHandler.num_attributes];
		for (int k = 0; k < DataHandler.numLabels; k++) {
			for (int j = 0; j < DataHandler.num_attributes; j++) {
				labels[k][j] = infinity;
			}
		}
		magicIndex = new ArrayList<Integer>();
	}

	/**
	 * public VertexPulse(int i) { id = i; insertedDist = false; minDist =
	 * infinity; minTime = infinity;
	 * 
	 * maxTime = 0; maxDist = 0;
	 * 
	 * leftDist = this; rigthDist = this; leftTime = this; rigthTime = this;
	 * LabelTime1 = Double.POSITIVE_INFINITY; LabelDist1 =
	 * Double.POSITIVE_INFINITY; LabelTime2 = Double.POSITIVE_INFINITY;
	 * LabelDist2 = Double.POSITIVE_INFINITY; LabelTime3 =
	 * Double.POSITIVE_INFINITY; LabelDist3 = Double.POSITIVE_INFINITY;
	 * 
	 * magicIndex = new ArrayList<Integer>(); }
	 */
	public int getID() {
		return id;
	}

	public void addReversedEdge(EdgePulse e) {
		if (reverseEdges != null) {
			reverseEdges.addNextCommonTailEdge(e);
		} else
			reverseEdges = e;
	}

	public EdgePulse getReversedEdges() {
		// if(reverseEdges!= null){
		return reverseEdges;
		// }return new EdgePulse(1,1,0, this,this , -1);
	}

	public int getMinSP(int c) {
		return spMatrix[c][c];
	}

	public int getMaxDistSP() {
		int maxDist = 0;

		for (int i = 0; i < DataHandler.num_attributes; i++) {
			if (PulseGraph.vertexes[0].spMatrix[i][2] > maxDist) {
				maxDist = PulseGraph.vertexes[0].spMatrix[i][2];
			}
		}
		return maxDist;
	}

	/*
	 * public void setMinDist(int c){ minDist = c; }
	 * 
	 * public int getMinDist(){ return minDist; }
	 * 
	 * public void setMaxDist(int md){ maxDist = md; }
	 * 
	 * public int getMaxDist(){ return maxDist; }
	 * 
	 * public void setMaxTime(int mt){ maxTime = mt; }
	 * 
	 * public int getMaxTime(){ return maxTime; }
	 * 
	 * public void setMinTime(int t){ minTime = t; }
	 * 
	 * public int getMinTime(){ return minTime; }
	 * 
	 * public int getMinStDev() { return 0; }
	 */
	/**
	 * Unlink a vertex from the bucket
	 * 
	 * @return true, if the buckets gets empty
	 */
	/*
	 * public boolean unLinkVertexDist(){ if(rigthDist.getID() == id){
	 * leftDist=this; rigthDist=this; return true; }else{
	 * leftDist.setRigthDist(rigthDist); rigthDist.setLeftDist(leftDist);
	 * leftDist = this; rigthDist = this; return false; } } public boolean
	 * unLinkVertexTime(){ if(rigthTime.getID() == id){ leftTime=this;
	 * rigthTime=this; return true; }else{ leftTime.setRigthTime(rigthTime);
	 * rigthTime.setLeftTime(leftTime); leftTime = this; rigthTime = this;
	 * return false; } }
	 * 
	 * public void fastUnlinkDist(){ leftDist=this; rigthDist=this; } public
	 * void fastUnlinkTime(){ leftTime = this; rigthTime = this; } public void
	 * unlinkRighBoundDist() { rigthDist = null; } public void
	 * unlinkRighBoundTime() { rigthTime = null; }
	 */
	/**
	 * Insert a vertex in a bucket. New vertex is inserted on the left of the
	 * bucket entrance
	 * 
	 * @param v
	 *            vertex in progress to be inserted
	 */
	/*
	 * public void insertVertexDist(VertexPulse v) { v.setLeftDist(leftDist);
	 * v.setRigthDist(this); leftDist.setRigthDist(v); leftDist = v; }
	 * 
	 * public void insertVertexTime(VertexPulse v) { v.setLeftTime(leftTime);
	 * v.setRigthTime(this); leftTime.setRigthTime(v); leftTime = v; }
	 */
	/**
	 * Distance basic methods
	 */
	/*
	 * public void setLeftDist(VertexPulse v){ leftDist= v; } public void
	 * setRigthDist(VertexPulse v){ rigthDist= v; } public VertexPulse
	 * getBLeftDist(){ return leftDist; } public VertexPulse getBRigthDist(){
	 * return rigthDist; } public void setInsertedDist(){ insertedDist = true; }
	 * public boolean isInserteDist(){ return insertedDist; }
	 */
	/**
	 * Time basic methods
	 */
	/*
	 * public void setLeftTime(VertexPulse v){ leftTime= v; } public void
	 * setRigthTime(VertexPulse v){ rigthTime= v; } public VertexPulse
	 * getBLeftTime(){ return leftTime; } public VertexPulse getBRigthTime(){
	 * return rigthTime; } public void setInsertedTime(){ insertedTime = true; }
	 * public boolean isInsertedTime(){ return insertedTime; }
	 */
	public void reset() {
		for (int i = 0; i < DataHandler.num_attributes; i++) {
			inserted[i] = false;
		}
	}

	/**
	 * public void reset(){ insertedDist = false; }
	 */

	// This is the pulse procedure
	public void pulse(int PTime, int PDist, int PVar, double Alfa,
			ArrayList<Integer> path) {
		// if a node is visited for first time, sort the arcs
		if (this.firstTime) {
			this.firstTime = false;
			this.Sort(this.magicIndex);
			// left[0] = null;
			// rigth[0] = null;
			/**
			 * leftDist = null; rigthDist = null;
			 */
			reverseEdges = null;
		}

		// Label update
		changeLabels(PTime, PDist, PVar);
		// Check for cycles
		if (PulseGraph.Visited[id] == 0) {
			// Add the node to the path
			path.add(id);
			System.out.println("path: " + path);
			System.out.println("");
			// Update the visit indicator
			PulseGraph.Visited[id] = 1;
			// Pulse all the head nodes for the outgoing arcs
			for (int i = 0; i < magicIndex.size(); i++) {
				// Update distance and time
				int NewMean = 0;
				int NewDist = 0;
				int NewVar = 0;
				double NewTTB = 0;
				NewDist = (PDist + DataHandler.atributes[magicIndex.get(i)][0]);
				NewMean = (PTime + DataHandler.atributes[magicIndex.get(i)][1]);
				NewVar = PVar + DataHandler.atributes[magicIndex.get(i)][2];
				NewTTB = NormalDistQuick.inverseF(NewMean+ PulseGraph.vertexes[DataHandler.Arcs[magicIndex.get(i)][1]].getMinSP(1),Math.sqrt(NewVar+ PulseGraph.vertexes[DataHandler.Arcs[magicIndex.get(i)][1]].getMinSP(2)), Alfa);
				System.out.println("Arco: " + magicIndex.get(i) + ", CostAcum: "
						+ NewDist+ ", PrimalBoundC: "
								+ PulseGraph.PrimalBound
						+ ", MeanAcum: "
						+ NewMean
						+ ", VarAcum: "
						+ NewVar
						+ ", TTB:"
						+ NewTTB
						+ ", CotaTiempo: "
						+ PulseGraph.TimeC
						+ ", CotaIT: "
						+ PulseGraph.vertexes[DataHandler.Arcs[magicIndex.get(i)][1]].getMinSP(1)
						+ ", CotaIV: "
						+ PulseGraph.vertexes[DataHandler.Arcs[magicIndex.get(i)][1]].getMinSP(2));
				// Pruning strategies: infeasibility, bounds and labels
				if ((NewTTB <= PulseGraph.TimeC) && (NewDist + PulseGraph.vertexes[DataHandler.Arcs[magicIndex.get(i)][1]].getMinSP(0)) <= PulseGraph.PrimalBound // &&
				// !PulseGraph.vertexes[DataHandler.Arcs[magicIndex.get(i)][1]].CheckLabels(NewTTB,
				// NewDist, NewStDev)
				) {
					// If not pruned the pulse travels to the next head node
					PulseGraph.vertexes[DataHandler.Arcs[magicIndex.get(i)][1]].pulse(NewMean, NewDist, NewVar, Alfa, path);
				}
			}
			// Updates path and visit indicator for backtrack
			path.remove((path.size() - 1));
			PulseGraph.Visited[id] = 0;
		}
	}

	private void Sort(ArrayList<Integer> set) {
		QS(magicIndex, 0, magicIndex.size() - 1);
	}

	public int colocar(ArrayList<Integer> e, int b, int t) {
		int i;
		int pivote, valor_pivote;
		int temp;

		pivote = b;
		valor_pivote = PulseGraph.vertexes[DataHandler.Arcs[e.get(pivote)][1]]
				.getCompareCriteria();
		for (i = b + 1; i <= t; i++) {
			if (PulseGraph.vertexes[DataHandler.Arcs[e.get(i)][1]]
					.getCompareCriteria() < valor_pivote) {
				pivote++;
				temp = e.get(i);
				e.set(i, e.get(pivote));
				e.set(pivote, temp);
			}
		}
		temp = e.get(b);
		e.set(b, e.get(pivote));
		e.set(pivote, temp);
		return pivote;
	}

	public void QS(ArrayList<Integer> e, int b, int t) {
		int pivote;
		if (b < t) {
			pivote = colocar(e, b, t);
			QS(e, b, pivote - 1);
			QS(e, pivote + 1, t);
		}
	}

	/**
	 * public boolean CheckLabels( double PTime, double PDist) // Label pruning
	 * strategy { if((PTime >= LabelTime1 && PDist >= LabelDist1) || (PTime >=
	 * LabelTime2 && PDist >= LabelDist2) || (PTime >= LabelTime3 && PDist >=
	 * LabelDist3)){ return true; } return false; }
	 */

	private void changeLabels(int PTime, int PDist, double PStDev) {
		/**
		 * // Stores the best distance if (PDist <= LabelDist1) { LabelTime1 =
		 * PTime; LabelDist1 = PDist; // Stores the best time } else if (PTime
		 * <= LabelTime2) { LabelTime2 = PTime; LabelDist2 = PDist; // Replaces
		 * the third label } else { LabelTime3 = PTime; LabelDist3 = PDist; }
		 */
	}

	public int getCompareCriteria() {
		int suma = 0;
		for (int j = 0; j < DataHandler.num_attributes; j++) {
			suma += this.spMatrix[j][j];
		}
		return suma;
	}
	/**
	 * public int getCompareCriteria(){ return getMinDist(); }
	 */

}
