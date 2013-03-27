import junit.framework.TestCase;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TestEquivMeasures16 extends TestCase
{
	// constructor
	public TestEquivMeasures16(String name)
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
	public void testDifferingElements16() throws Exception
	{
		// create the vertices used in the test
		Vertex vertexA = new Vertex(0, "A");
		Vertex vertexB = new Vertex(1, "B");
		Vertex[] vertexSet1 = {vertexA};
		Vertex[] vertexSet2 = {vertexB};
		
		// create the orbit finder (using 2nd constructor to bypass calculations)
		ArrayList<Vertex> orbit1 = new ArrayList<Vertex>();
		orbit1.add(vertexB);
		ArrayList<ArrayList<Vertex>> orbits = new ArrayList<ArrayList<Vertex>>();
		orbits.add(orbit1);
		OrbitFinder orbitFinder = new OrbitFinder(orbits, vertexSet1);
		
	  // create the k-means clusterer (using 2nd constructor to bypass calculations)
		ArrayList<Vertex> cluster1 = new ArrayList<Vertex>();
		cluster1.add(vertexA);
		ArrayList<ArrayList<Vertex>> clusters = new ArrayList<ArrayList<Vertex>>();
		clusters.add(cluster1);
		KMeansClusterer kMeansClusterer = new KMeansClusterer(clusters, vertexSet2);
		
	  // create a new position analysis with the secondary constructor
		// so that any orbitsFinders/kMeansClusteres can be fed in
		// not just ones generated from graphs
		try
		{
		PositionAnalysis positionAnalysis 
		  = new PositionAnalysis(orbitFinder, kMeansClusterer);
		} // try
		catch (Exception exception)
		{
			// succeed quietly
		} // catch
		
	} // testDifferingElements

} // class TestEquivMeasures16
