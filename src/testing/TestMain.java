package testing;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import testing.highlevel.CmdUITest;

@RunWith(Suite.class)
@SuiteClasses({
                   CmdUITest.class
				// ByteConversionTest.class
})
public class TestMain
{
}
