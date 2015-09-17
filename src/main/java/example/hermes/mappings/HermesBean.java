package example.hermes.mappings;

import java.io.Serializable;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class HermesBean  implements Serializable {
	public HermesBean() {
		this.metadata = new HashMap<String,String>();
	}

	public void add(String key,String value) {
		value = (value != null) ? value : "";
		this.metadata.put(key, value);
	}

	public HashMap<String, String> getMetadata() {
		return metadata;
	}

	public void setMetadata(HashMap<String, String> metadata) {
		this.metadata = metadata;
	}

	@XmlElement
	private HashMap<String,String> metadata = null;
	
	public static final String HE_WEB_METADATA_VIDEO_BRIGHTCOVE_ID 	   = "BC_ID";
	public static final String HE_WEB_METADATA_COLLECTION_GALLERY_LINK = "POLOPOLY_GALLERY_LINK";
	public static final String HE_WEB_METADATA_DESK_PREVIEW_LINK 	   = "DESK_PREVIEW_LINK";
	
	private static final long serialVersionUID = 1L;
}