package testing;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import testing.highlevel.FullPNGTest;
import testing.highlevel.StealthPNGTest;
import testing.highlevel.TextBlockTest;
import util.ByteConversionTest;

@RunWith(Suite.class)
@SuiteClasses({
	//FullPNGTest.class,
	//ByteConversionTest.class
	//TextBlockTest.class,
	StealthPNGTest.class
})
public class TestMain {}
