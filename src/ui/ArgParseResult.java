package ui;

import java.io.File;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class ArgParseResult
{
	public ArgParseResult(){}
	
	public String algorithmName = null;
	public File inputFile = null;
	public File outputFolder = null;
	public File keyFile = null;
	public boolean usingPassword = false;
}
