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

public class PositionAnalysis 
{
	private static String inputFilename;
	private static String outputFilename;
	private static UndirectedGraph<Vertex, DefaultEdge> inputGraph;
	private static int kValue;
	private static OrbitFinder orbitFinder;
	private static KMeansClusterer kMeansClusterer;
	private static ArrayList<ArrayList<Vertex>> orbits;
	private static ArrayList<ArrayList<Vertex>> clusters;
	private static ArrayList<PointPair> pointPairs;
	private static double coverageToAccuracyBias;
	private static double orbitsToClustersEquivalence;
	private static double similarityOfClusters;
	private static double elementCloseness;
	private static String orbitToClusterEquivCalculations;
	private static String similarityOfClustersCalculations;
	private static String elementClosenessCalculations;
	
	/* constructor method */
	public PositionAnalysis (String requiredInputFilename, 
			                     int requiredKValue) throws Exception
	{
	  // intialise the variables
		inputFilename = requiredInputFilename;
		inputGraph = new GMLParser(inputFilename).getGraph();
		outputFilename = deriveOutputFilename("results", "txt");
		
		if (inputGraph.vertexSet().isEmpty())
			throw new Exception("\nInput graph must not be an empty graph");
		
		String orbitOutput = deriveOutputFilename("orbit", "txt");
		orbitFinder = new OrbitFinder(inputGraph, orbitOutput, 2);
		orbits = orbitFinder.getOrbits();
		
		String clusterOutput = deriveOutputFilename("cluster", "txt");
		String stabilizingMeansOutput = deriveOutputFilename("meanchanges", "csv");
		kValue = requiredKValue;
		kMeansClusterer = new KMeansClusterer(inputGraph, clusterOutput,
				                                  stabilizingMeansOutput, kValue);
		clusters = kMeansClusterer.getClusters();
		
		// prepare strings and formatting for outputting results
		orbitToClusterEquivCalculations 
		  = "============================================================" 
        + "==================================================" + "\r\n";
		similarityOfClustersCalculations 
	  = "============================================================" 
      + "==================================================" + "\r\n";
		elementClosenessCalculations
      = "============================================================" 
        + "==================================================" + "\r\n";
		
	  // obtain the equivalence between the set of orbits and the set of clusters
	  // and the similarity of the two clusterings based on point-pair assignments
		// and how close clustered elements are to being in the correct orbit
		coverageToAccuracyBias = 0.5;
		orbitsToClustersEquivalence = clustersEquivToOrbits();
		pointPairs = new ArrayList<PointPair>();
		similarityOfClusters = clusteringSimilarity();
		elementCloseness = closenessOfElements();
		
		printOutput();
		System.out.println("output produced");
	} // PositionAnalysis
	
	/* constructor method */
	public PositionAnalysis (OrbitFinder requiredOrbitFinder,
			                     KMeansClusterer requiredKMeansClusterer) 
	                         throws Exception
	{
	  // intialise the variables
		orbitFinder = requiredOrbitFinder;
		kMeansClusterer = requiredKMeansClusterer;
		orbits = orbitFinder.getOrbits();
		clusters = kMeansClusterer.getClusters();
		
    Vertex[] orbitVertices = orbitFinder.getInputVertexSet();
    Vertex[] clusterVertices = kMeansClusterer.getInputVertexSet();
    if (orbitVertices.length != clusterVertices.length)
    	throw new Exception("\nElements in the two clustering methods differ");
    for (int index = 0; index < orbitVertices.length; index++)
      if (!orbitVertices[index].equals(clusterVertices[index]))
        throw new Exception("\nElements in the two clustering methods differ");
		
   // prepare strings and formatting for outputting results
		orbitToClusterEquivCalculations 
		  = "============================================================" 
        + "==================================================" + "\r\n";
		similarityOfClustersCalculations 
	  = "============================================================" 
      + "==================================================" + "\r\n";
    
	  // obtain the equivalence between the set of orbits and the set of clusters
	  // and the similarity of the two clusterings based on point-pair assignments
		// and how close clustered elements are to being in the correct orbit
		coverageToAccuracyBias = 0.5;
		orbitsToClustersEquivalence = clustersEquivToOrbits();
		pointPairs = new ArrayList<PointPair>();
		similarityOfClusters = clusteringSimilarity();
	} // PositionAnalysis
	
