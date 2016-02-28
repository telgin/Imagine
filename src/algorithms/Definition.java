package algorithms;

import java.util.List;

import archive.ArchiveFactoryCreator;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * Defines an interface for algorithm definitions
 */
public interface Definition
{
	/**
	 * Gets the name of the algorithm
	 * @return The name
	 */
	public String getName();

	/**
	 * Creates a default algorithm, which is like a preset with
	 * all default values.
	 * @return The default algorithm
	 */
	public Algorithm constructDefaultAlgorithm();
	
	/**
	 * Gets the default presets defined in the definition. Default presets
	 * are presets which exist when the software is installed and are meant to
	 * offer some basic/common options for archive creation to the user without
	 * them having to make custom algorithms for the first run.
	 * @return The list of default algorithm presets.
	 */
	public List<Algorithm> getAlgorithmPresets();

	/**
	 * Gets the archive factory creator associated with this algorithm definition.
	 * @return The archive factory creator
	 */
	public ArchiveFactoryCreator getArchiveFactoryCreator();
}
