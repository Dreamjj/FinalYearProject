public class Vertex
{
	
  private int ID;
  private String label;

	public Vertex(int requiredID, String requiredLabel)
	{
		ID = requiredID;
		label = requiredLabel;
	} // Vertex
	
	public int getID()
	{
		return ID;
	} // getID
	
	public String getLabel()
	{
		return label;
	} // getLabel
	
	public String toString()
	{
		return label;
	} // toString

} // class Vertex
