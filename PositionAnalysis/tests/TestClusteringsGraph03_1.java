import junit.framework.TestCase;

import java.util.ArrayList;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TestClusteringsGraph03_1 extends TestCase
{
	private String directory;
	private UndirectedGraph<Vertex, DefaultEdge> inputGraph;
	private Vertex[] inputVertexSet;
	private OrbitFinder orbitFinder;
	private KMeansClusterer kMeanClusterer;
	
	// constructor
	public TestClusteringsGraph03_1(String name, String requiredDirectory)
	{
		super(name);
		directory = requiredDirectory + "\\graph03_1.gml";
	}
	
	@Before
	public void setUp() throws Exception
	{
		inputGraph = new GMLParser(directory).getGraph();
		inputVertexSet = inputGraph.vertexSet().toArray(new Vertex[0]);
	}

	@After
	public void tearDown() throws Exception
	{
		inputGraph = null;
		inputVertexSet = null;
		orbitFinder = null;
		kMeanClusterer = null;
	}

	@Test
	public void testClusteringsG03_1() throws Exception
	{
	  // test the orbits are as expected
		// -- [A,B,C]
		orbitFinder = new OrbitFinder(inputGraph, "testOut.txt", 2);
		ArrayList<Vertex> expectedOrbit1 = new ArrayList<Vertex>();
		expectedOrbit1.add(inputVertexSet[0]);
		expectedOrbit1.add(inputVertexSet[1]);
		expectedOrbit1.add(inputVertexSet[2]);
		ArrayList<ArrayList<Vertex>> expectedOrbits = new ArrayList<ArrayList<Vertex>>();
		expectedOrbits.add(expectedOrbit1);
		ArrayList<ArrayList<Vertex>> actualOrbits = orbitFinder.getOrbits();
		assertEquals(expectedOrbits, actualOrbits);
		
	  // test the k-means clusterings (k = 2) are as expected
		// -- [] [A,B,C]
		kMeanClusterer = new KMeansClusterer(inputGraph, "testOut.txt", "testOut.csv", 2);
		ArrayList<Vertex> expectedCluster1 = new ArrayList<Vertex>();
		ArrayList<Vertex> expectedCluster2 = new ArrayList<Vertex>();
		expectedCluster2.add(inputVertexSet[0]);
		expectedCluster2.add(inputVertexSet[1]);
		expectedCluster2.add(inputVertexSet[2]);
		ArrayList<ArrayList<Vertex>> expectedClusters = new ArrayList<ArrayList<Vertex>>();
		expectedClusters.add(expectedCluster1);
		expectedClusters.add(expectedCluster2);
		ArrayList<ArrayList<Vertex>> actualClusters = kMeanClusterer.getClusters();
		assertEquals(expectedClusters, actualClusters);
		
		// test the k-means clusterings (k = 10) are as expected
		// -- [] [] [] [] [] [] [] [] [] [A,B,C]
		kMeanClusterer = new KMeansClusterer(inputGraph, "testOut.txt", "testOut.csv", 10);
		expectedCluster1 = new ArrayList<Vertex>();
		expectedCluster2 = new ArrayList<Vertex>();
		ArrayList<Vertex> expectedCluster3 = new ArrayList<Vertex>();
		ArrayList<Vertex> expectedCluster4 = new ArrayList<Vertex>();
		ArrayList<Vertex> expectedCluster5 = new ArrayList<Vertex>();
		ArrayList<Vertex> expectedCluster6 = new ArrayList<Vertex>();
		ArrayList<Vertex> expectedCluster7 = new ArrayList<Vertex>();
		ArrayList<Vertex> expectedCluster8 = new ArrayList<Vertex>();
		ArrayList<Vertex> expectedCluster9 = new ArrayList<Vertex>();
		ArrayList<Vertex> expectedCluster10 = new ArrayList<Vertex>();
		expectedCluster10.add(inputVertexSet[0]);
		expectedCluster10.add(inputVertexSet[1]);
		expectedCluster10.add(inputVertexSet[2]);
		expectedClusters = new ArrayList<ArrayList<Vertex>>();
		expectedClusters.add(expectedCluster1);
		expectedClusters.add(expectedCluster2);
		expectedClusters.add(expectedCluster3);
		expectedClusters.add(expectedCluster4);
		expectedClusters.add(expectedCluster5);
		expectedClusters.add(expectedCluster6);
		expectedClusters.add(expectedCluster7);
		expectedClusters.add(expectedCluster8);
		expectedClusters.add(expectedCluster9);
		expectedClusters.add(expectedCluster10);
		actualClusters = kMeanClusterer.getClusters();
		assertEquals(expectedClusters, actualClusters);
		
	} // testClusterings

} // class TestClusteringsGraph03_1
