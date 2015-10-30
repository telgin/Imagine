package product;

public interface ProductReaderFactory<t_productMode extends ProductReader>
{
	t_productMode createReader();
}
