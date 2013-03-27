public class GreatestCoverageDetail 
{
  private double coverage;
  private int clusterIndex;
	
  /* constructor method */
	public GreatestCoverageDetail(double requiredCoverage,
			                            int requiredClusterIndex) 
	{
		coverage = requiredCoverage;
		clusterIndex = requiredClusterIndex;
	} // GreatestCoverageDetail
	
	public double getCoverage()
	{
		return coverage;
	} // getCoverage
	
	public int getClusterIndex()
	{
		return clusterIndex;
	} // getClusterIndex
}
