import org.jgrapht.*;
import org.jgrapht.alg.*;
import org.jgrapht.graph.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;

public class KMeansClusterer
{
	private UndirectedGraph<Vertex, DefaultEdge> inputGraph;
	private Vertex[] inputVertexSet;
	private DefaultEdge[] inputEdgeSet;
	private int noOfClusters = 20; // "k"
	private double degreeMeanChangeThresh = 0.1;
	private double neighbourMeanChangeThresh = 0.1;
	private ArrayList<ArrayList<Vertex>> clusters;
	private String outputFilename;
	private String stabilizingMeansFilename;
	private PrintWriter outputWriter;
	private PrintWriter stabilizingMeansWriter;
	private String stabilizingMeansChange;
	
	/* constructor method */
	public KMeansClusterer(UndirectedGraph<Vertex, DefaultEdge> requiredGraph,
			                   String requiredOutputFilename, 
			                   String requiredstabilizingMeansFilename,
			                   int requiredNoOfClusters) throws Exception
	{
		if (noOfClusters <= 0)
			throw new Exception("\nThe number of clusters must be >= 1)");
			
		inputGraph = requiredGraph;
		inputVertexSet = inputGraph.vertexSet().toArray(new Vertex[0]);
		inputEdgeSet = inputGraph.edgeSet().toArray(new DefaultEdge[0]);
		noOfClusters = requiredNoOfClusters;
		clusters = new ArrayList<ArrayList<Vertex>>();
		outputFilename = requiredOutputFilename;
		stabilizingMeansFilename = requiredstabilizingMeansFilename;
		outputWriter = new PrintWriter(new FileWriter(outputFilename));
		stabilizingMeansWriter 
		  = new PrintWriter(new FileWriter(stabilizingMeansFilename));
		stabilizingMeansChange = "";
		
		printGraphDetails();
		
	  // proceed with the calculations
		System.out.println("clustering on degree...");
		findClustersFromDegree();
		outputWriter.println("Clustering after initial cluster on degree:");
		printClustering();
		System.out.println("clustering on types of neighbour...");
		findClustersFromNeighbours();
		outputWriter.println("Clustering after secondary cluster on neighbours:");
		printClustering();
		outputWriter.close();
		System.out.println("clustering completed\n");
		
		// output data about the stabilizing means
		stabilizingMeansWriter.println(stabilizingMeansChange);
		stabilizingMeansWriter.close();
	} // KMeansClusterer
	
	/* constructor method */
	public KMeansClusterer(ArrayList<ArrayList<Vertex>> requiredClusters, 
                         Vertex[] requiredVertexSet) throws Exception
	{
		clusters = requiredClusters;
		inputVertexSet = requiredVertexSet;
	} // KMeansClusterer
	
	private void printGraphDetails()
	{
		outputWriter.println("Graph: " + inputGraph + "\r\n");
		outputWriter.print("Vertices(" + inputVertexSet.length + "): ");
		for (int index = 0; index < inputVertexSet.length; index++)
			outputWriter.print(inputVertexSet[index] + "#" + (index + 1) + " | ");
		outputWriter.println("\r\n");
		outputWriter.print("Edges(" + inputEdgeSet.length + "): ");
		for (int index = 0; index < inputEdgeSet.length; index++)
			outputWriter.print(inputEdgeSet[index] + "#" + (index + 1) + " | ");
		outputWriter.println("\r\n");
	} // printGraphDetails
	
