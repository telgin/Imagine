package algorithms;

import java.util.List;

import product.ProductFactoryCreation;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public interface Definition
{
	/**
	 * @update_comment
	 * @return
	 */
	public String getName();

	/**
	 * @update_comment
	 * @return
	 */
	public Algorithm constructDefaultAlgorithm();
	
	/**
	 * @update_comment
	 * @return
	 */
	public List<Algorithm> getAlgorithmPresets();

	/**
	 * @update_comment
	 * @return
	 */
	public ProductFactoryCreation getProductFactoryCreation();
}
