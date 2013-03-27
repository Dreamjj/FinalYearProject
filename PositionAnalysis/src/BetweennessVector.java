import java.util.ArrayList;

public class BetweennessVector
{
	
  private ArrayList<Double> betweennessVector;

	public BetweennessVector(ArrayList<Double> requiredVector)
	{
		betweennessVector = requiredVector;
	} // BetweennessVector
	
	public ArrayList<Double> getBetweennessVector()
	{
		return betweennessVector;
	} // getBetweennessVector
	
	/* method to calculate similarity between two BetweennessVectors */
	public double similarityWith(BetweennessVector otherVector)
	{
		double similarity;
		
	  // if BOTH of the BetweennessVectors are empty, return identical
		if (otherVector.getBetweennessVector().size() == 0
				&& betweennessVector.size() == 0)
			similarity = 1.0;
		
		// if only one of the BetweennessVectors are empty, return no similarity		
		else if (otherVector.getBetweennessVector().size() == 0
				     || betweennessVector.size() == 0)
			similarity = 0.0;
		
		// otherwise, calculate similarity in proportion to equal elements
		else
		{
			// set the number of elements as the size of the largest DegreeVector
			int noOfElements;
			if (otherVector.getBetweennessVector().size() > betweennessVector.size())
				noOfElements = otherVector.getBetweennessVector().size();
			else
				noOfElements = betweennessVector.size();
			
			// create a variable to count the number of equal elements
			int noOfMatches = 0;
			
			// loop through all elements of this DegreeVector...
			// (using a variable to store where currently are in other DegreeVector
		  int otherIndex = 0;
		  for (int thisIndex = 0; thisIndex < betweennessVector.size(); thisIndex++)
		  {
		  	// ... but only do work if other DegreeVector has elements left to check
		  	if (otherIndex < otherVector.getBetweennessVector().size())
		  	{
		  		// if this element > other element
		  	  // decrement this index so that the next loop will use same index
		  		// and progress to the next other element
		  		if (betweennessVector.get(thisIndex)
		  				.compareTo(otherVector.getBetweennessVector().get(otherIndex)) > 0)
		  		{
		  			thisIndex--;
				  	otherIndex++;
		  		} // if
		  		
		  	  // if this element = other element
		  	  // count as a match and progress to the next other element 
		  		else if (betweennessVector.get(thisIndex)
		  				     .compareTo(otherVector.getBetweennessVector().get(otherIndex)) == 0)
		  		{
		  			noOfMatches++;
		  			otherIndex++;
		  		} // else if
		  	} // if
		  } // for
		  
		  // the measure of similarity is the proportion of matches to elements
		  similarity = (double)noOfMatches / (double)noOfElements;
		} // else
		
		return similarity;
	} // similarityWith
	
	public String toString()
	{
		return "" + betweennessVector;
	} // toString

} // class BetweennessVector