	/* method to derive the appropriate output filename string */
	private String deriveOutputFilename(String givenSuffix, 
			                                       String givenFiletype)
	{
		String derivedFilename;
		String[] inputParts = inputFilename.split("\\.");
		derivedFilename = inputParts[0] + "-" + givenSuffix + "." + givenFiletype;
		
	  // if filename derived already exists, append a non-existent number to the end
	  File outputFile = new File(derivedFilename);
	  int appendNum = 0;
	  while (outputFile.exists())
	  {
	  	appendNum++;
	  	String[] filenameParts = derivedFilename.split("-");
	  	derivedFilename = filenameParts[0] + "-" + givenSuffix 
	  	                  + "(" + appendNum + ")." + givenFiletype;;
	  	outputFile = new File(derivedFilename);
	  } // while
		
		return derivedFilename;
	} // deriveOutputFilename

	/* method to calculate the equivalance between 
	 * orbit result and cluster result */
	private double clustersEquivToOrbits()
	{
		// variables to assist with result output
		String overallCalculation = "";
		DecimalFormat decimalFormat = new DecimalFormat("0.000");
		
		double overallEquivalence = 0;
		
		// for each orbit, find the cluster with greatest coverage
		for (int orbitsIndex = 0; orbitsIndex < orbits.size(); orbitsIndex++)
		{
			GreatestCoverageDetail greatestCovInfo = findCoverageInfo(orbitsIndex);
			
			// retrieve the numbers required for the calculation of the 'cluster distance'
			// cluster equiv = (coverage * lambda) + (accuracy * (1 - lambda))
			// [lambda = bias towards either coverage or accuracy; 0.5 is no bias]
			// (fraction of orbit covered by cluster) + (fraction of cluster in excess)
			double accuracy 
			  = accuracyOf(orbits.get(orbitsIndex), 
                     clusters.get(greatestCovInfo.getClusterIndex()));
			
			double thisClusterEquiv 
			  = (greatestCovInfo.getCoverage() * coverageToAccuracyBias) 
			  + (accuracy * (1 - coverageToAccuracyBias));
      
      // overall equivalence = sum(cluster equivalences) / no of orbits
      // ... this is the summation part of the equation
      overallEquivalence += thisClusterEquiv;
      
      // generate the string which explains calculations at output
      // requires variables to be used in the output
      int noOfMatches 
		    = countMatchesBetween(orbits.get(orbitsIndex), 
				                      clusters.get(greatestCovInfo.getClusterIndex()));
      int noAdditionalClusterElements
        = additionalElements(orbits.get(orbitsIndex), 
                             clusters.get(greatestCovInfo.getClusterIndex()));
      int orbitSize = orbits.get(orbitsIndex).size();
      int clusterSize = clusters.get(greatestCovInfo.getClusterIndex()).size();
      
      orbitToClusterEquivCalculations 
        += "orbit: " + (orbitsIndex + 1) + " | most similar cluster: "
           + (greatestCovInfo.getClusterIndex() + 1)
           + " | equivalence: " + thisClusterEquiv + "\r\n"
           + "  - orbit:   " + orbits.get(orbitsIndex) + "\r\n"
           + "  - cluster: " + clusters.get(greatestCovInfo.getClusterIndex()) 
           + "\r\n" 
           + "    # coverage = matched elements / size of orbit = "
           + noOfMatches + " / " + orbitSize + " = " 
           + greatestCovInfo.getCoverage() + "\r\n"
           + "    # accuracy = 1 - (cluster elements not in orbit / size of cluster)"
           + " = 1 - (" + noAdditionalClusterElements + " / " + clusterSize + ") = "
           + accuracy + "\r\n"
           + "    # orbit-cluster equivalence = "
           + "(coverage * bias) + (accuracy * (1 - bias))" + "\r\n"
           + "                                = "
           + "(" + greatestCovInfo.getCoverage() + " * " + coverageToAccuracyBias
           + ") + (" + accuracy + " * (1 - " + coverageToAccuracyBias + "))"
           + " = " + thisClusterEquiv + "\r\n"
           + "------------------------------------------------------------" 
           + "--------------------------------------------------" + "\r\n";
      overallCalculation += decimalFormat.format(thisClusterEquiv) + " + "; 
		} // for
	  
	  // remove the final extra " + " and
	  // continue to generate the string which explains calculations at output
		overallCalculation 
		  = overallCalculation.substring(0, overallCalculation.length() - 3);
		orbitToClusterEquivCalculations 
		  += "average orbit-cluster equivalence:" + "\r\n"
		  	 + "(" + overallCalculation + ")" + " / " + orbits.size()
		  	 + " = " + overallEquivalence / (double)orbits.size() + "\r\n"
		  	 + "============================================================" 
         + "==================================================" + "\r\n";
		
	  // overall equivalence = sum(cluster equivalences) / no of orbits
    // ... this is the division part of the equation
		overallEquivalence /= (double)orbits.size();
		return overallEquivalence;
	} // clustersEquivToOrbits
	
