import junit.framework.TestCase;

import java.util.ArrayList;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TestClusteringsGraph09_1 extends TestCase
{
	private String directory;
	private UndirectedGraph<Vertex, DefaultEdge> inputGraph;
	private Vertex[] inputVertexSet;
	private OrbitFinder orbitFinder;
	private KMeansClusterer kMeanClusterer;
	
	// constructor
	public TestClusteringsGraph09_1(String name, String requiredDirectory)
	{
		super(name);
		directory = requiredDirectory + "\\graph09_1.gml";
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
	public void testClusteringsG09_1() throws Exception
	{
	  // test the orbits are as expected
		// -- [A] [B] [C] [D] [E] [F,H] [G] [I]
		orbitFinder = new OrbitFinder(inputGraph, "testOut.txt", 2);
		ArrayList<Vertex> expectedOrbit1 = new ArrayList<Vertex>();
		ArrayList<Vertex> expectedOrbit2 = new ArrayList<Vertex>();
		ArrayList<Vertex> expectedOrbit3 = new ArrayList<Vertex>();
		ArrayList<Vertex> expectedOrbit4 = new ArrayList<Vertex>();
		ArrayList<Vertex> expectedOrbit5 = new ArrayList<Vertex>();
		ArrayList<Vertex> expectedOrbit6 = new ArrayList<Vertex>();
		ArrayList<Vertex> expectedOrbit7 = new ArrayList<Vertex>();
		ArrayList<Vertex> expectedOrbit8 = new ArrayList<Vertex>();
		expectedOrbit1.add(inputVertexSet[0]);
		expectedOrbit2.add(inputVertexSet[1]);
		expectedOrbit3.add(inputVertexSet[2]);
		expectedOrbit4.add(inputVertexSet[3]);
		expectedOrbit5.add(inputVertexSet[4]);
		expectedOrbit6.add(inputVertexSet[5]);
		expectedOrbit7.add(inputVertexSet[6]);
		expectedOrbit6.add(inputVertexSet[7]);
		expectedOrbit8.add(inputVertexSet[8]);
		ArrayList<ArrayList<Vertex>> expectedOrbits = new ArrayList<ArrayList<Vertex>>();
		expectedOrbits.add(expectedOrbit1);
		expectedOrbits.add(expectedOrbit2);
		expectedOrbits.add(expectedOrbit3);
		expectedOrbits.add(expectedOrbit4);
		expectedOrbits.add(expectedOrbit5);
		expectedOrbits.add(expectedOrbit6);
		expectedOrbits.add(expectedOrbit7);
		expectedOrbits.add(expectedOrbit8);
		ArrayList<ArrayList<Vertex>> actualOrbits = orbitFinder.getOrbits();
		assertEquals(expectedOrbits, actualOrbits);
		
	  // test the k-means clusterings (k = 8) are as expected
		// -- [A] [BI] [] [CFH] [D] [] [E] [G]
		kMeanClusterer = new KMeansClusterer(inputGraph, "testOut.txt", "testOut.csv", 8);
		ArrayList<Vertex> expectedCluster1 = new ArrayList<Vertex>();
		ArrayList<Vertex> expectedCluster2 = new ArrayList<Vertex>();
		ArrayList<Vertex> expectedCluster3 = new ArrayList<Vertex>();
		ArrayList<Vertex> expectedCluster4 = new ArrayList<Vertex>();
		ArrayList<Vertex> expectedCluster5 = new ArrayList<Vertex>();
		ArrayList<Vertex> expectedCluster6 = new ArrayList<Vertex>();
		ArrayList<Vertex> expectedCluster7 = new ArrayList<Vertex>();
		ArrayList<Vertex> expectedCluster8 = new ArrayList<Vertex>();
		expectedCluster1.add(inputVertexSet[0]);
		expectedCluster2.add(inputVertexSet[1]);
		expectedCluster4.add(inputVertexSet[2]);
		expectedCluster5.add(inputVertexSet[3]);
		expectedCluster7.add(inputVertexSet[4]);
		expectedCluster4.add(inputVertexSet[5]);
		expectedCluster8.add(inputVertexSet[6]);
		expectedCluster4.add(inputVertexSet[7]);
		expectedCluster2.add(inputVertexSet[8]);
		ArrayList<ArrayList<Vertex>> expectedClusters = new ArrayList<ArrayList<Vertex>>();
		expectedClusters.add(expectedCluster1);
		expectedClusters.add(expectedCluster2);
		expectedClusters.add(expectedCluster3);
		expectedClusters.add(expectedCluster4);
		expectedClusters.add(expectedCluster5);
		expectedClusters.add(expectedCluster6);
		expectedClusters.add(expectedCluster7);
		expectedClusters.add(expectedCluster8);
		ArrayList<ArrayList<Vertex>> actualClusters = kMeanClusterer.getClusters();
		assertEquals(expectedClusters, actualClusters);
		
	  // test the k-means clusterings (k = 5) are as expected
		// -- [ABI] [CFH] [D] [E] [G]
		kMeanClusterer = new KMeansClusterer(inputGraph, "testOut.txt", "testOut.csv", 5);
		expectedCluster1 = new ArrayList<Vertex>();
		expectedCluster2 = new ArrayList<Vertex>();
		expectedCluster3 = new ArrayList<Vertex>();
		expectedCluster4 = new ArrayList<Vertex>();
		expectedCluster5 = new ArrayList<Vertex>();
		expectedCluster1.add(inputVertexSet[0]);
		expectedCluster1.add(inputVertexSet[1]);
		expectedCluster2.add(inputVertexSet[2]);
		expectedCluster3.add(inputVertexSet[3]);
		expectedCluster4.add(inputVertexSet[4]);
		expectedCluster2.add(inputVertexSet[5]);
		expectedCluster5.add(inputVertexSet[6]);
		expectedCluster2.add(inputVertexSet[7]);
		expectedCluster1.add(inputVertexSet[8]);
		expectedClusters = new ArrayList<ArrayList<Vertex>>();
		expectedClusters.add(expectedCluster1);
		expectedClusters.add(expectedCluster2);
		expectedClusters.add(expectedCluster3);
		expectedClusters.add(expectedCluster4);
		expectedClusters.add(expectedCluster5);
		actualClusters = kMeanClusterer.getClusters();
		assertEquals(expectedClusters, actualClusters);
		
		// test the k-means clusterings (k = 10) are as expected
		// -- [A] [BI] [] [CFH] [] [] [D] [] [E] [G]
		kMeanClusterer = new KMeansClusterer(inputGraph, "testOut.txt", "testOut.csv", 10);
		expectedCluster1 = new ArrayList<Vertex>();
		expectedCluster2 = new ArrayList<Vertex>();
		expectedCluster3 = new ArrayList<Vertex>();
		expectedCluster4 = new ArrayList<Vertex>();
		expectedCluster5 = new ArrayList<Vertex>();
		expectedCluster6 = new ArrayList<Vertex>();
		expectedCluster7 = new ArrayList<Vertex>();
		expectedCluster8 = new ArrayList<Vertex>();
		ArrayList<Vertex> expectedCluster9 = new ArrayList<Vertex>();
		ArrayList<Vertex> expectedCluster10 = new ArrayList<Vertex>();
		expectedCluster1.add(inputVertexSet[0]);
		expectedCluster2.add(inputVertexSet[1]);
		expectedCluster4.add(inputVertexSet[2]);
		expectedCluster6.add(inputVertexSet[3]);
		expectedCluster8.add(inputVertexSet[4]);
		expectedCluster4.add(inputVertexSet[5]);
		expectedCluster10.add(inputVertexSet[6]);
		expectedCluster4.add(inputVertexSet[7]);
		expectedCluster2.add(inputVertexSet[8]);
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

} // class TestClusteringsGraph04_5
