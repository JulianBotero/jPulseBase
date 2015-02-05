/**
 * Data structure for an edge used for the SP implementation (not for the pulse!)
 * 
 * Ref.: Lozano, L. and Medaglia, A. L. (2013). 
 * On an exact method for the constrained shortest path problem. Computers & Operations Research. 40 (1):378-384.
 * DOI: http://dx.doi.org/10.1016/j.cor.2012.07.008 
 * 
 * 
 * @author D. Duque
 * @affiliation Universidad de los Andes - Centro para la Optimización y Probabilidad Aplicada (COPA)
 * @url http://copa.uniandes.edu.co/
 * 
 */

package edu.uniandes.copa.Jpulse;

public class EdgePulse {
	/**
	private int eDist;
	private int eTime;
	private int eStDev;
	*/
	
	private EdgePulse nextE;
	private int id;
	private VertexPulse source;
	private VertexPulse target;
	private int[] atributes; 
	
	public EdgePulse(VertexPulse nT, VertexPulse nH, int nid, int[] atri) {
		
		this.source = nT;
		this.target = nH;
		this.id = nid;
		this.atributes = atri;
	}
	/**
	public EdgePulse(int d , int t, int sd,  VertexPulse nT, VertexPulse nH, int nid) {
		// TODO Auto-generated constructor stub
		eDist = d;
		eTime = t;
		eStDev= sd;
		this.source = nT;
		this.target = nH;
		this.id = nid;
	}
	*/
	
	public void addNextCommonTailEdge(EdgePulse e){
		if(nextE!= null){
			nextE.addNextCommonTailEdge(e);
		}
		else{
			nextE = e;
		}
	}

	
	public EdgePulse getNext()
	{
		return nextE;
	}
	public void setNextE(EdgePulse e ){
		nextE = e;
	}
	public int getWeight(int obj){
		return atributes[obj];
	}
	/**
	public int getWeightDist(){
		return eDist;
	}
	public int getWeightTime(){
		return eTime;
	}
	public int getWeightStDev(){
		return eStDev;
	}
	*/
	public VertexPulse getSource(){
		return source;
	}
	
	public VertexPulse getTarget(){
		return target;
	}
	public int getID()
	{
		return id;
	}
	public EdgePulse findEdgebyTarget(VertexPulse targetT)
	{
		if(targetT.getID() == this.target.getID())
		{
			return this;
		}
		else{
			if(nextE!= null)
			{
				return nextE.findEdgebyTarget(targetT);
			}
		}
		return null;
	}
	
	public int getCompareCriteria(){
		int suma = 0;
		for (int i = 0; i < DataHandler.num_attributes; i++) {
			suma += target.spMatrix[i][i];
		}
		return suma;
	}
	/**
	public int getCompareCriteria(){
		return target.getMinDist() + target.getMinTime();
	}
	*/
	
}
