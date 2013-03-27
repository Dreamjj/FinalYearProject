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

public class OrbitFinder
{
	private UndirectedGraph<Vertex, DefaultEdge> inputGraph;
	private Vertex[] inputVertexSet;
	private DefaultEdge[] inputEdgeSet;
	private int MAX_N_LEVEL;
	private double[][] equivMatrix;
	private ArrayList<ArrayList<Vertex>> orbits;
	private String outputFilename;
	private PrintWriter outputWriter;

	/* constructor method */
	public OrbitFinder(UndirectedGraph<Vertex, DefaultEdge> requiredGraph,
			               String requiredOutputFilename, int requiredMAX_N_LEVEL) 
	                   throws IOException
	{
		inputGraph = requiredGraph;
		inputVertexSet = inputGraph.vertexSet().toArray(new Vertex[0]);
		inputEdgeSet = inputGraph.edgeSet().toArray(new DefaultEdge[0]);
		MAX_N_LEVEL = requiredMAX_N_LEVEL;
		equivMatrix = new double[inputVertexSet.length][inputVertexSet.length];
		orbits = new ArrayList<ArrayList<Vertex>>();
		outputFilename = requiredOutputFilename;
		outputWriter = new PrintWriter(new FileWriter(outputFilename));
		
		printGraphDetails();
		
		// proceed with the calculations
		System.out.println("calculating equivalence matrix...");
		calculateEquivMatrix();
		System.out.println("finding orbits...");
		findOrbits();
		printOrbits();
		printEquivalences();
		outputWriter.close();
		System.out.println("orbit grouping completed\n");
	} // OrbitFinder
	
	/* constructor method */
	public OrbitFinder(ArrayList<ArrayList<Vertex>> requiredOrbits, 
			               Vertex[] requiredVertexSet) throws IOException
	{
		orbits = requiredOrbits;
		inputVertexSet = requiredVertexSet;
	} // OrbitFinder

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
	
	/* method to calculate the equivalence matrix of the input graph */
	private void calculateEquivMatrix()
	{
		// initialise (set all zeroes) the equivalence matrix
		// and matrices to store degree/betw vectors for each actor at each neighbour. level
		for (int xIndex = 0; xIndex < inputVertexSet.length; xIndex++)
			for (int yIndex = 0; yIndex < inputVertexSet.length; yIndex++)
				equivMatrix[xIndex][yIndex] = 0.0;
		
		DegreeVector[][] degreeMatrix = new DegreeVector[inputVertexSet.length][MAX_N_LEVEL];
		BetweennessVector[][] betweennessMatrix
		  = new BetweennessVector[inputVertexSet.length][MAX_N_LEVEL];
		
		double normaliser = 0.0;
		// for each n-order neighbourhood loop through each actor... 
		for (int nLevel = 0; nLevel < MAX_N_LEVEL; nLevel++)
		{
			System.out.println("   ... at neighbourhood level " + (nLevel + 1));
		  for (int actorIndex = 0; actorIndex < inputVertexSet.length; actorIndex++)
		  {
		  	/* UNCOMMENT FOR PROGRAM TRACKING PURPOSES
		  	 * System.out.println(inputVertexSet[actorIndex] + " @ N:" + nLevel); */
		  	// ... and generate its neighbourhood subgraph
		  	UndirectedGraph<Vertex, DefaultEdge> neighbourhoodSubgraph
		      = generateNeighbourhoodGraph(nLevel + 1, actorIndex);
				
		  	// and using this fill the degree and betweenness matrices
				degreeMatrix[actorIndex][nLevel] = calculateDegreeVector(neighbourhoodSubgraph);
				betweennessMatrix[actorIndex][nLevel]
				  = calculateBetweennessVector(neighbourhoodSubgraph);
		  } // for
		  
		  // fill in equivalence matrix
			for (int xIndex = 0; xIndex < inputVertexSet.length; xIndex++)
				for (int yIndex = 0; yIndex < inputVertexSet.length; yIndex++)
				{
					// calculate similarity between vectors
					double degreeSim = degreeMatrix[xIndex][nLevel]
					                   .similarityWith(degreeMatrix[yIndex][nLevel]);
					double betweennessSim = betweennessMatrix[xIndex][nLevel]
					           					    .similarityWith(betweennessMatrix[yIndex][nLevel]);
					
					// overall similarity is average of the two vector similarities
					double similarity = (degreeSim + betweennessSim) / 2.0;
					equivMatrix[xIndex][yIndex] += (similarity / (nLevel + 1));
				} // for
			normaliser += (1.0 / (nLevel + 1));
		} // for
		
		// normalise equivalence matrix (all values between 0 and 1)
		for (int xIndex = 0; xIndex < inputVertexSet.length; xIndex++)
			for (int yIndex = 0; yIndex < inputVertexSet.length; yIndex++)
				equivMatrix[xIndex][yIndex] 
				  = equivMatrix[xIndex][yIndex] / normaliser;
		
	} // calculateEquivMatrix 
	
