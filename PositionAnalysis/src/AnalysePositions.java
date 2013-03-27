public class AnalysePositions
{
	
  private static String inputFilename;
  private static int kValue;

	public static void main(String[] args) throws Exception
	{
		if (args.length < 1)
			throw new Exception("\nInput file not specified (as first argument)");
		if (args.length < 2)
			throw new Exception("\nk-value not specified (as second argument)");
		
		inputFilename = args[0];
		kValue = Integer.parseInt(args[1]);
		
		PositionAnalysis positionAnalysis 
		  = new PositionAnalysis(inputFilename, kValue);
	} // main

} // class AnalysePositions
