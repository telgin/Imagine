package algorithms.text;

import algorithms.Algorithm;
import key.Key;
import product.ProductReaderFactory;
import product.ProductWriterFactory;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class TextFactory implements ProductReaderFactory<TextReader>,
	ProductWriterFactory<TextWriter>
{
	private Algorithm s_algo;
	private Key s_key;

	/**
	 * @update_comment
	 * @param p_algo
	 * @param p_key
	 */
	public TextFactory(Algorithm p_algo, Key p_key)
	{
		this.s_algo = p_algo;
		this.s_key = p_key;
	}

	/* (non-Javadoc)
	 * @see product.ProductWriterFactory#createWriter()
	 */
	@Override
	public TextWriter createWriter()
	{
		return new TextWriter(s_algo, s_key);
	}

	/* (non-Javadoc)
	 * @see product.ProductReaderFactory#createReader()
	 */
	@Override
	public TextReader createReader()
	{
		return new TextReader(s_algo, s_key);
	}

}