	/* method to create an undirected subgraph
	 * based on one particular actor and a neighbourhood level */
	private UndirectedGraph<Vertex, DefaultEdge>
	  generateNeighbourhoodGraph(int givenNLevel, int givenActorIndex)
	{
		// acquire the vertex set of the original supergraph
		// and add the considered actor to the set of subgraph vertices
		Set<Vertex> subVertexSet = new HashSet<Vertex>();
		subVertexSet.add(inputVertexSet[givenActorIndex]);
		
		// acquire the edges connected to the considered actor ("ego edge set")
		Set<DefaultEdge> egoEdgeSet = inputGraph.edgesOf(inputVertexSet[givenActorIndex]);
		
		// create a set to allow collection of the subgraph edges
		// and initialise with the "ego edge set"
		Set<DefaultEdge> subEdgeSet = new HashSet<DefaultEdge>();
		subEdgeSet.addAll(egoEdgeSet);
		ArrayList<Vertex> addedVertices = new ArrayList<Vertex>();
		
		// add all the source & target vertices of the subgraph edges
		// to the set of subgraph vertices 
		// .....
		// each iteration produces a higher-order neighbourhood of the considered actor
		// (i.e. 1st iteration = immediate neighbours, 2nd iteration = 2nd-order neighbourhood)
		for (int nLevel = 0; nLevel < givenNLevel; nLevel++)
		{
			// first, add all edges of vertices for which we want to get the neighbours
			// NOTE: always null for the first iteration - i.e. only want neighbours of self
			if (addedVertices != null)
				for (int vertexIndex = 0; vertexIndex < addedVertices.size(); vertexIndex++)
			    subEdgeSet.addAll(inputGraph.edgesOf(addedVertices.get(vertexIndex)));
			
			// then iterate over the set of edges collected so far
			// if edge has a source/target vertex which isn't in the set of subgraph vertices
			// add this vertex to the set
		  Iterator<DefaultEdge> edgeIterator = subEdgeSet.iterator();
      while (edgeIterator.hasNext())
      {
          DefaultEdge currentEdge = edgeIterator.next();
          if (!subVertexSet.contains(inputGraph.getEdgeSource(currentEdge)))
          {
          	subVertexSet.add(inputGraph.getEdgeSource(currentEdge));
          	addedVertices.add(inputGraph.getEdgeSource(currentEdge));
          } // if
          if (!subVertexSet.contains(inputGraph.getEdgeTarget(currentEdge)))
          {
          	subVertexSet.add(inputGraph.getEdgeTarget(currentEdge));
          	addedVertices.add(inputGraph.getEdgeTarget(currentEdge));
          } // if
      } // while
		} // for
    
    // finally add any additional edges between the added vertices
		// by looping through all added vertices and checking for edges between them
		// and adding them to the set of subgraph edges if not already contained
    if (addedVertices != null)
      for (int v1Index = 0; v1Index < addedVertices.size() - 1; v1Index++)
        for (int v2Index = v1Index + 1; v2Index < addedVertices.size(); v2Index++)
        {
        	DefaultEdge additionalEdge
        	  = inputGraph.getEdge(addedVertices.get(v1Index), addedVertices.get(v2Index));
        	if (additionalEdge != null)
        	{
        	  if (!subEdgeSet.contains(additionalEdge))
        	  	subEdgeSet.add(additionalEdge);
        	} // if
        } // for
		
    // create the subgraph of the considered node and its neighbourhood
		UndirectedSubgraph<Vertex, DefaultEdge> neighbourhoodSubgraph
		= new UndirectedSubgraph<Vertex, DefaultEdge>(inputGraph, subVertexSet, subEdgeSet);
		
		// remove considered node from the graph
		// to be left with just the subgraph of its neighbourhood
		neighbourhoodSubgraph.removeVertex(inputVertexSet[givenActorIndex]);
		
		return neighbourhoodSubgraph;
	} // neighbourhoodSubgraph

