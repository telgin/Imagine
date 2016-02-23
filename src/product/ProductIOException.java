package product;

import java.io.IOException;

public class ProductIOException extends IOException
{
	private static final long serialVersionUID = -2058437202993005146L;

	public ProductIOException(){}
	
	public ProductIOException(String message)
	{
		super(message);
	}
}