	/* method to cluster the data */
	private void findClustersFromDegree()
	{
	  // variables for data output of the stablizing means
		stabilizingMeansChange += "degree";
		for (int index = 0; index < noOfClusters; index++)
			stabilizingMeansChange += ", Cluster[" + (index + 1) + "]";
		stabilizingMeansChange += ", THRESHOLD\r\n";
		int iteration = 0;
		
		double[] clusterDegreeMeans = new double[noOfClusters];
		
		// find the largest degree of the graph
		Integer maxDegree = 0;
		for (int index = 0; index < inputVertexSet.length; index++)
		{
		  Integer thisDegree = (Integer)inputGraph.degreeOf(inputVertexSet[index]);
		  if (thisDegree > maxDegree)
		  	maxDegree = thisDegree;
		} // for
		
		// initialise the means array
		// starting with mean equally spread according to no of clusters
		// ...
		// e.g. neighbourhood range 0 -> 3, no of clusters (k) = 2
		// m(1) = 0.75; m(2) = 2.25
	  // e.g. neighbourhood range 0 -> 7, no of clusters (k) = 3
		// m(1) = 1.1666; m(2) = 3.5; m(3) = 5.8333
		double meanSpread = (double)maxDegree / (double)noOfClusters;
		for (int index = 0; index < noOfClusters; index++)
			clusterDegreeMeans[index] = (meanSpread / 2.0) * ((2 * index) + 1);
		
		// variable to say whether the algorithm has stabilized
		boolean negligibleMeanChanges;
		
		do
		{
			// variable for data output of the stabilizing means
			iteration++;
			
			// as we are reassigning nodes to clusters based on newly calculated means
			// erase the previous assignment of clusters
			clusters.clear();
			
		  // an array to monitor whether nodes have been assigned to clusters yet
			boolean[] assignedToCluster = new boolean[inputVertexSet.length];
			for (int index = 0; index < inputVertexSet.length; index++)
				assignedToCluster[index] = false;
			
			// if the boolean remains unchanged, algorithm will end (now stable)
			negligibleMeanChanges = true;
			double[] meanChanges = new double[noOfClusters];
			
		  // associate data with the closest mean
		  // ...
	    // loop through each cluster 
		  for (int clusterIndex = 0; clusterIndex < noOfClusters; clusterIndex++)
	  	{
		  	ArrayList<Vertex> thisCluster = new ArrayList<Vertex>();
		  	
			  // and compare the distance between node's degree and the mean of the cluster
			  // with that of the distance to the mean of every other cluster
		  	// if it is not already assigned to a cluster
			  for (int nodeIndex = 0; nodeIndex < inputVertexSet.length; nodeIndex++)
			  {
			  	if (!assignedToCluster[nodeIndex])
			  	{
			  	  int nodeDegree = (Integer)inputGraph.degreeOf(inputVertexSet[nodeIndex]);
			  	  boolean currentMeanClosest = true;
				
				    // if this cluster is still the nearest cluster
				    // compare distance of next cluster available
				    for (int otherIndex = 0; currentMeanClosest && otherIndex < noOfClusters; 
				         otherIndex++)
				    {
				  	  // do nothing if the clusters are the same
				  	  if (clusterIndex != otherIndex)
				  	  {
				  	    double closenessToCurrentCluster
				  	      = Math.pow(Math.abs((nodeDegree - clusterDegreeMeans[clusterIndex])), 2);
				  	    double closenessToOtherCluster
			  		      = Math.pow(Math.abs((nodeDegree - clusterDegreeMeans[otherIndex])), 2);
				  	
				        if (closenessToOtherCluster < closenessToCurrentCluster)
					  	    currentMeanClosest = false;
				  	  } // if
				    } // for
				
				    // if the considered cluster is still flagged as the closest
				    // i.e. none of the others are closer
				    // set the node to be in that cluster
				    if (currentMeanClosest)
				    {
					    thisCluster.add(inputVertexSet[nodeIndex]);
					    assignedToCluster[nodeIndex] = true;
				    } // if
			    } // if
			  } // for
			  
			  clusters.add(thisCluster);
		  } // for
		
		  // loop through the clusters, updating their means to be the
		  // average degree of the vertices in the cluster
		  for (int clusterIndex = 0; clusterIndex < noOfClusters; clusterIndex++)
		  {
			  // only update the set if it is not empty (empty sets are not updated)
		  	if (!clusters.get(clusterIndex).isEmpty())
			  {
		  		// use this to help record the change in the mean
		  		double originalMean = clusterDegreeMeans[clusterIndex];
		  		
			  	ArrayList<Vertex> currentCluster = clusters.get(clusterIndex);
			  	int sumOfDegrees = 0;
				  for (int nodeIndex = 0; nodeIndex < currentCluster.size(); nodeIndex++)
					  sumOfDegrees += (Integer)inputGraph.degreeOf(currentCluster.get(nodeIndex));
				  clusterDegreeMeans[clusterIndex] 
				    = (double)sumOfDegrees / (double)clusters.get(clusterIndex).size();
				  
				  // keep a record of the size of the changes of the means
				  meanChanges[clusterIndex] 
				    = Math.abs(originalMean - clusterDegreeMeans[clusterIndex]);
		  	} // if
		  } // for
		  
		  // output data of the stabilizing means
		  stabilizingMeansChange += iteration;
		  for (int index = 0; index < meanChanges.length ; index++)
		  	stabilizingMeansChange += ", " + meanChanges[index];
		  stabilizingMeansChange +=  ", " + degreeMeanChangeThresh + "\r\n";
		  
		  // if all means have a change that is < 0.1 [ARBITRARY]
		  // then the algorithm has stabilized and the loop will not be executed again 
		  for (int index = 0; index < meanChanges.length; index++)
		  {
		  	if (meanChanges[index] >= degreeMeanChangeThresh)
		  	{
		  		negligibleMeanChanges = false;
		  		break;
		  	} // if
		  } // for
		} while (!negligibleMeanChanges);
	} // findClusters

