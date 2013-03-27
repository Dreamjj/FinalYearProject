import java.util.ArrayList;

public class DegreeVector
{
	
  private ArrayList<Integer> degreeVector;

	public DegreeVector(ArrayList<Integer> requiredVector)
	{
		degreeVector = requiredVector;
	} // DegreeVector
	
	public ArrayList<Integer> getDegreeVector()
	{
		return degreeVector;
	} // getDegreeVector
	
	/* method to calculate similarity between two DegreeVectors */
	public double similarityWith(DegreeVector otherVector)
	{
		double similarity;
		
	  // if BOTH of the DegreeVectors are empty, return identical
		if (otherVector.getDegreeVector().size() == 0
				&& degreeVector.size() == 0)
			similarity = 1.0;
		
	  // if only one of the DegreeVectors are empty, return no similarity
		else if (otherVector.getDegreeVector().size() == 0
				     || degreeVector.size() == 0)
			similarity = 0.0;
		
		// otherwise, calculate similarity in proportion to equal elements
		else
		{
			// set the number of elements as the size of the largest DegreeVector
			int noOfElements;
			if (otherVector.getDegreeVector().size() > degreeVector.size())
				noOfElements = otherVector.getDegreeVector().size();
			else
				noOfElements = degreeVector.size();
			
			// create a variable to count the number of equal elements
			int noOfMatches = 0;
			
			// loop through all elements of this DegreeVector...
			// (using a variable to store where currently are in other DegreeVector
		  int otherIndex = 0;
		  for (int thisIndex = 0; thisIndex < degreeVector.size(); thisIndex++)
		  {
		  	// ... but only do work if other DegreeVector has elements left to check
		  	if (otherIndex < otherVector.getDegreeVector().size())
		  	{
		  		// if this element > other element
		  	  // decrement this index so that the next loop will use same index
		  		// and progress to the next other element
		  		if (degreeVector.get(thisIndex)
		  				.compareTo(otherVector.getDegreeVector().get(otherIndex)) > 0)
		  		{
		  			thisIndex--;
				  	otherIndex++;
		  		} // if
		  		
		  	  // if this element = other element
		  	  // count as a match and progress to the next other element 
		  		else if (degreeVector.get(thisIndex)
		  				     .compareTo(otherVector.getDegreeVector().get(otherIndex)) == 0)
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
		return "" + degreeVector;
	} // toString
	
} // class DegreeVector
