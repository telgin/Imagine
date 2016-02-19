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
			"testing/scratch/gui_products/1455838594412_0.png"
		};
		
		Imagine.run(args);
	}
}
