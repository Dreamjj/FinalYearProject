import junit.framework.TestCase;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TestEquivMeasures08 extends TestCase
{
	// constructor
	public TestEquivMeasures08(String name)
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
	public void testEquivMeasures08() throws Exception
	{
		// create the vertices used in the test
		Vertex vertexA = new Vertex(0, "A");
		Vertex vertexB = new Vertex(1, "B");
		Vertex vertexC = new Vertex(2, "C");
		Vertex[] vertexSet = {vertexA, vertexB, vertexC};
		
		// create the orbit finder (using 2nd constructor to bypass calculations)
		ArrayList<Vertex> orbit1 = new ArrayList<Vertex>();
		ArrayList<Vertex> orbit2 = new ArrayList<Vertex>();
		ArrayList<Vertex> orbit3 = new ArrayList<Vertex>();
		orbit1.add(vertexA);
		orbit2.add(vertexB);
		orbit3.add(vertexC);
		ArrayList<ArrayList<Vertex>> orbits = new ArrayList<ArrayList<Vertex>>();
		orbits.add(orbit1);
		orbits.add(orbit2);
		orbits.add(orbit3);
		OrbitFinder orbitFinder = new OrbitFinder(orbits, vertexSet);
		
	  // create the k-means clusterer (using 2nd constructor to bypass calculations)
		ArrayList<Vertex> cluster1 = new ArrayList<Vertex>();
		ArrayList<Vertex> cluster2 = new ArrayList<Vertex>();
		ArrayList<Vertex> cluster3 = new ArrayList<Vertex>();
		cluster1.add(vertexA);
		cluster2.add(vertexB);
		cluster3.add(vertexC);
		ArrayList<ArrayList<Vertex>> clusters = new ArrayList<ArrayList<Vertex>>();
		clusters.add(cluster1);
		clusters.add(cluster2);
		clusters.add(cluster3);
		KMeansClusterer kMeansClusterer = new KMeansClusterer(clusters, vertexSet);
		
	  // create a new position analysis with the secondary constructor
		// so that any orbitsFinders/kMeansClusteres can be fed in
		// not just ones generated from graphs
		PositionAnalysis positionAnalysis 
		  = new PositionAnalysis(orbitFinder, kMeansClusterer);
		
	  // test orbit-cluster equivalence, and clustering similarity
		double equivalence = positionAnalysis.getOrbitsToClustersEquivalence();
		double similarity = positionAnalysis.getSimilarityOfClusters();
		assertEquals(1.0, equivalence);
		assertEquals(1.0, similarity);
		
	} // testEquivMeasures

} // class TestEquivMeasures08
