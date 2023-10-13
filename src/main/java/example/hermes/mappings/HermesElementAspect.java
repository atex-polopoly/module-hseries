package example.hermes.mappings;

import java.util.ArrayList;
import java.util.List;

import com.atex.onecms.content.aspects.annotations.AspectDefinition;

@AspectDefinition("hermesElements")
public class HermesElementAspect {

	
	/*
	 *	"hermesElements": {
	        "_type": "example.hermes.mappings.HermesElementAspect",
	        "elements": [{
	        	"name" : "title",
	        	"hermesType" : "HEADER",
	        	"hermesPk" : "1323",
	        	"metadata": {} 
	        } ,
	        {
	        	"name" : "body",
	        	"hermesType" : "TEXT",
	        	"hermesPk" : "23232",
	        	"metadata": {} 
	        }  ,
	        {
	        	"name" : "lead",
	        	"hermesType" : "HEADER",
	        	"hermesPk" : "8789",
	        	"metadata": {} 
	        } ]
    },
	 */

	private int hermesContentType;
	private List<HermesElement> elements = new ArrayList<HermesElement>();
	private String hermesPageFormat;

	public String getHermesPageFormat() {
		return hermesPageFormat;
	}

	public void setHermesPageFormat(String hermesPageFormat) {
		this.hermesPageFormat = hermesPageFormat;
	}

	public int getHermesContentType() {
		return hermesContentType;
	}


	public void setHermesContentType(int hermesContentType) {
		this.hermesContentType = hermesContentType;
	}


	public HermesElementAspect() {
		super();
	}


	public List<HermesElement> getElements() {
		return elements;
	}



	public void setElements(List<HermesElement> elements) {
		this.elements = elements;
	}


	public HermesElement findElementByName(String name){
	
		List<HermesElement> elements = this.getElements();
		
		for(int i=0; i< elements.size(); i++){
			HermesElement hermesElement = elements.get(i);
			if(hermesElement.getName().equals(name)){
				return hermesElement;
			}
		}
		return null;
	}
}