	/* method to calculate the similarity between the two clusterings
	 * (orbit-finder method and k-means method) 
	 * = no of similar point-pair clustering assignments / all assignments */
	private double clusteringSimilarity()
	{
		double similarity;
		int similarAssignments = 0;
		Vertex[] vertexSet = orbitFinder.getInputVertexSet();
		
		ArrayList<PointPair> similarPairs = new ArrayList<PointPair>();
		ArrayList<PointPair> dissimilarPairs = new ArrayList<PointPair>();
		
		// for every pair of points (vertices)
		// create the point pair to go into the array
		// NB: pair (ab) = pair (ba) so only calculate pair (ab)
		for (int index1 = 0; index1 < vertexSet.length - 1; index1++)
			for (int index2 = index1 + 1; index2 < vertexSet.length; index2++)
			{
				Vertex pointA = vertexSet[index1];
				Vertex pointB = vertexSet[index2];
				ArrayList<Vertex> clusterY_pointA = orbitFinder.getOrbitOf(pointA);
				ArrayList<Vertex> clusterY_pointB = orbitFinder.getOrbitOf(pointB);
				ArrayList<Vertex> clusterZ_pointA 
				  = kMeansClusterer.getClusterOf(pointA);
				ArrayList<Vertex> clusterZ_pointB
				  = kMeansClusterer.getClusterOf(pointB);

				PointPair thisPair 
				  = new PointPair(pointA, pointB, clusterY_pointA, 
				  		            clusterY_pointB, clusterZ_pointA, clusterZ_pointB);
				pointPairs.add(thisPair);
			} // for
		
		// count how many point-pairs are assigned similarly
		// (similarAssignments method returns 1 if similar, 0 if not)
		for (int index = 0; index < pointPairs.size(); index++)
		{
			if (pointPairs.get(index).assignmentSimilarity() == 1)
				similarPairs.add(pointPairs.get(index));
			else
				dissimilarPairs.add(pointPairs.get(index));
			
			similarAssignments += pointPairs.get(index).assignmentSimilarity();
		} // for
		
		// generate the string which explains calculations at output
		similarityOfClustersCalculations += "similarly clustered pairs:\r\n";
		for (int index = 0; index < similarPairs.size(); index++)
			similarityOfClustersCalculations 
			  += "  * " + similarPairs.get(index) + "\r\n";
		similarityOfClustersCalculations 
		  += "  + " + similarPairs.size() + " pairs\r\n"
			   + "------------------------------------------------------------" 
         + "--------------------------------------------------" + "\r\n";
		similarityOfClustersCalculations += "dissimilarly clustered pairs:\r\n";
		for (int index = 0; index < dissimilarPairs.size(); index++)
			similarityOfClustersCalculations 
		    += "  * " + dissimilarPairs.get(index) + "\r\n";
		similarityOfClustersCalculations 
	  += "  + " + dissimilarPairs.size() + " pairs\r\n"
		   + "------------------------------------------------------------" 
       + "--------------------------------------------------" + "\r\n";
	  similarityOfClustersCalculations 
	    += "similarity between the two clusterings:\r\n"
	       + similarPairs.size() + " / " 
	       + (similarPairs.size() + dissimilarPairs.size())
	       + " = " + ((double)similarAssignments / (double)pointPairs.size()) 
	       + "\r\n"
	       + "============================================================" 
         + "==================================================" + "\r\n";
		
		// overall similarity is #similarPointPairAssignments/#pointPairAssignments
		similarity = (double)similarAssignments / (double)pointPairs.size();
		return similarity;
	} // clusteringSimilarity

