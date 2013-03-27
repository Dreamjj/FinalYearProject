import junit.framework.Test;
import junit.framework.TestSuite;


public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for default package");
		//$JUnit-BEGIN$
		
		// test clusterings with a variety of graphs
		String directory = "C:\\Users\\William\\Documents\\University\\3rd Year Project\\Eclipse\\PositionAnalysis\\src\\Graphs";
		suite.addTest(new TestClusteringsGraph00("testEmptyGraphG00", directory));
		suite.addTest(new TestClusteringsGraph01_1("testClusteringsG01_1", directory));
		suite.addTest(new TestClusteringsGraph01_2("testInputWithLoopG01_2", directory));
		suite.addTest(new TestClusteringsGraph02_1("testClusteringsG02_1", directory));
		suite.addTest(new TestClusteringsGraph02_2("testClusteringsG02_2", directory));
		suite.addTest(new TestClusteringsGraph02_3("testClusteringsG02_3", directory));
		suite.addTest(new TestClusteringsGraph03_1("testClusteringsG03_1", directory));
		suite.addTest(new TestClusteringsGraph03_2("testClusteringsG03_2", directory));
		suite.addTest(new TestClusteringsGraph03_3("testClusteringsG03_3", directory));
		suite.addTest(new TestClusteringsGraph03_4("testInputWithLoopG03_4", directory));
		suite.addTest(new TestClusteringsGraph04_1("testClusteringsG04_1", directory));
		suite.addTest(new TestClusteringsGraph04_2("testClusteringsG04_2", directory));
		suite.addTest(new TestClusteringsGraph04_3("testClusteringsG04_3", directory));
		suite.addTest(new TestClusteringsGraph04_4("testClusteringsG04_4", directory));
		suite.addTest(new TestClusteringsGraph04_5("testClusteringsG04_5", directory));
		suite.addTest(new TestClusteringsGraph04_6("testClusteringsG04_6", directory));
		suite.addTest(new TestClusteringsGraph09_1("testClusteringsG09_1", directory));
		suite.addTest(new TestClusteringsGraph10_1("testClusteringsG10_1", directory));
		suite.addTest(new TestClusteringsGraph10_2("testClusteringsG10_2", directory));
		suite.addTest(new TestClusteringsGraph10_3("testClusteringsG10_3", directory));
		
		// test equivalence/similarity measures using a variety of clusterings
		suite.addTest(new TestEquivMeasures01("testEquivMeasures01"));
		suite.addTest(new TestEquivMeasures02("testEquivMeasures02"));
		suite.addTest(new TestEquivMeasures03("testEquivMeasures03"));
		suite.addTest(new TestEquivMeasures04("testEquivMeasures04"));
		suite.addTest(new TestEquivMeasures05("testEquivMeasures05"));
		suite.addTest(new TestEquivMeasures06("testEquivMeasures06"));
		suite.addTest(new TestEquivMeasures07("testEquivMeasures07"));
		suite.addTest(new TestEquivMeasures08("testEquivMeasures08"));
		suite.addTest(new TestEquivMeasures09("testEquivMeasures09"));
		suite.addTest(new TestEquivMeasures10("testEquivMeasures10"));
		suite.addTest(new TestEquivMeasures11("testEquivMeasures11"));
		suite.addTest(new TestEquivMeasures12("testEquivMeasures12"));
		suite.addTest(new TestEquivMeasures13("testEquivMeasures13"));
		suite.addTest(new TestEquivMeasures14("testEquivMeasures14"));
		suite.addTest(new TestEquivMeasures15("testEquivMeasures15"));
		suite.addTest(new TestEquivMeasures16("testDifferingElements16"));
		suite.addTest(new TestEquivMeasures17("testDifferingElements17"));

		//$JUnit-END$
		return suite;
	}

}
