package ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import system.CmdAction;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class ArgParseResult
{
	public ArgParseResult(){}
	
	public String presetName = null;
	public List<File> inputFiles = new ArrayList<File>();
	public List<String[]> parameters = new ArrayList<String[]>();
	public File outputFolder = null;
	public File keyFile = null;
	public File resultFile = null;
	public boolean usingPassword = false;
	public boolean guiMode = false;
	public CmdAction action = null;
	public boolean usePassword = false;
}
