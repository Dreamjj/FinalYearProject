import org.jgrapht.*;
import org.jgrapht.graph.*;
import java.util.List;

public class GraphPathVector
{
	
  private List<GraphPath<Vertex, DefaultEdge>> graphPathVector;

	public GraphPathVector(List<GraphPath<Vertex, DefaultEdge>> requiredVector)
	{
		graphPathVector = requiredVector;
	} // GraphPathVector
	
	public List<GraphPath<Vertex, DefaultEdge>> getGraphPathVector()
	{
		return graphPathVector;
	} // getGraphPathVector
	
	public String toString()
	{
		return "" + graphPathVector;
	} // toString

} // class GraphPathVector
