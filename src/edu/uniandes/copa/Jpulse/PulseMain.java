/**
 * This is the main class for the pulse algorithm.
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
 * Julian Botero
 * 
 * 
 */

package edu.uniandes.copa.Jpulse;

import java.io.IOException;
import java.util.ArrayList;

public class PulseMain {

	public static void main(String[] args) throws IOException,
			InterruptedException {

		// Read a config file with the instance information
		String ini = "Config-Test-NormDistr.txt";
		Settings Instance;
		Instance = new Settings(ini);
		// Read the data file and store the data on a DataHandler
		DataHandler data = new DataHandler(Instance, Instance.numObjs);// Num
																		// Attributes
		data.ReadDimacs();
		// Create the network and set the time constraint
		PulseGraph network = data.getGd();

		// Set time Constraint
		network.SetConstraint(Instance.TimeC);
		
		// Begin the time count
		double Atime = System.nanoTime();
		// SP algorithm for each weight/objective
		DIKBD[] spAlgo = new DIKBD[Instance.numObjs];
		Thread[] tSp = new Thread[Instance.numObjs];
		for (int i = 0; i < spAlgo.length; i++) {
			spAlgo[i] = new DIKBD(network, Instance.LastNode - 1, i);
			tSp[i] = new Thread(new ShortestPathTask(i, spAlgo[i]));
		}

		for (int i = 0; i < spAlgo.length; i++) {
			tSp[i].start();
		}

		for (int i = 0; i < spAlgo.length; i++) {
			tSp[i].join();
		}

		// MD is the distance for the best time path
		/**
		 * int MD=network.getVertexByID(Instance.Source-1).getMaxDistSP(); int
		 * MD = (int) Double.POSITIVE_INFINITY;
		 */
		// TODO: Set the first primal bound
		int MD = network.getVertexByID(Instance.Source - 1).getMaxCostSP();
		System.out.println("Costo Primal: " + MD);
		network.setPrimalBound(MD);

		// /////////////////////////////////// PULSE
		// /////////////////////////////////////////////////////////////
		// Create an empty path
		ArrayList<Integer> Path = new ArrayList<Integer>();
		// Pulse the origin node
		network.getVertexByID(Instance.Source - 1).pulse(0, 0, 0,
				Instance.Alfa, Path);
		// Report the results
		System.out.println("");
		System.out
				.println("-------------------------------------------------------------");
		System.out.println("EXECUTION TIME: " + (System.nanoTime() - Atime)
				/ 1000000000);
		System.out
				.println("***************OPTIMAL SOLUTION*****************************");
		System.out.println("Cost: " + network.PrimalBound);
		System.out.println("Time: " + network.TimeStar);
		System.out.println("Optimal path: " + network.Path);
	}
}
