/**
 * This class represents a graph. It is used for the SP and the pulse!!!
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
import java.util.Collection;
import java.util.Set;

import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;

public class PulseGraph implements Graph<VertexPulse, EdgePulse> {
	// Attributes
	private static int[] C;
	// Nodes
	static VertexPulse[] vertexes;
	// Number of nodes
	private int numNodes;
	// Time constraint
	static double TimeC;
	// Primal bound
	static int PrimalBound;
	// The best solution found is globally stored here
	static ArrayList<Integer> Path;
	// The time for the best solution found (the distance is stored in the
	// primal bound)
	static double TimeStar;
	// Binary indicator to know if visiting a node creates a cycle
	static int[] Visited = new int[DataHandler.NumNodes];

	public PulseGraph(int numNodes) {
		super();
		this.numNodes = numNodes;
		C = new int[DataHandler.num_attributes];
		for (int i = 0; i < DataHandler.num_attributes; i++) {
			C[i] = 0;
		}

		vertexes = new VertexPulse[numNodes];
		Path = new ArrayList<Integer>();

	}

	@Override
	public EdgePulse addEdge(VertexPulse sourceVertex, VertexPulse targetVertex) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getNumNodes() {
		return numNodes;
	}

	public VertexPulse getVertexByID(int id) {
		return vertexes[id];
	}

	public EdgePulse addWeightedEdge(VertexPulse sourceVertex,
			VertexPulse targetVertex, int[] atris, int id) {
		for (int i = 0; i < DataHandler.num_attributes; i++) {
			if (atris[i] > C[i]) {
				C[i] = atris[i];
			}
		}

		vertexes[targetVertex.getID()].addReversedEdge(new EdgePulse(
				targetVertex, sourceVertex, id, atris));
		vertexes[sourceVertex.getID()].magicIndex.add(id);
		return null;
	}

	@Override
	public boolean addEdge(VertexPulse sourceVertex, VertexPulse targetVertex,
			EdgePulse e) {
		return false;
	}

	@Override
	public boolean addVertex(VertexPulse v) {
		vertexes[v.getID()] = v;
		return true;
	}

	public boolean addFinalVertex(FinalVertexPulse v) {
		vertexes[v.getID()] = v;
		return true;
	}

	@Override
	public boolean containsEdge(VertexPulse sourceVertex,
			VertexPulse targetVertex) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsEdge(EdgePulse e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsVertex(VertexPulse v) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<EdgePulse> edgeSet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<EdgePulse> edgesOf(VertexPulse vertex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<EdgePulse> getAllEdges(VertexPulse sourceVertex,
			VertexPulse targetVertex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EdgePulse getEdge(VertexPulse sourceVertex, VertexPulse targetVertex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EdgeFactory<VertexPulse, EdgePulse> getEdgeFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VertexPulse getEdgeSource(EdgePulse e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VertexPulse getEdgeTarget(EdgePulse e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getEdgeWeight(EdgePulse e) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean removeAllEdges(Collection<? extends EdgePulse> edges) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<EdgePulse> removeAllEdges(VertexPulse sourceVertex,
			VertexPulse targetVertex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeAllVertices(Collection<? extends VertexPulse> vertices) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public EdgePulse removeEdge(VertexPulse sourceVertex,
			VertexPulse targetVertex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeEdge(EdgePulse e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeVertex(VertexPulse v) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<VertexPulse> vertexSet() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getC(int obj) {
		return C[obj];
	}

	public void resetNetwork() {
		for (int i = 0; i < numNodes; i++) {
			vertexes[i].reset();
		}
	}

	public void SetConstraint(int timeC) {
		this.TimeC = timeC;
	}

	public void setPrimalBound(int bound) {
		this.PrimalBound = bound;
	}
}
