package product;

import algorithms.Algorithm;
import key.Key;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public interface ProductFactoryCreation
{
	/**
	 * @update_comment
	 * @param p_algo
	 * @param p_key
	 * @return
	 */
	public ProductReaderFactory<? extends ProductReader>
		createReader(Algorithm p_algo, Key p_key);

	/**
	 * @update_comment
	 * @param p_algo
	 * @param p_key
	 * @return
	 */
	public ProductWriterFactory<? extends ProductWriter> 
		createWriter(Algorithm p_algo, Key p_key);
}
