package data;

import runner.Runner;
import util.Hashing;

public class PasswordKey implements Key{
	private boolean secure;
	private String name;
	private String groupName;
	private byte[] keyHash;

	public PasswordKey(String keyName, String groupName)
	{
		this.name = keyName;
		this.groupName = groupName;
		secure = true;
	}

	@Override
	public boolean isSecure() {
		return secure;
	}

	@Override
	public byte[] getKeyHash() {
		if (keyHash == null)
		{
			fetchKey();
		}
		
		return keyHash;
	}
	
	private void fetchKey() {
		if (keyHash == null)
		{
			keyHash = Hashing.hash(Runner.getActiveGUI().promptKey(name, groupName).getBytes());
		}
	}

	@Override
	public String getName() {
		return name;
	}

}
