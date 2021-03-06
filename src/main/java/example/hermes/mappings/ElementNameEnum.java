package example.hermes.mappings;

/*

	Print name references

*/

public enum ElementNameEnum {

	ARTICLE("article", "article"),
	TEXT("body", "Text"),
	HEADLINE("headline", "Headline"),
	LEAD("lead", "Lead"),
	IMAGE("image", "Image"),
	DESCRIPTION("description","description"),
	HTTP_LINK("http_link", "http_link");

	private String name;
	private String printName;

    private ElementNameEnum(String name, String printName) {
            this.name = name;
            this.printName = printName;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrintName() {
		return printName;
	}

	public void setPrintName(String printName) {
		this.printName = printName;
	}
}
