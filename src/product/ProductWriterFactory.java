package product;

public interface ProductWriterFactory<t_productMode extends ProductWriter>
{
	t_productMode createWriter();
}
