package algorithms;

import java.util.List;

import product.ProductFactoryCreation;

public interface Definition
{
	public static final String PARAM_STRING_TYPE = "string";
	public static final String PARAM_INT_TYPE = "int";
	public static final String PARAM_DECIMAL_TYPE = "decimal";
	public static final String PARAM_FILE_TYPE = "file";
	
	public String getName();
	
	public String getDescription();

	public Algorithm getDefaultAlgorithm();

	public Algorithm getAlgorithmSpec();
	
	public List<Algorithm> getAlgorithmPresets();

	public ProductFactoryCreation getProductFactoryCreation();
}
