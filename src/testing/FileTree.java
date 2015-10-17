package testing;

import java.io.File;

public interface FileTree {

	public File getRoot(File parent);
	
	public void create(File parent);
}
