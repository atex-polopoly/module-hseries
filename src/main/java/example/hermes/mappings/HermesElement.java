package example.hermes.mappings;

import java.util.HashMap;

public class HermesElement{
	private String name;
	private String hermesPk;
	private int hermesType; // HermesTypesEnum value
	private String hermesDataType;
	private String hermesVariant;
	private String hermesLevelId;
	private String resourceContentId;
	private HashMap<String, Object> metadata = new HashMap<String, Object>();
	
	
	
	public HermesElement() {
		super();
	}
	
	public HermesElement(String name, int hermesType, String hermesLevelId, String hermesDataType) {
		super();
		this.name = name;
		this.hermesType = hermesType;
		this.hermesLevelId = hermesLevelId;
		this.hermesDataType = hermesDataType;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHermesPk() {
		return hermesPk;
	}
	public void setHermesPk(String hermesPk) {
		this.hermesPk = hermesPk;
	}
	public int getHermesType() {
		return hermesType;
	}
	public void setHermesType(int hermesType) {
		this.hermesType = hermesType;
	}
	public String getHermesVariant() {
		return hermesVariant;
	}
	public void setHermesVariant(String hermesVariant) {
		this.hermesVariant = hermesVariant;
	}
	public String getHermesDataType() {
		return hermesDataType;
	}

	public void setHermesDataType(String hermesDataType) {
		this.hermesDataType = hermesDataType;
	}

	public String getHermesLevelId() {
		return hermesLevelId;
	}

	public void setHermesLevelId(String hermesLevelId) {
		this.hermesLevelId = hermesLevelId;
	}

	public String getResourceContentId() {
		return resourceContentId;
	}

	public void setResourceContentId(String resourceContentId) {
		this.resourceContentId = resourceContentId;
	}

	public HashMap<String, Object> getMetadata() {
		return metadata;
	}
	public void setMetadata(HashMap<String, Object> metadata) {
		this.metadata = metadata;
	}

}