	/* method to calculate the degree vector of a given undirected graph */
	private DegreeVector 
	  calculateDegreeVector(UndirectedGraph<Vertex, DefaultEdge> givenGraph)
	{
		// create a new DegreeVector (where the calculated ArrayList will be stored)
		DegreeVector degreeVector;
		ArrayList<Integer> neighbourhoodDegrees = new ArrayList<Integer>();
		
		// loop through the vertices of the graph
		// finding the degree of each vertex and adding it to the ArrayList
		Vertex[] givenVertexSet = givenGraph.vertexSet().toArray(new Vertex[0]);
		for (int index = 0; index < givenVertexSet.length; index++)
		{
			Integer thisDegree = (Integer)givenGraph.degreeOf(givenVertexSet[index]);
			neighbourhoodDegrees.add(thisDegree);
		} // for
		
		// sort the ArrayList
		Collections.sort(neighbourhoodDegrees);
		
		// construct the DegreeVector with the ArrayList and return it
		degreeVector = new DegreeVector(neighbourhoodDegrees);
		return degreeVector;
	} // calculateDegreeVector
	
  /* method to calculate the betweenness vector of a given undirected graph */
	private BetweennessVector
	  calculateBetweennessVector(UndirectedGraph<Vertex, DefaultEdge> givenGraph)
	{	
	  // create a new BetweennessVector (where the calculated ArrayList will be stored)
		BetweennessVector betweennessVector = null;
		ArrayList<Double> neighbourhoodBetweenness = new ArrayList<Double>();
		
		Vertex[] givenVertexSet = givenGraph.vertexSet().toArray(new Vertex[0]);
		DefaultEdge[] givenEdgeSet = givenGraph.edgeSet().toArray(new DefaultEdge[0]);
		
		// create new FloydWarshall algorithm
		// to know shortest distance between two nodes of the given graph
		FloydWarshallShortestPaths<Vertex, DefaultEdge> floydWarshall
	  = new FloydWarshallShortestPaths<Vertex, DefaultEdge>(givenGraph);
		
		// create a matrix to store all the shortest paths
		// between two given nodes
		GraphPathVector[][] shortestPathsMatrix
		  = new GraphPathVector[givenVertexSet.length][givenVertexSet.length];
		
		// loop through each pair of nodes, finding all the shortest paths between them
		for (int startIndex = 0; startIndex < givenVertexSet.length; startIndex++)
			for (int endIndex = 0; endIndex < givenVertexSet.length; endIndex++)
			{
				// get the shortest distance between the nodes
				int shortestDistance 
				  = (int) floydWarshall.shortestDistance(givenVertexSet[startIndex], 
				  		                                   givenVertexSet[endIndex]);
				
				// create a list of graph paths to store the shortest paths
				// between two nodes
				List<GraphPath<Vertex,DefaultEdge>> shortestPaths;
				
				// if the shortest distance is 0 (i.e. the same node)
				// or 1 (i.e. no intermediate node)
				// or equals infinity (i.e. no path)
				// the shortest path will be an empty list
				if (shortestDistance <= 1
						|| shortestDistance == (int) Double.POSITIVE_INFINITY)
					shortestPaths = new ArrayList<GraphPath<Vertex, DefaultEdge>>();
				
				else
				{
					// nPaths is the max no. of paths to find
					// set to 1 if there are no edges
					// otherwise set to size of edge set squared - ARBITRARY?!?!?!?!
					int nPaths;
					if (givenEdgeSet.length <= 0)
						nPaths = 1;
					else
						nPaths = givenEdgeSet.length * givenEdgeSet.length;
					
				  // create a new K-shortest paths and use to get all the paths
				  // from the start node that travel that shortest distance to the end node
					// ...
			    // therefore getting all the shortest paths from one node to the other!
			  	KShortestPaths<Vertex, DefaultEdge> kShortestPaths 
			      = new KShortestPaths<Vertex, DefaultEdge>
				      (givenGraph, givenVertexSet[startIndex], nPaths, shortestDistance);
			  	
			  	shortestPaths = kShortestPaths.getPaths(givenVertexSet[endIndex]);
			  	
			  	// if there are no paths, kShortestPaths will have returned null
			  	// so replace with an empty ArrayList
			  	if (shortestPaths == null)
			  		shortestPaths = new ArrayList<GraphPath<Vertex, DefaultEdge>>();
				} // else
				
			  // store the shortest paths in the matrix of shortest paths
		  	shortestPathsMatrix[startIndex][endIndex] = new GraphPathVector(shortestPaths);
			} // for
		
		// sum all shortest paths
		int noOfShortestPaths = 0;
		for (int startIndex = 0; startIndex < givenVertexSet.length; startIndex++)
			for (int endIndex = 0; endIndex < givenVertexSet.length; endIndex++)
				noOfShortestPaths 
				  += shortestPathsMatrix[startIndex][endIndex].getGraphPathVector().size();
		
		// sum all the shortest paths which pass through each node
		for (int nodeIndex = 0; nodeIndex < givenVertexSet.length; nodeIndex++)
		{
			int noOfPathsThroughThis = 0;
			
			// the path A:B,B:C should only count B as an intermediate node
			// in order to stop A,C being counted
			// increment only when a node appears TWICE in a path denoted by edges
			boolean detectedOnce = false;
			
			// for each pair of nodes get all the shortest paths between them
			for (int startIndex = 0; startIndex < givenVertexSet.length; startIndex++)
				for (int endIndex = 0; endIndex < givenVertexSet.length; endIndex++)
				{
					List<GraphPath<Vertex, DefaultEdge>> currentShortestPaths
					  = shortestPathsMatrix[startIndex][endIndex].getGraphPathVector();
					
					// for each path in the list of graphPaths
					// get the list of edges in the path
					for (int pathIndex = 0; pathIndex < currentShortestPaths.size(); pathIndex++)
					{
						GraphPath<Vertex, DefaultEdge> currentPath
						  = currentShortestPaths.get(pathIndex);
						
						List<DefaultEdge> currentEdgeList = currentPath.getEdgeList();
						
						// loop through each edge in the current shortest path
						for (int edgeIndex = 0; edgeIndex < currentEdgeList.size(); edgeIndex++)
						{
							// get the current node under consideration
							// and the source and target nodes
							// against which the current node will be compared
							Vertex currentVertex = givenVertexSet[nodeIndex];
							Vertex edgeSourceVertex 
							  = givenGraph.getEdgeSource(currentEdgeList.get(edgeIndex));
							Vertex edgeTargetVertex 
						    = givenGraph.getEdgeTarget(currentEdgeList.get(edgeIndex));
							
							// if the node has not been detected yet...
						  // ... if it matches one of the vertices of the edge
							// ... set it to have been detected
							if (!detectedOnce)
							{
							  if (currentVertex.equals(edgeSourceVertex)
							  		|| currentVertex.equals(edgeTargetVertex))
								  detectedOnce = true;
							} // if
							
						  // if the node has been detected...
						  // ... if it matches one of the vertices of the edge
							// ... increment the count of the shortest paths through this node
							else
							{
								if (currentVertex.equals(edgeSourceVertex)
										|| currentVertex.equals(edgeTargetVertex))
								  noOfPathsThroughThis++;
								
								// regardless of node being matched, reset detectedOnce
								// - therefore if a vertex is the start or end of a path
								// - it will never be found a 2nd time, and not counted
								detectedOnce = false;
							} // else
						} // for
						
						// reset detectedOnce when reached end of the current path
						detectedOnce = false;
					} // for
				} // for
			
			// calculate the betweenness of the current node
			// and add to the current neighbourhood betweenness
			Double ratioOfPathsThroughThis 
			  = ((double)noOfPathsThroughThis / noOfShortestPaths);
			neighbourhoodBetweenness.add(ratioOfPathsThroughThis);
		} // for
		
		// sort the ArrayList
		Collections.sort(neighbourhoodBetweenness);
		
		// construct the BetweennessVector with the ArrayList and return it
		betweennessVector = new BetweennessVector(neighbourhoodBetweenness);
		return betweennessVector;
  } // calculateBetweennessVector

