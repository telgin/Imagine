package product;

public interface Product
{

	// generic methods
	public String getAlgorithmName();

	public int getAlgorithmVersionNumber();

	public void setUUID(byte[] uuid);

	public byte[] getUUID();

	// secure methods
	public void secureStream();
}