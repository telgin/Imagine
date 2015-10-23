package testing;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import testing.highlevel.FullPNGTest;
import testing.highlevel.TextBlockTest;

@RunWith(Suite.class)
@SuiteClasses({
	FullPNGTest.class,
	TextBlockTest.class
})
public class TestMain {}