	/* method to find the orbits of the input */
	private void findOrbits()
	{
		// keep track of which vertices are yet to be grouped
		// - initialise this to be all of them
		ArrayList<Vertex> ungroupedVertices = new ArrayList<Vertex>();
		for (int index = 0; index < inputVertexSet.length; index++)
			ungroupedVertices.add(inputVertexSet[index]);
		
		// loop through each vertex finding equivalent others
		for (int xIndex = 0; xIndex < inputVertexSet.length; xIndex++)
		{
			
			// if the current vertex is not yet grouped...
			if (ungroupedVertices.contains(inputVertexSet[xIndex]))
			{
				ArrayList<Vertex> thisOrbit = new ArrayList<Vertex>();
				
				// ... loop through the remaining ungrouped vertices
				// adding them to the current orbit
				// and removing them from the list of ungrouped vertices
			  for (int yIndex = 0; yIndex < inputVertexSet.length; yIndex++)
			  {
			  	if (ungroupedVertices.contains(inputVertexSet[yIndex])
				  		&& equivMatrix[xIndex][yIndex] == 1.0)
			  	{
				  	thisOrbit.add(inputVertexSet[yIndex]);
				  	ungroupedVertices.remove(inputVertexSet[yIndex]);
			  	} // if
			  } // for
			  
			  // add the resulting orbit to the array of orbits
			  // and remove the current vertex from the ungrouped vertices
			  orbits.add(thisOrbit);
			  ungroupedVertices.remove(inputVertexSet[xIndex]);
			} // if
		} // for
	} // findOrbits
	
