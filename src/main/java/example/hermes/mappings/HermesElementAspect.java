package example.hermes.mappings;

import java.util.ArrayList;

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
	private ArrayList<HermesElement> elements = new ArrayList<HermesElement>();


	public int getHermesContentType() {
		return hermesContentType;
	}


	public void setHermesContentType(int hermesContentType) {
		this.hermesContentType = hermesContentType;
	}


	public HermesElementAspect() {
		super();
	}


	public ArrayList<HermesElement> getElements() {
		return elements;
	}



	public void setElements(ArrayList<HermesElement> elements) {
		this.elements = elements;
	}


	public HermesElement findElementByName(String name){
	
		ArrayList<HermesElement> elements = this.getElements();
		
		for(int i=0; i< elements.size(); i++){
			HermesElement hermesElement = elements.get(i);
			if(hermesElement.getName().equals(name)){
				return hermesElement;
			}
		}
		return null;
	}
}
