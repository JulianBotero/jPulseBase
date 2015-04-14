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
	/**
	 * This matrix contains the sp bounds for each node. spMatrix(i,i)
	 * correspond to the sp from the end node to this node of the objective i.
	 * M_ij correspond to the objective consumptions of the objective j when the
	 * objective i is minimum.
	 */
	public int[][] spMatrix;

	public boolean[] inserted;

	public VertexPulse[] left;
	public VertexPulse[] rigth;

	// Labels
	private double labels[][];

	// Boolean that tells if the node is visited for first time
	boolean firstTime = true;

	private int usedLabels = 0;

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

		labels = new double[DataHandler.numLabels][DataHandler.num_attributes + 1];
		for (int k = 0; k < DataHandler.numLabels; k++) {
			for (int j = 0; j < DataHandler.num_attributes + 1; j++) {
				labels[k][j] = infinity;
			}
		}
		magicIndex = new ArrayList<Integer>();
	}

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

	public void reset() {
		for (int i = 0; i < DataHandler.num_attributes; i++) {
			inserted[i] = false;
		}
	}

	/**
	 * public void reset(){ insertedDist = false; }
	 */

	// This is the pulse procedure
	public void pulse(int PCost, int PMean, int PVar, double Alfa,
			ArrayList<Integer> path) {
		// if a node is visited for first time, sort the arcs
		if (this.firstTime) {
			this.firstTime = false;
			this.Sort(this.magicIndex);
			reverseEdges = null;
		}

		// Label update
		double PTTBl = NormalDistQuick.inverseF(PMean, Math.sqrt(PVar), Alfa);
		changeLabels(PCost, PMean, PVar, PTTBl);
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
				int NewCost = 0;
				int NewMean = 0;
				int NewVar = 0;
				double NewTTBp = 0;
				double NewTTBi = 0;
				NewCost = (PCost + DataHandler.atributes[magicIndex.get(i)][0]);
				NewMean = (PMean + DataHandler.atributes[magicIndex.get(i)][1]);
				NewVar = PVar + DataHandler.atributes[magicIndex.get(i)][2];
				NewTTBp = NormalDistQuick.inverseF(NewMean, Math.sqrt(NewVar),
						Alfa);
				NewTTBi = NormalDistQuick
						.inverseF(
								NewMean
										+ PulseGraph.vertexes[DataHandler.Arcs[magicIndex
												.get(i)][1]].getMinSP(1),
								Math.sqrt(NewVar
										+ PulseGraph.vertexes[DataHandler.Arcs[magicIndex
												.get(i)][1]].getMinSP(2)), Alfa);
				
				System.out.println("Arco: "
						+ magicIndex.get(i)
						+ ", CostAcum: "
						+ NewCost
						+ ", PrimalBoundC: "
						+ PulseGraph.PrimalBound
						+ ", MeanAcum: "
						+ NewMean
						+ ", VarAcum: "
						+ NewVar
						+ ", TTB:"
						+ NewTTBi
						+ ", CotaTiempo: "
						+ PulseGraph.TimeC
						+ ", CotaIT: "
						+ PulseGraph.vertexes[DataHandler.Arcs[magicIndex
								.get(i)][1]].getMinSP(1)
						+ ", CotaIV: "
						+ PulseGraph.vertexes[DataHandler.Arcs[magicIndex
								.get(i)][1]].getMinSP(2));
				// Pruning strategies: infeasibility, bounds and dominance
				if ((NewTTBi <= PulseGraph.TimeC)
						&& (NewCost + PulseGraph.vertexes[DataHandler.Arcs[magicIndex
								.get(i)][1]].getMinSP(0)) < PulseGraph.PrimalBound
						&& !CheckLabels(NewCost, NewMean, NewVar, NewTTBp, i,Alfa)) {
					// If not pruned the pulse travels to the next head node
					PulseGraph.vertexes[DataHandler.Arcs[magicIndex.get(i)][1]]
							.pulse(NewCost, NewMean, NewVar, Alfa, path);
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

	public boolean CheckLabels(int NewCost, int NewMean, int NewVar,
			double NewTTBp, int i, double Alfa) {
		if (PulseGraph.vertexes[DataHandler.Arcs[magicIndex.get(i)][1]]
				.CheckLabels1(NewCost, NewMean, NewVar, NewTTBp)) {
			return true;
		} else if (PulseGraph.vertexes[DataHandler.Arcs[magicIndex.get(i)][1]]
				.CheckLabels2(NewCost, NewMean, NewVar, NewTTBp, Alfa)) {
			return true;
		}
		return false;
	}

	// Dominance pruning checking 1, True if Label dominates pulse
	public boolean CheckLabels1(int NewCost, int NewMean, int NewVar,
			double NewTTBp) {
		for (int i = 0; i < DataHandler.numLabels; i++) {
			if (NewCost >= labels[i][0] && NewMean >= labels[i][1]
					&& NewTTBp >= labels[i][2]) {
				return true;
			}
		}
		return false;
	}

	// Dominance pruning checking 2
	public boolean CheckLabels2(int PCost, int PMean, int PVar, double PTTB, double Alfa) {
		// TODO : Revisar ubicación
		for (int i = 0; i < DataHandler.numLabels; i++) {
			double za =NormalDistQuick.inverseF01(Alfa);
			double LMean = labels[i][1];
			double LVar = labels[i][2];
			double VarInter = Math.pow((LMean - PMean) / za ,4) - 2
					* Math.pow((LMean - PMean) / za , 2) * (PVar + LVar)
					+ Math.pow(PVar + LVar , 2) - 4 * (PVar * LVar)
					/ (4 * Math.pow((LMean - PMean) / za , 2));
					
			if (PCost >= labels[i][0] && PMean >= labels[i][1]
					&& PTTB <= labels[i][2] && getMinSP(2) >= VarInter) {
				return true;
			}
		}
		return false;
	}

	private void changeLabels(int NewCost, int NewMean, int NewVar,
			double NewTTBl) {
		/**
		 * labels[label][0] = NewCost; labels[label][1] = NewMean;
		 * labels[label][2] = NewVar; labels[label][3] = NewTTBl;
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
