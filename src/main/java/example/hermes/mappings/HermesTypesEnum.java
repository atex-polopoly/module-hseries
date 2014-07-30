package example.hermes.mappings;

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

public enum HermesTypesEnum {
	
	TEXT(1), HEADER(2), CAPTION(3), TESTATINA(4), TABLE(5), IMAGE(6), SUMMARY(14), CREDIT_BOX(16), STORY_PACKAGE(17);
    
	private int value;

    private HermesTypesEnum(int value) {
            this.value = value;
    }

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

}