	/* method to print the clusters */
	private void printClustering()
	{
		for (int index = 0; index < noOfClusters; index++)
		{
			if ((index + 1) >= 100)
			  outputWriter.println("Cluster " + (index + 1) + ": " + clusters.get(index));
			else if ((index + 1) >= 10)
				outputWriter.println("Cluster  " + (index + 1) + ": " + clusters.get(index));
			else
				outputWriter.println("Cluster   " + (index + 1) + ": " + clusters.get(index));
		} // for
		
		outputWriter.println();
	} // printClustering

	/* method to cluster the data */
	private void findClustersFromNeighbours()
	{
	  // variables for data output of the stabilizing means
		stabilizingMeansChange += "\r\nneighbourtypes";
		for (int index = 0; index < noOfClusters; index++)
			stabilizingMeansChange += ", Cluster[" + (index + 1) + "]";
		stabilizingMeansChange += ", THRESHOLD\r\n";
		int iteration = 0;
		
		// array stores average no. of neighbours of each cluster
		// ...
		// i.e. if 3-dimensional:
		// means(0) = [avr 0 -> 0][avr 0 -> 1][avr 0 -> 2]
	  // means(1) = [avr 1 -> 0][avr 1 -> 1][avr 1 -> 2]
	  // means(2) = [avr 2 -> 0][avr 2 -> 1][avr 2 -> 2]
		ArrayList<ArrayList<Double>> clusterNeighbourMeans 
		  = new ArrayList<ArrayList<Double>>();
		
		clusterNeighbourMeans = calculateNeighbourhoodMeans();
		
		// variable to say whether the algorithm has stabilized
		boolean negligibleMeanChanges;
		
		do
		{
		  // variable for data output of the stabilizing means
			iteration++;
			
			// as we are reassigning nodes to clusters based on newly calculated means
			// erase the previous assignment of clusters
			// (but temporarily store the current clustering
			//  for calculation of new clusters)
			ArrayList<ArrayList<Vertex>> currentClustering = new ArrayList<ArrayList<Vertex>>();
			for (int index = 0; index < clusters.size(); index++)
				currentClustering.add(clusters.get(index));
			clusters.clear();
			
		  // an array to monitor whether nodes have been assigned to clusters yet
			boolean[] assignedToCluster = new boolean[inputVertexSet.length];
			for (int index = 0; index < inputVertexSet.length; index++)
				assignedToCluster[index] = false;
			
			// if the boolean remains unchanged, algorithm will end (now stable)
			negligibleMeanChanges = true;
			double[] meanChanges = new double[noOfClusters];
			
		  // associate data with the closest mean
		  // ...
	    // loop through each cluster 
		  for (int clusterIndex = 0; clusterIndex < noOfClusters; clusterIndex++)
	  	{
		  	ArrayList<Vertex> thisCluster = new ArrayList<Vertex>();
		  	
			  // and, by looking at how many of each cluster a node is connected to,
			  // compare the node's distance to all the clusters
		  	// and assign it to the nearest cluster (if not already assigned)
			  for (int nodeIndex = 0; nodeIndex < inputVertexSet.length; nodeIndex++)
			  {
			  	if (!assignedToCluster[nodeIndex])
			  	{
			  		// first create an ArrayList to store the count of different clusters
			  		// a node is connected to
			  	  ArrayList<Integer> neighbourTypeCount = new ArrayList<Integer>();
			  	  
			  	  // count the number of differently clustered neighbours of each node
			  	  // ... by getting a list of all the edges connected to the node...
			  	  DefaultEdge[] currentEdges
			  	    = inputGraph.edgesOf(inputVertexSet[nodeIndex]).toArray(new DefaultEdge[0]);
			  	  
			  	  // ... looping through each possible adjacent cluster...
			  	  for (int adjIndex = 0; adjIndex < noOfClusters; adjIndex++)
			  	  {
			  	  	ArrayList<Vertex> currentAdjacents = currentClustering.get(adjIndex);
			  	  	Integer adjCount = 0;
			  	  	
			  	  	// ... and counting how many times the sources/targets of the edges
			  	  	// are found in the current adjacent cluster
			  	  	for (int edgeIndex = 0; edgeIndex < currentEdges.length; edgeIndex++)
			  	  	{
			  	  	  Vertex currentEdgeSource = inputGraph.getEdgeSource(currentEdges[edgeIndex]);
						  	Vertex currentEdgeTarget = inputGraph.getEdgeTarget(currentEdges[edgeIndex]);
							
						  	// if the node on the other end of the edge is in the adjacent cluster
						  	// increment the count of how many neighbours of that position there are
							  if (currentAdjacents.contains(currentEdgeSource)
							  		&& !currentEdgeSource.equals(inputVertexSet[nodeIndex]))
							  	adjCount++;
							  else if (currentAdjacents.contains(currentEdgeTarget)
							  		     && !currentEdgeTarget.equals(inputVertexSet[nodeIndex]))
							  	adjCount++;
			  	  	} // for
			  	  	
			  	  	neighbourTypeCount.add(adjCount);
			  	  } // for
			  	
			  	  boolean currentMeanClosest = true;
				
				    // if this cluster is still the nearest cluster
				    // compare distance of next cluster available
				    for (int otherIndex = 0; currentMeanClosest && otherIndex < noOfClusters; 
				         otherIndex++)
				    {
				  	  // do nothing if the clusters are the same
				    	// or if either of the clusters being compared is empty
				  	  if (clusterIndex != otherIndex
				  	  		&& !clusterNeighbourMeans.get(clusterIndex).isEmpty()
				  	  		&& !clusterNeighbourMeans.get(otherIndex).isEmpty())
				  	  {
				  	  	
				  	  	
				  	    double distanceToCurrentCluster = 0.0;
				  	    for (int index = 0; index < noOfClusters; index++)
				  	      distanceToCurrentCluster 
				  	        += Math.pow(Math.abs((double)neighbourTypeCount.get(index) 
				  	           - clusterNeighbourMeans.get(clusterIndex).get(index)), 2);
				  	    double distanceToOtherCluster = 0.0;
				  	    for (int index = 0; index < noOfClusters; index++)
				  	      distanceToOtherCluster 
				  	        += Math.pow(Math.abs(neighbourTypeCount.get(index) 
				  	           - clusterNeighbourMeans.get(otherIndex).get(index)), 2);
				  	
				        if (distanceToCurrentCluster > distanceToOtherCluster)
					  	    currentMeanClosest = false;
				  	  } // if
				    } // for
				    
				    // if the considered cluster is still flagged as the closest
				    // (and if it is NOT EMPTY)
				    // i.e. none of the others are closer
				    // set the node to be in that cluster
				    if (currentMeanClosest && !clusterNeighbourMeans.get(clusterIndex).isEmpty())
				    {
					    thisCluster.add(inputVertexSet[nodeIndex]);
					    assignedToCluster[nodeIndex] = true;
				    } // if 
			    } // if
			  } // for 
			  
			  clusters.add(thisCluster);
		  } // for
		
		  // recalculate the neighbourhood means of the clusters
		  // after temporarily storing the means of the previous clusters
		  ArrayList<ArrayList<Double>> clusterPrevNeighbourMeans 
		    = new ArrayList<ArrayList<Double>>();
		  for (int index = 0; index < clusterNeighbourMeans.size(); index++)
				clusterPrevNeighbourMeans.add(clusterNeighbourMeans.get(index));
		  clusterNeighbourMeans = calculateNeighbourhoodMeans();
		  
		  // loop through the clusters, calculating the change in mean
		  for (int clusterIndex = 0; clusterIndex < noOfClusters; clusterIndex++)
		  {
			  // keep a record of the size of the changes of the means
			  if (clusterNeighbourMeans.get(clusterIndex).isEmpty())
			  	meanChanges[clusterIndex] = 0.0;
			  
			  else
			    for (int index = 0; index < noOfClusters; index++)
			    {
				    meanChanges[clusterIndex] 
				      += Math.pow(Math.abs(clusterNeighbourMeans.get(clusterIndex).get(index)
				         - clusterPrevNeighbourMeans.get(clusterIndex).get(index)), 2);
			    }
		  } // for
		  
		  // output data of the stabilizing means
		  stabilizingMeansChange += iteration;
		  for (int index = 0; index < meanChanges.length ; index++)
		  	stabilizingMeansChange += ", " + meanChanges[index];
		  stabilizingMeansChange += ", " + neighbourMeanChangeThresh + "\r\n";
		  
		  // if all means have a change that is < ARBITRARY NUMBER
		  // then the algorithm has stabilized and the loop will not be executed again 
		  for (int index = 0; index < meanChanges.length; index++)
		  {
		  	if (meanChanges[index] >= neighbourMeanChangeThresh)
		  	{
		  		negligibleMeanChanges = false;
		  		break;
		  	} // if
		  } // for
		} while (!negligibleMeanChanges);
	} // findClusters

