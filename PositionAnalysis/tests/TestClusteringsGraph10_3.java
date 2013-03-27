import junit.framework.TestCase;

import java.util.ArrayList;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TestClusteringsGraph10_3 extends TestCase
{
	private String directory;
	private UndirectedGraph<Vertex, DefaultEdge> inputGraph;
	private Vertex[] inputVertexSet;
	private OrbitFinder orbitFinder;
	private KMeansClusterer kMeanClusterer;
	
	// constructor
	public TestClusteringsGraph10_3(String name, String requiredDirectory)
	{
		super(name);
		directory = requiredDirectory + "\\graph10_3.gml";
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
	public void testClusteringsG10_3() throws Exception
	{
	  // test the orbits are as expected
		// -- [A] [B,C,D] [E,F] [G,H] [I,J]
		orbitFinder = new OrbitFinder(inputGraph, "testOut.txt", 3);
		ArrayList<Vertex> expectedOrbit1 = new ArrayList<Vertex>();
		ArrayList<Vertex> expectedOrbit2 = new ArrayList<Vertex>();
		ArrayList<Vertex> expectedOrbit3 = new ArrayList<Vertex>();
		ArrayList<Vertex> expectedOrbit4 = new ArrayList<Vertex>();
		ArrayList<Vertex> expectedOrbit5 = new ArrayList<Vertex>();
		expectedOrbit1.add(inputVertexSet[0]);
		expectedOrbit2.add(inputVertexSet[1]);
		expectedOrbit2.add(inputVertexSet[2]);
		expectedOrbit2.add(inputVertexSet[3]);
		expectedOrbit3.add(inputVertexSet[4]);
		expectedOrbit3.add(inputVertexSet[5]);
		expectedOrbit4.add(inputVertexSet[6]);
		expectedOrbit4.add(inputVertexSet[7]);
		expectedOrbit5.add(inputVertexSet[8]);
		expectedOrbit5.add(inputVertexSet[9]);
		ArrayList<ArrayList<Vertex>> expectedOrbits = new ArrayList<ArrayList<Vertex>>();
		expectedOrbits.add(expectedOrbit1);
		expectedOrbits.add(expectedOrbit2);
		expectedOrbits.add(expectedOrbit3);
		expectedOrbits.add(expectedOrbit4);
		expectedOrbits.add(expectedOrbit5);;
		ArrayList<ArrayList<Vertex>> actualOrbits = orbitFinder.getOrbits();
		assertEquals(expectedOrbits, actualOrbits);
		
		// test the k-means clusterings (k = 3) are as expected
		// -- [B,C,D,E,F] [G,H,I,J] [A]
		kMeanClusterer = new KMeansClusterer(inputGraph, "testOut.txt", "testOut.csv", 3);
		ArrayList<Vertex> expectedCluster1 = new ArrayList<Vertex>();
		ArrayList<Vertex> expectedCluster2 = new ArrayList<Vertex>();
		ArrayList<Vertex> expectedCluster3 = new ArrayList<Vertex>();
		expectedCluster3.add(inputVertexSet[0]);
		expectedCluster1.add(inputVertexSet[1]);
		expectedCluster1.add(inputVertexSet[2]);
		expectedCluster1.add(inputVertexSet[3]);
		expectedCluster1.add(inputVertexSet[4]);
		expectedCluster1.add(inputVertexSet[5]);
		expectedCluster2.add(inputVertexSet[6]);
		expectedCluster2.add(inputVertexSet[7]);
		expectedCluster2.add(inputVertexSet[8]);
		expectedCluster2.add(inputVertexSet[9]);
		ArrayList<ArrayList<Vertex>> expectedClusters = new ArrayList<ArrayList<Vertex>>();
		expectedClusters.add(expectedCluster1);
		expectedClusters.add(expectedCluster2);
		expectedClusters.add(expectedCluster3);
		ArrayList<ArrayList<Vertex>> actualClusters = kMeanClusterer.getClusters();
		assertEquals(expectedClusters, actualClusters);
		
	} // testClusterings

} // class TestClusteringsGraph10_3