  /* method to calculate the closeness of the clustered elements
	 * i.e. using the equivalence matrix of the found orbits
	 *      see how close the elements of a cluster are
	 * e.g. if [abc] -> [gh] = 0.5, [abc] -> [jk] = 0.1
	 *      then [abcgk] = (1 + 1 + 1 + 0.5 + 0.1) / 5 = 0.72 closeness */
	private double closenessOfElements()
	{
	  // variables to assist with result output
		String overallCalculation = "";
		DecimalFormat decimalFormat = new DecimalFormat("0.000");
		
		double overallElementCloseness = 0;
		
		// for each orbit, find the most similar cluster
		for (int orbitsIndex = 0; orbitsIndex < orbits.size(); orbitsIndex++)
		{
		  // variables to assist with result output
			String thisClustersCalculation = "";
			String thisClustersSum = "";
			
			GreatestCoverageDetail greatestSimilarityInfo = findCoverageInfo(orbitsIndex);
			double thisClustersCloseness = 0;
			
			// loop through each element of the cluster
			// find out which orbit the element is in
			// and how similar the orbit is to the current orbit
			// summing the similarities as we go
			for (int elementIndex = 0; 
			     elementIndex < clusters.get(greatestSimilarityInfo.getClusterIndex()).size();
			     elementIndex++)
			{
				Vertex element 
				  = clusters.get(greatestSimilarityInfo.getClusterIndex()).get(elementIndex);
				ArrayList<Vertex> elementsOrbit = orbitFinder.getOrbitOf(element);
				double orbitEquivalence
				  = orbitFinder.getOrbitEquivalence(orbits.get(orbitsIndex), elementsOrbit);
				thisClustersCloseness += orbitEquivalence;
				
			  // generate the string which explains calculations at output
				thisClustersCalculation 
				  += "    > closeness of orbit " + (orbitsIndex + 1) + " to "
				     + "\"" + element + "\" (orbit " 
				     + (orbitFinder.getOrbitIndex(elementsOrbit) + 1)
				     + ")" + " = " + orbitEquivalence + "\r\n";
				thisClustersSum += orbitEquivalence + " + ";
			} // for

			// remove the final extra " + "
			thisClustersSum 
		    = thisClustersSum.substring(0, thisClustersSum.length() - 3);
			
      // the element similarity for this orbit's closest cluster
			// is the summed similarity divided by the no of cluster elements (done here)
			thisClustersCloseness 
			  /= (double)clusters.get(greatestSimilarityInfo.getClusterIndex()).size();
			overallElementCloseness += thisClustersCloseness;
			
		  // generate the string which explains calculations at output
      elementClosenessCalculations
        += "orbit: " + (orbitsIndex + 1) + " | most similar cluster: "
           + (greatestSimilarityInfo.getClusterIndex() + 1)
           + " | element closeness: " + thisClustersCloseness + "\r\n"
           + "  - orbit:   " + orbits.get(orbitsIndex) + "\r\n"
           + "  - cluster: " + clusters.get(greatestSimilarityInfo.getClusterIndex()) 
           + "\r\n"
           + thisClustersCalculation
           + "    > (" + thisClustersSum + ") / " 
           + clusters.get(greatestSimilarityInfo.getClusterIndex()).size()
           + " = " + thisClustersCloseness + "\r\n"
           + "------------------------------------------------------------" 
           + "--------------------------------------------------" + "\r\n";
      overallCalculation += decimalFormat.format(thisClustersCloseness) + " + ";
		} // for
	  
  	// continue to generate the string which explains calculations at output
		overallCalculation 
		  = overallCalculation.substring(0, overallCalculation.length() - 3);
		elementClosenessCalculations 
		  += "average element closeness:" + "\r\n"
		  	 + "(" + overallCalculation + ")" + " / " + orbits.size()
		  	 + " = " + overallElementCloseness / (double)orbits.size() + "\r\n"
		  	 + "============================================================" 
         + "==================================================" + "\r\n";
		
	  // overall closeness is the sum of all orbit closeness
		// divided by the no of orbits (done here)
		overallElementCloseness /= (double)orbits.size();
		return overallElementCloseness;
	} // clustersEquivToOrbits
	
