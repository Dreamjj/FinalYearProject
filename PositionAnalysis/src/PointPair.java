import java.util.ArrayList;

public class PointPair
{
	// a point pair consists of two points
	// and which clusters
  private Vertex pointA;
  private Vertex pointB;
  private ArrayList<Vertex> clusterY_pointA;
  private ArrayList<Vertex> clusterY_pointB;
  private ArrayList<Vertex> clusterZ_pointA;
  private ArrayList<Vertex> clusterZ_pointB;

	public PointPair(Vertex requiredPointA, Vertex requiredPointB,
			             ArrayList<Vertex> requiredClusterYPointA,            
			             ArrayList<Vertex> requiredClusterYPointB,
			             ArrayList<Vertex> requiredClusterZPointA,
			             ArrayList<Vertex> requiredClusterZPointB)
	{
		pointA = requiredPointA;
		pointB = requiredPointB;
		clusterY_pointA = requiredClusterYPointA;
		clusterY_pointB = requiredClusterYPointB;
		clusterZ_pointA = requiredClusterZPointA;
		clusterZ_pointB = requiredClusterZPointB;
	} // PointPair
	
	/* method to assign the similarity between the two points
	 * if the two points are clustered together in both clusterings
	 *   return 1 (clusterings are similar)
	 * if the two points are clustered separately in both clusterings
	 *   return 1 (clusterings are similar)
	 * else the two points are clustered together in one clustering
	 * and clustered separately in the other clustering
	 *   return 0 */
	public int assignmentSimilarity()
	{
		if (clusterY_pointA.equals(clusterY_pointB)
				&& clusterZ_pointA.equals(clusterZ_pointB))
			return 1;
		else if (!clusterY_pointA.equals(clusterY_pointB)
				     && !clusterZ_pointA.equals(clusterZ_pointB))
			return 1;
		else
			return 0;
	} // assignmentSimilarity
	
	public Vertex getPointA()
	{
		return pointA;
	} // getPointA
	
	public Vertex getPointB()
	{
		return pointB;
	} // getPointB
	
	public ArrayList<Vertex> getClusterYPointA()
	{
		return clusterY_pointA;
	} // getClusterYPointA
	
	public ArrayList<Vertex> getClusterYPointB()
	{
		return clusterY_pointB;
	} // getClusterYPointB
	
	public ArrayList<Vertex> getClusterZPointA()
	{
		return clusterZ_pointA;
	} // getClusterZPointA
	
	public ArrayList<Vertex> getClusterZPointB()
	{
		return clusterZ_pointB;
	} // getClusterZPointB
	
	public String toString()
	{
		return "(" + pointA + "-" + pointB + ")";
	} // toString

} // class PointPair
