package product;

public interface ProductFactory<t_productMode extends Product> {
	t_productMode create();
}