	/* method to find info about the highest coverage of an orbit
	 * returns array: [0] = greatest coverage, [1] = index of cluster with this cov */
  private GreatestCoverageDetail findCoverageInfo(int orbitsIndex)
  {
  	double greatestCoverage = 0;
  	int mostCovClusterIndex = 0;
  	
  	for (int clustersIndex = 0; clustersIndex < clusters.size(); clustersIndex++)
	  {
	    double orbitCoverage 
	    = coverageOf(orbits.get(orbitsIndex), clusters.get(clustersIndex));
	    
	    if (orbitCoverage > greatestCoverage)
	    {
	    	greatestCoverage = orbitCoverage;
	    	mostCovClusterIndex = clustersIndex;
	    } // if
	    
	    // if the cluster has the same coverage as the current greatest coverage
	    // check to see if it has a higher accuracy
	    // NB: no need to do if cluster is assigned greatest coverage by default
	    else if (orbitCoverage == greatestCoverage 
	    		     && clustersIndex != mostCovClusterIndex)
	    {
	    	double additionalCluster1Elements 
	    	       = accuracyOf(orbits.get(orbitsIndex), 
                                 clusters.get(clustersIndex));
	    	double additionalCluster2Elements 
  	           = accuracyOf(orbits.get(orbitsIndex), 
                                 clusters.get(mostCovClusterIndex));
	    	
	    	if (additionalCluster1Elements > additionalCluster2Elements) 
		    	mostCovClusterIndex = clustersIndex;
	    } // else
	  } // for
  	
  	// wrap the details of the greatest coverage into an object and return
  	GreatestCoverageDetail greatestCovInfo 
  	  = new GreatestCoverageDetail(greatestCoverage, mostCovClusterIndex);
  	return greatestCovInfo;
  } // findCoverageInfo
	
  /* method to calculate coverage of an orbit by a cluster */
	private double coverageOf(ArrayList<Vertex> givenOrbit,
	                                        ArrayList<Vertex> givenCluster)
	{
		double coverage;
		
	  // if BOTH of the lists are empty, return identical (SHOULD NOT HAPPEN!)
		if (givenOrbit.size() == 0 && givenCluster.size() == 0)
			coverage = 1.0;
		
	  // if only one of the lists are empty, return no coverage
		else if (givenOrbit.size() == 0 || givenCluster.size() == 0)
			coverage = 0.0;
		
		// otherwise, calculate coverage (#matchedElements / #orbitElements)
		else
		{
		  // set the number of elements as the size of the given orbit
			int noOfElements;
		  noOfElements = givenOrbit.size();
      int noOfMatches = countMatchesBetween(givenOrbit, givenCluster);
      
		  // the measure of coverage is the proportion of matches to elements
		  coverage = (double)noOfMatches / (double)noOfElements;
		} // else
		
		return coverage;
	} // coverageOf
  
  /* method to calculate the accuracy of a cluster to an orbit
   * accuracy = how much of the cluster is found in the orbit
   *          = 1 - (#clusterElementsNotInOrbit / #clusterElements) */
  private double accuracyOf(ArrayList<Vertex> givenOrbit,
	                                 ArrayList<Vertex> givenCluster)
  {
  	double clusterAccuracy = 0.0;
  	int noAdditionalElements = additionalElements(givenOrbit, givenCluster);
	
  	// (#noAdditionalElements / #clusterSize)
  	// returns 1 if all elements of cluster DO NOT appear in the orbit
  	// that should be accuracy of 0, so do 1 - answer
  	clusterAccuracy 
  	  = 1 - ((double)noAdditionalElements / (double)givenCluster.size());
  	
  	return clusterAccuracy;
  } // differentElementsBetween
  
  /* method to count the elements in a cluster not existent in an orbit */
  private int additionalElements(ArrayList<Vertex> givenOrbit,
	                                      ArrayList<Vertex> givenCluster)
  {
    int noDifferentElements = 0;
  	
  	// count how many cluster elements are NOT in the orbit
  	for (int clusterIndex = 0; clusterIndex < givenCluster.size(); clusterIndex++)
  	{
  		boolean clusterElementFound = false;
  		
  		for (int orbitIndex = 0; orbitIndex < givenOrbit.size(); orbitIndex++)
  			if (givenCluster.get(clusterIndex).getLabel()
		  			.compareTo(givenOrbit.get(orbitIndex).getLabel()) == 0)
  			{
  				clusterElementFound = true;
  				break;
  			} // if
  		
  		if (!clusterElementFound)
  			noDifferentElements++;
  	} // for
  	
  	return noDifferentElements;
  } // additionalElements
  
