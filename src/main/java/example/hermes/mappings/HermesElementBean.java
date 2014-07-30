package example.hermes.mappings;

import java.util.HashMap;

public class HermesElementBean {

	private String name;
	private String value;
	private HashMap<String, String> metadata = new HashMap<String, String>();
	
	
	public HermesElementBean() {
		super();
	}

	public HermesElementBean(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public HashMap<String, String> getMetadata() {
		return metadata;
	}
	public void setMetadata(HashMap<String, String> metadata) {
		this.metadata = metadata;
	}


	
}
