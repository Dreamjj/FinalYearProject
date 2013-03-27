import junit.framework.TestCase;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TestEquivMeasures15 extends TestCase
{
	// constructor
	public TestEquivMeasures15(String name)
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
	public void testEquivMeasures15() throws Exception
	{
		// create the vertices used in the test
		Vertex vertexA = new Vertex(0, "A");
		Vertex vertexB = new Vertex(1, "B");
		Vertex vertexC = new Vertex(2, "C");
		Vertex vertexD = new Vertex(3, "D");
		Vertex vertexV = new Vertex(4, "V");
		Vertex vertexW = new Vertex(5, "W");
		Vertex vertexX = new Vertex(6, "X");
		Vertex vertexY = new Vertex(7, "Y");
		Vertex vertexZ = new Vertex(8, "Z");
		Vertex[] vertexSet = {vertexA, vertexB, vertexC, vertexD,
				                  vertexV, vertexW, vertexX, vertexY, vertexZ};
		
		// create the orbit finder (using 2nd constructor to bypass calculations)
		ArrayList<Vertex> orbit1 = new ArrayList<Vertex>();
		ArrayList<Vertex> orbit2 = new ArrayList<Vertex>();
		orbit1.add(vertexA);
		orbit1.add(vertexB);
		orbit1.add(vertexC);
		orbit1.add(vertexD);
		orbit2.add(vertexV);
		orbit2.add(vertexW);
		orbit2.add(vertexX);
		orbit2.add(vertexY);
		orbit2.add(vertexZ);
		ArrayList<ArrayList<Vertex>> orbits = new ArrayList<ArrayList<Vertex>>();
		orbits.add(orbit1);
		orbits.add(orbit2);
		OrbitFinder orbitFinder = new OrbitFinder(orbits, vertexSet);
		
	  // create the k-means clusterer (using 2nd constructor to bypass calculations)
		ArrayList<Vertex> cluster1 = new ArrayList<Vertex>();
		ArrayList<Vertex> cluster2 = new ArrayList<Vertex>();
		cluster1.add(vertexA);
		cluster1.add(vertexB);
		cluster2.add(vertexC);
		cluster2.add(vertexD);
		cluster1.add(vertexV);
		cluster1.add(vertexW);
		cluster2.add(vertexX);
		cluster2.add(vertexY);
		cluster2.add(vertexZ);
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
		assertEquals(0.55, equivalence);
		assertEquals((16.0 / 36.0), similarity);
		
	} // testEquivMeasures

} // class TestEquivMeasures15
