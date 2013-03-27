import junit.framework.TestCase;

import java.util.ArrayList;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TestClusteringsGraph00 extends TestCase
{
	private String directory;
	private UndirectedGraph<Vertex, DefaultEdge> inputGraph;
	private Vertex[] inputVertexSet;
	private OrbitFinder orbitFinder;
	private KMeansClusterer kMeanClusterer;
	
	// constructor
	public TestClusteringsGraph00(String name, String requiredDirectory)
	{
		super(name);
		directory = requiredDirectory + "\\graph00.gml";
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
	public void testEmptyGraphG00() throws Exception
	{
	  // test the orbits are as expected
		// -- exception (empty graph not allowed)
		try 
		{
		  orbitFinder = new OrbitFinder(inputGraph, "testOut.txt", 2);
		} // try
		catch (Exception exception)
		{
			// succeed quietly
		} // catch
		
	  // test the k-means clusterings are as expected
	  // -- exception (empty graph not allowed)
		try 
		{
		  kMeanClusterer = new KMeansClusterer(inputGraph, "testOut.txt", "testOut.csv", 0);
		} // try
		catch (Exception exception)
		{
			// succeed quietly
		} // catch
		
	} // testEmpty

} // class TestClusteringsGraph00
