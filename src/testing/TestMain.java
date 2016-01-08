package testing;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import testing.highlevel.ImageTest;
import testing.highlevel.CmdUITest;
import testing.highlevel.ImageOverlayTest;
import testing.highlevel.TextTest;
import util.test.ByteConversionTest;

@RunWith(Suite.class)
@SuiteClasses({
                   CmdUITest.class
				// ImageTest.class,
				// ByteConversionTest.class
				// TextTest.class,
				// ImageOverlayTest.class
})
public class TestMain
{
}
