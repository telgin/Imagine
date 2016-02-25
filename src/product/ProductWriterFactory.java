package product;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 * @param <t_productMode>
 */
public interface ProductWriterFactory<t_productMode extends ProductWriter>
{
	/**
	 * @update_comment
	 * @return
	 */
	t_productMode createWriter();
}
