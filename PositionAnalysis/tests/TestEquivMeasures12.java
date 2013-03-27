import junit.framework.TestCase;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TestEquivMeasures12 extends TestCase
{
	// constructor
	public TestEquivMeasures12(String name)
	{
		super(name);
	}
	
	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testEquivMeasures12() throws Exception
	{
		// create the vertices used in the test
		Vertex vertexA = new Vertex(0, "A");
		Vertex vertexB = new Vertex(1, "B");
		Vertex vertexC = new Vertex(2, "C");
		Vertex vertexD = new Vertex(3, "D");
		Vertex vertexE = new Vertex(4, "E");
		Vertex[] vertexSet = {vertexA, vertexB, vertexC, vertexD, vertexE};
		
		// create the orbit finder (using 2nd constructor to bypass calculations)
		ArrayList<Vertex> orbit1 = new ArrayList<Vertex>();
		orbit1.add(vertexA);
		orbit1.add(vertexB);
		orbit1.add(vertexC);
		orbit1.add(vertexD);
		orbit1.add(vertexE);
		ArrayList<ArrayList<Vertex>> orbits = new ArrayList<ArrayList<Vertex>>();
		orbits.add(orbit1);
		OrbitFinder orbitFinder = new OrbitFinder(orbits, vertexSet);
		
	  // create the k-means clusterer (using 2nd constructor to bypass calculations)
		ArrayList<Vertex> cluster1 = new ArrayList<Vertex>();
		ArrayList<Vertex> cluster2 = new ArrayList<Vertex>();
		cluster1.add(vertexA);
		cluster1.add(vertexB);
		cluster2.add(vertexC);
		cluster2.add(vertexD);
		cluster2.add(vertexE);
		ArrayList<ArrayList<Vertex>> clusters = new ArrayList<ArrayList<Vertex>>();
		clusters.add(cluster1);
		clusters.add(cluster2);
		KMeansClusterer kMeansClusterer = new KMeansClusterer(clusters, vertexSet);
		
	  // create a new position analysis with the secondary constructor
		// so that any orbitsFinders/kMeansClusteres can be fed in
		// not just ones generated from graphs
		PositionAnalysis positionAnalysis 
		  = new PositionAnalysis(orbitFinder, kMeansClusterer);
		
	  // test orbit-cluster equivalence, and clustering similarity
		double equivalence = positionAnalysis.getOrbitsToClustersEquivalence();
		double similarity = positionAnalysis.getSimilarityOfClusters();
		assertEquals(0.8, equivalence);
		assertEquals(0.4, similarity);
		
	} // testEquivMeasures

} // class TestEquivMeasures12
