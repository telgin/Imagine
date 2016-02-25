package product;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 * @param <t_productMode>
 */
public interface ProductReaderFactory<t_productMode extends ProductReader>
{
	/**
	 * @update_comment
	 * @return
	 */
	t_productMode createReader();
}
