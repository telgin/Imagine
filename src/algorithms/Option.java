package algorithms;

public class Option {
	private String string;
	private String type;
	
	public Option(String type, String string) {
		this.setString(string);
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