	/* method to print the orbits of the input */
	private void printOrbits()
	{
		for (int index = 0; index < orbits.size(); index++)
		{
			if ((index + 1) >= 100)
			  outputWriter.println("Orbit " + (index + 1) + ": " + orbits.get(index));
			else if ((index + 1) >= 10)
				outputWriter.println("Orbit  " + (index + 1) + ": " + orbits.get(index));
			else
				outputWriter.println("Orbit   " + (index + 1) + ": " + orbits.get(index));
		} // for
		
		outputWriter.println();
	} // printOrbits
	
	/* method to print the equivalences between the orbits */
	private void printEquivalences()
	{
		outputWriter.println("Equivalences: ");
		for (int orbitIndex = 0; orbitIndex < orbits.size(); orbitIndex++)
		{
			outputWriter.println(orbits.get(orbitIndex));
			
			for (int compIndex = 0; compIndex < orbits.size(); compIndex++)
			{
				if (compIndex != orbitIndex)
				{
					double similarity = equivMatrix[orbits.get(orbitIndex).get(0).getID()]
					                               [orbits.get(compIndex).get(0).getID()];
					
					DecimalFormat decimalFormat = new DecimalFormat("0.000");
					String similarityDF = decimalFormat.format(similarity);
					outputWriter.println("   -> " + orbits.get(compIndex)
							               + " = " + similarityDF);
				}
			} // for
		} // for
	} // printEquivalences


	/* method to return the orbit containing the given node */
	public ArrayList<Vertex> getOrbitOf(Vertex givenNode)
	{
		ArrayList<Vertex> orbit = new ArrayList<Vertex>();
		
		for (int orbitIndex = 0; orbitIndex < orbits.size(); orbitIndex++)
		{
			if (orbits.get(orbitIndex).contains(givenNode))
				orbit = orbits.get(orbitIndex);
		} // for
		
		return orbit;
	} // getOrbitOf
	
