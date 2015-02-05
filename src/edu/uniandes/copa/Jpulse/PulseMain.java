/**
 * This is the main class for the pulse algorithm.
 * 
 * Ref.: Lozano, L. and Medaglia, A. L. (2013). 
 * On an exact method for the constrained shortest path problem. Computers & Operations Research. 40 (1):378-384.
 * DOI: http://dx.doi.org/10.1016/j.cor.2012.07.008 
 * 
 * 
 * @author L. Lozano & D. Duque
 * @affiliation Universidad de los Andes - Centro para la Optimizaci�n y Probabilidad Aplicada (COPA)
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
		DataHandler data = new DataHandler(Instance, Instance.numObjs);// 3 =
																		// Number
																		// of
																		// attributes
		data.ReadDimacs();
		// Create the network and set the time constraint
		PulseGraph network = data.getGd();

		// TODO
		network.SetConstraint(Instance.TimeC);
		/**
		 * // Create two threads and run parallel SP for the initialization
		 * Thread tTime = new Thread(); Thread tDist = new Thread();
		 */
		// Begin the time count
		double Atime = System.nanoTime();
		// Reverse the network and run SP for distance and time
		/**
		 * DukqstraDist spDist = new DukqstraDist(network, Instance.LastNode-1);
		 * DukqstraTime spTime = new DukqstraTime(network, Instance.LastNode-1);
		 * tDist = new Thread(new ShortestPathTask(1, spDist, null)); tTime =
		 * new Thread(new ShortestPathTask(0, null, spTime)); tDist.start();
		 * tTime.start(); tDist.join(); tTime.join();
		 * 
		 * // MD is the distance for the best time path int
		 * MD=network.getVertexByID(Instance.Source-1).getMaxDist(); // Set the
		 * first primal bound network.setPrimalBound(MD);
		 */
		// We need one SP algorithm for each weight/objective
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

		/**
		 * network.vertexes = null; data.Arcs = null; data.atributes = null;
		 * data = null; network = null;
		 */

		// /////////////////////////////////// PULSE
		// /////////////////////////////////////////////////////////////
		// Create an empty path
		ArrayList<Integer> Path = new ArrayList<Integer>();
		// Pulse the origin node
		network.getVertexByID(Instance.Source - 1).pulse(0, 0,0, Path);
		// Report the results
		System.out.println("EXECUTION TIME: " + (System.nanoTime() - Atime)
				/ 1000000000);
		System.out
				.println("***************OPTIMAL SOLUTION*****************************");
		System.out.println("Distance: " + network.PrimalBound);
		System.out.println("Time: " + network.TimeStar);
		System.out.println("Optimal path: " + network.Path);

	}
}
