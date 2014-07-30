package example.hermes.mappings;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class HermesObjectBean {

        /*

        // Relevant Hermes Object types for articles

        public static final short OBJ_TEXT				= 1;
        public static final short OBJ_HEADER			= 2;
        public static final short OBJ_DID				= 3;
        public static final short OBJ_TESTATINA			= 4;
        public static final short OBJ_TABLE				= 5;
        public static final short OBJ_SUMMARY			= 14;
        public static final short OBJ_CREDIT_BOX		= 16;
        public static final short OBJ_STORY_PACKAGE		= 17;

	*/

	@XmlElement
	private String  contentId	= null;
    @XmlElement
    private String  name     = null;    // used as object name in Hermes
	@XmlElement
	private String  text     = null;
	@XmlElement
	private String  header   = null;
	@XmlElement
	private String  caption	 = null;
	@XmlElement
	private String  creditbox   = null;
	@XmlElement
	private String  summary     = null;
	@XmlElement
	private String  fileurl	= null;
	@XmlElement
	private String    author	= null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getCreditbox() {
        return creditbox;
    }

    public void setCreditbox(String creditbox) {
        this.creditbox = creditbox;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getFileurl() {
        return fileurl;
    }

    public void setFileurl(String fileurl) {
        this.fileurl = fileurl;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }


}
