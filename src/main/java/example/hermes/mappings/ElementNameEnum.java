package example.hermes.mappings;

/*

	Print name references

*/

public enum ElementNameEnum {

	ARTICLE("article", "article"), TEXT("body", "Text"), HEADLINE("headline", "Headline"), LEAD("lead", "Lead"), IMAGE("image", "image"), DESCRIPTION("description","description");

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