	/* method to return the equivalence between two given orbits */
	public double getOrbitEquivalence(ArrayList<Vertex> givenOrbit1,
			                              ArrayList<Vertex> givenOrbit2)
	{
		double orbitEquiv = 0.0;
		int orbit1Index = 0;
		int orbit2Index = 0;
		
		// find the index of the orbits
		// so they can be input into the equivalence matrix
		orbit1Index = getOrbitIndex(givenOrbit1);
		orbit2Index = getOrbitIndex(givenOrbit2);
		
		// equivalence matrix stores relates NODES, not ORBITS
		// so get the ID of the first element of each orbit
		// (any element from either orbit would produce same equiv)
		// to use as indices
		orbitEquiv = equivMatrix[orbits.get(orbit1Index).get(0).getID()]
		                        [orbits.get(orbit2Index).get(0).getID()];
		
		return orbitEquiv;
	}
	
	/* method to return the index of the orbit of a given node */
	public int getOrbitIndex(ArrayList<Vertex> givenOrbit)
	{
		int indexOfOrbit = 0;
		
		for (int orbitIndex = 0; orbitIndex < orbits.size(); orbitIndex++)
		{
			if (orbits.get(orbitIndex).equals(givenOrbit))
				indexOfOrbit = orbitIndex;
		} // for
		
		return indexOfOrbit;
	} // getOrbitIndex
	
	/* method to print the equivalence matrix */
	public String printEquivMatrix()
	{
		String equivMatrixString = "";
		
		// print the top rows of the table
		// (using index numbers for equal spacing)
		equivMatrixString += " Orbit |";
			for (int index = 0; index < orbits.size(); index++)
					equivMatrixString += "       |";
		equivMatrixString += "\r\n";
		
		equivMatrixString += " Equiv |";
		for (int index = 0; index < orbits.size(); index++)
		{
			if ((index + 1) >= 100)
				equivMatrixString += "  " + (index + 1) + "  |";
			else if ((index + 1) >= 10)
				equivMatrixString += "  0" + (index + 1) + "  |";
			else
				equivMatrixString += "  00" + (index + 1) + "  |";
		} // for
		equivMatrixString += "\r\n";
		
		// print the second row of the table
		// which is just dashes underneath each orbit number
		equivMatrixString += "--------";
		for (int index = 0; index < orbits.size(); index++)
		{
			equivMatrixString += "--------";
		} // for
		equivMatrixString += "\r\n";
		
		// print each row of data
		for (int xIndex = 0; xIndex < orbits.size(); xIndex++)
		{
			if ((xIndex + 1) >= 100)
				equivMatrixString += "  ";
			else if ((xIndex + 1) >= 10)
				equivMatrixString += "  0";
			else
				equivMatrixString += "  00";
			equivMatrixString += (xIndex + 1) + "  |";
			
			for (int yIndex = 0; yIndex < orbits.size(); yIndex++)
			{
				DecimalFormat decimalFormat = new DecimalFormat("0.000");
				String currentEquiv = decimalFormat
				  .format(equivMatrix[orbits.get(xIndex).get(0).getID()]
				                     [orbits.get(yIndex).get(0).getID()]);
				equivMatrixString += " " + currentEquiv + " |";
			} // for
			equivMatrixString += "\r\n";
		} // for
		
		return equivMatrixString;
	} // printEquivMatrix

	
	/* getters and setters */
	public Vertex[] getInputVertexSet()
	{
		return inputVertexSet;
	} // getInputVertexSet
	
	public DefaultEdge[] getInputEdgeSet()
	{
		return inputEdgeSet;
	} // getInputEdgeSet
	
	public ArrayList<ArrayList<Vertex>> getOrbits()
	{
		return orbits;
	} // getOrbits
	
	public String toString()
	{
		String orbitString = "";
		for (int index = 0; index < orbits.size(); index++)
		{
			if ((index + 1) >= 100)
			  orbitString += "Orbit " + (index + 1) + ": " + orbits.get(index) + "\r\n";
			else if ((index + 1) >= 10)
				orbitString += "Orbit 0" + (index + 1) + ": " + orbits.get(index) + "\r\n";
			else
				orbitString += "Orbit 00" + (index + 1) + ": " + orbits.get(index) + "\r\n";
		} // for
		return orbitString;
	} // toString
} // class OrbitFinder