package data;

public class NullKey implements Key{

	public NullKey(){}
	
	@Override
	public boolean isSecure() {
		return false;
	}

	@Override
	public byte[] getKeyHash() {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

}
