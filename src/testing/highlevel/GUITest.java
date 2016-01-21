package testing.highlevel;

import org.junit.Test;

import system.Imagine;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class GUITest
{
	@Test
	public void testOpenArchive()
	{
		String[] args = new String[]
		{
			"--gui", "--open", "-i",
			"testing/bank/image_basic_sample/imagine_1453074440957_0.png"
		};
		
		Imagine.run(args);
		
	}
}