  /* method to count number of matching elements between an orbit and a cluster */
	private int countMatchesBetween(ArrayList<Vertex> givenOrbit,
	                                       ArrayList<Vertex> givenCluster)
	{
		// create a variable to count the number of equal elements
		int noOfMatches = 0;
		
		// for each element in the orbit, if any element of the cluster matches it
		// increment the number of matches
	  for (int orbitIndex = 0; orbitIndex < givenOrbit.size(); orbitIndex++)
	  	for (int clusterIndex = 0; clusterIndex < givenCluster.size(); clusterIndex++)
	  		if (givenOrbit.get(orbitIndex).getLabel()
	  				.compareTo(givenCluster.get(clusterIndex).getLabel()) == 0)
	  			noOfMatches++;
	  
	  return noOfMatches;
	} // countMatchesBetween()

	/* method to print the output */
  private void printOutput() throws IOException
  {
  	PrintWriter outputWriter = new PrintWriter(new FileWriter(outputFilename));
		outputWriter.println("\r\n\r\n" + orbitFinder);
		outputWriter.println(kMeansClusterer + "\r\n");
		outputWriter.println(orbitFinder.printEquivMatrix() + "\r\n\r\n");
		outputWriter.println("equivalence between the set of orbits"
                         + " and the set of clusters: "
                         + orbitsToClustersEquivalence);
		outputWriter.println("[coverage/accuracy bias: " 
				                 + coverageToAccuracyBias + "]");
		outputWriter.println(orbitToClusterEquivCalculations + "\r\n\r\n");
		outputWriter.println("similarity between the two clusterings: "
                         + similarityOfClusters);
		outputWriter.println(similarityOfClustersCalculations + "\r\n\r\n");
		outputWriter.println("closeness of elements to their correct cluster: "
				                 + elementCloseness);
		outputWriter.println(elementClosenessCalculations);
		outputWriter.close();
		//System.out.println("\n\n" + orbitFinder);
		//System.out.println(kMeansClusterer + "\n");
		//System.out.println(orbitFinder.printEquivMatrix() + "\n\n");
		//System.out.println("equivalence between the set of orbits"
		//		               + " and the set of clusters: "
		//		               + orbitsToClustersEquivalence);
		//System.out.println("[coverage/accuracy bias: " 
    //                   + coverageToAccuracyBias + "]");
		//System.out.println(orbitToClusterEquivCalculations + "\n\n");
		//System.out.println("similarity between the two clusterings: "
    //                   + similarityOfClusters);
		//System.out.println(similarityOfClustersCalculations + "\n\n");
		//System.out.println("closeness of elements to their correct cluster: "
    //                   + elementCloseness);
		//System.out.println(elementClosenessCalculations);
  } // printOutput


  /* getters and setters */
  public ArrayList<ArrayList<Vertex>> getOrbits()
  {
  	return orbits;
  } // getOrbits
  
  public ArrayList<ArrayList<Vertex>> getClusters()
  {
  	return clusters;
  } // getClusters
  
  public double getOrbitsToClustersEquivalence()
  {
  	return orbitsToClustersEquivalence;
  } // getOrbitsToClustersEquivalence
  
  public String getOrbitsToClustersEquivCalculations()
  {
  	return orbitToClusterEquivCalculations;
  } // getOrbitsToClustersEquivCalculations
  
  public double getSimilarityOfClusters()
  {
  	return similarityOfClusters;
  } // getSimilarityOfClusters
  
  public String getSimilarityOfClustersCalculations()
  {
  	return similarityOfClustersCalculations;
  } // getSimilarityOfClustersCalculations
  
  public double getElementCloseness()
  {
  	return elementCloseness;
  } // getElementCloseness
  
  public String getElementClosenessCalculations()
  {
  	return elementClosenessCalculations;
  } // getElementClosenessCalculations

} // PositionAnalysis
