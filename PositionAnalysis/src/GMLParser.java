import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class GMLParser
{
	
  private String filename;
  private ArrayList<Vertex> vertexList;
  private ArrayList<DefaultEdge> edgeList;
  private UndirectedGraph<Vertex, DefaultEdge> graph;

  /* method to create an undirected graph from an input GML file */
	public GMLParser(String requiredFilename) throws IOException
	{
	  filename = requiredFilename;
		
  	// create a new graph
	  graph = new SimpleGraph<Vertex, DefaultEdge>(DefaultEdge.class);
		
	  // create an arrayList to store the vertex instances created
	  // this is used to create the edges
	  ArrayList<Vertex> vertexList = new ArrayList<Vertex>();
		
	  // create a reader for the input file
	  BufferedReader in = new BufferedReader(new FileReader(filename));
		
	  // add the vertices and edges from the file into the graph
	  String line;
	  while ((line = in.readLine()) != null)
	  {
		  if (line.equals("  node"))
		  {
		    /* NOTE: vertices expected in the following format:
		     * 
		     * node
		     * [
		     *   id #
		     *   label "#"
		     * ]
		     * 
		     * where '#' is what needs to be recorded
		     */
		    in.readLine(); // reads in "[" - not needed
				
			  // note: after the split...
			  // ... indices 0-3 = whitespace, index 4 = "id"/"label", index 5 = #
			  String idString = in.readLine();
			  String[] idStrings = idString.split("\\s");
			  Integer newID = Integer.parseInt(idStrings[5]);
			  
			  // if there is a second line containing the word 'label'
			  // use this to set the vertex label, otherwise use the node's ID
			  String newLabel;
			  if ((line = in.readLine()).contains("label"))
			  {
			  	String labelString = line;
				  String[] labelStrings = labelString.split("\\s");
				  newLabel = labelStrings[5].substring(1, labelStrings[5].length() - 1);
			  } // if
			  else
			  	newLabel = newID.toString(); 
			  
			  Vertex newVertex = new Vertex(newID, newLabel);
			  vertexList.add(newVertex);
			  graph.addVertex(newVertex);
			} // if
			if (line.equals("  edge"))
			{
				/* NOTE: edges expected in the following format:
				 * 
				 * edge
				 * [
				 *   source #
				 *   target #
				 *   value 1
				 * ]
				 * 
				 * where '#' is what needs to be recorded
				 * ('value' is ignored because dealing with unweighted graphs)
				 */
				in.readLine(); // reads in "[" - not needed
			  String sourceString = in.readLine();
			  String[] sourceStrings = sourceString.split("\\s");
			  String targetString = in.readLine();
			  String[] targetStrings = targetString.split("\\s");
			  // note: after the split...
			  // ... indices 0-3 = whitespace, index 4 = "source"/"target", index 5 = #
			  
			  Integer newSource = Integer.parseInt(sourceStrings[5]);
			  Integer newTarget = Integer.parseInt(targetStrings[5]);
			  
			  graph.addEdge(vertexList.get(newSource), vertexList.get(newTarget));
			} // if
		} // while
	} // GMLParser
	
	public UndirectedGraph<Vertex, DefaultEdge> getGraph()
	{
		return graph;
	} // getVertexList
	
	public String getFilename()
	{
		return filename;
	} // getFilename
	
	public String toString()
	{
		return "" + filename;
	} // toString
	
} // class GMLParser