	/* method to calculate the neighbourhood means */
	private ArrayList<ArrayList<Double>> calculateNeighbourhoodMeans()
	{
		ArrayList<ArrayList<Double>> neighbourhoodMeans 
	  = new ArrayList<ArrayList<Double>>();
		
	  // initialise the means arrays
		// based on the existing clusters and their neighbours
		for (int clusterIndex = 0; clusterIndex < noOfClusters; clusterIndex++)
		{
			ArrayList<Double> adjacencyMeans = new ArrayList<Double>();
			
			// if the cluster is not empty
			// average the connections its elements has to all the clusters
			if (!clusters.get(clusterIndex).isEmpty())
			{
				ArrayList<Vertex> currentCluster = clusters.get(clusterIndex);
				
				// for each cluster, loop through each of the possible adjacent clusters
				for (int adjIndex = 0; adjIndex < noOfClusters; adjIndex++)
				{
					ArrayList<Vertex> currentAdjacents = clusters.get(adjIndex);
					Double adjCount = 0.0;
					
					// for each adjacent cluster
					// loop through all the vertices of the current cluster under consideration
				  for (int vertexIndex = 0; vertexIndex < currentCluster.size(); vertexIndex++)
				  {
				  	DefaultEdge[] currentEdges
				  	  = inputGraph.edgesOf(currentCluster.get(vertexIndex)).toArray(new DefaultEdge[0]);
					
					  // for each vertex in the current cluster
				  	// loop through the edges that are connected to it
					  for (int edgeIndex = 0; edgeIndex < currentEdges.length; edgeIndex++)
					  {
					  	Vertex currentEdgeSource = inputGraph.getEdgeSource(currentEdges[edgeIndex]);
					  	Vertex currentEdgeTarget = inputGraph.getEdgeTarget(currentEdges[edgeIndex]);
						
					  	// if the node on the other end of the edge is in the adjacent cluster
					  	// increment the count of how many neighbours of that position there are
						  if (currentAdjacents.contains(currentEdgeSource)
						  		&& !currentEdgeSource.equals(currentCluster.get(vertexIndex)))
						  	adjCount++;
						  else if (currentAdjacents.contains(currentEdgeTarget)
						  		     && !currentEdgeTarget.equals(currentCluster.get(vertexIndex)))
						  	adjCount++;
					  } // for
				  } // for
				  
				  // average the count over the number of elements in the set
				  if (adjCount == 0.0)
				  	adjacencyMeans.add(adjCount);
				  else
				  	adjacencyMeans.add(adjCount / (double)currentCluster.size());
				} // for
			} // if
			
			neighbourhoodMeans.add(adjacencyMeans);
		} // for
		
		return neighbourhoodMeans;
	} // initialiseNeighbourhoodMeans
	
	
	/* method to return the cluster containing the given node */
	public ArrayList<Vertex> getClusterOf(Vertex givenNode)
	{
		ArrayList<Vertex> cluster = null;
		
		for (int clusterIndex = 0; clusterIndex < clusters.size(); clusterIndex++)
		{
			if (clusters.get(clusterIndex).contains(givenNode))
				cluster = clusters.get(clusterIndex);
		} // for
		
		return cluster;
	} // getClusterIndex
	
	
	/* getters and setters */
	public Vertex[] getInputVertexSet()
	{
		return inputVertexSet;
	} // getInputVertexSet
	
	public DefaultEdge[] getInputEdgeSet()
	{
		return inputEdgeSet;
	} // getInputEdgeSet
	
	public ArrayList<ArrayList<Vertex>> getClusters()
	{
		return clusters;
	} // getClusters
	
	public String toString()
	{
		String clusterString = "";
		for (int index = 0; index < clusters.size(); index++)
		{
			if ((index + 1) >= 100)
			  clusterString += "Cluster " + (index + 1) + ": " + clusters.get(index) + "\r\n";
			else if ((index + 1) >= 10)
				clusterString += "Cluster 0" + (index + 1) + ": " + clusters.get(index) + "\r\n";
			else
				clusterString += "Cluster 00" + (index + 1) + ": " + clusters.get(index) + "\r\n";
		} // for
		return clusterString;
	} // toString
} // class KMeansClusterer