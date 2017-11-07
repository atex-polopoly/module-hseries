package example.hermes.composer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.atex.onecms.content.Content;
import com.atex.onecms.content.ContentManager;
import com.atex.onecms.content.ContentResult;
import com.atex.onecms.content.ContentWrite;
import com.atex.onecms.content.ContentWriteBuilder;
import com.atex.onecms.content.Subject;
import com.atex.onecms.content.aspects.annotations.AspectDefinition;
import com.atex.onecms.content.mapping.ContentComposer;
import com.atex.onecms.content.mapping.Context;
import com.atex.onecms.content.mapping.Request;
import com.polopoly.cm.ContentIdFactory;
import com.polopoly.model.Model;
import com.polopoly.model.ModelDomain;
import com.polopoly.model.ModelListBase;
import com.polopoly.model.PojoAsModel;

import example.hermes.mappings.*;
import com.atex.onecms.content.IdUtil;


public class StandardArticleHermesComposer implements ContentComposer<Object, Object, Object>{

	private static final Subject SYSTEM_SUBJECT = new Subject("98", null);
	private static final String hermesAspectName = HermesElementAspect.class.getAnnotation(AspectDefinition.class).value()[0];

	@Override
	public ContentResult<Object> compose(ContentResult<Object> articleBeanDataResult,
			String s, Request request, Context<Object> context) {

		ModelDomain modelDomain = context.getModelDomain();
		Content<Object>  content = articleBeanDataResult.getContent();
		Model contentBean = new PojoAsModel(modelDomain, content.getContentData());


		ContentResult<Object> res = new ContentResult<Object>(articleBeanDataResult, contentBean);

		try{
			ContentManager cm = context.getContentManager();
			
			

			String hermesDataType = "NewsRoom";

			HermesElementAspect hermesElementAspect = null;

			/*
			 *  variant: hermesStory
			 */
			if(s.equals("hermes")){

				/*
				 * create aspects for hermes
				 * 
				 * 
				 */

				/*
				 * Start Polopoly To Hermes Mapping
				 */
				if(content.getAspect(hermesAspectName) == null){
					hermesElementAspect = new HermesElementAspect();
					hermesElementAspect.setHermesContentType(HermesTypesEnum.STORY_PACKAGE.getValue());

					List<HermesElement> hermesElements = hermesElementAspect.getElements();


					HermesElement spElement = new HermesElement(ElementNameEnum.ARTICLE.getName(), ElementNameEnum.ARTICLE.getPrintName(), HermesTypesEnum.STORY_PACKAGE.getValue(), HermesConstants.HERMES_LEVEL_SP, hermesDataType);
					//					spElement.getMetadata().put("WEB/AUTHOR", original.getByline());
					hermesElements.add(spElement);

					HermesElement titleElement = new HermesElement(ElementNameEnum.HEADLINE.getName(), ElementNameEnum.HEADLINE.getPrintName(), HermesTypesEnum.HEADER.getValue(), HermesConstants.HERMES_LEVEL_TEXTS, hermesDataType);
					// titleElement.getMetadata().put("WEB/SUBTYPE", "main title");
					hermesElements.add(titleElement);

					hermesElements.add(new HermesElement(ElementNameEnum.LEAD.getName(), ElementNameEnum.LEAD.getPrintName(), HermesTypesEnum.CAPTION.getValue(), HermesConstants.HERMES_LEVEL_TEXTS, hermesDataType));
					hermesElements.add(new HermesElement(ElementNameEnum.TEXT.getName(), ElementNameEnum.TEXT.getPrintName(), HermesTypesEnum.TEXT.getValue(), HermesConstants.HERMES_LEVEL_TEXTS, hermesDataType));

					if(contentBean.getChild("images")!=null 
							&& contentBean.getChild("images") instanceof ModelListBase
							&& ((ModelListBase)contentBean.getChild("images")).toArray() != null){	
						
						Object[] imgArray = ((ModelListBase)contentBean.getChild("images")).toArray();

						for (Object obj : imgArray) {
							String strContentId = getContentId(obj);
															
							HermesElement imageElement = new HermesElement(ElementNameEnum.IMAGE.getName(), ElementNameEnum.IMAGE.getPrintName(), HermesTypesEnum.IMAGE.getValue(), HermesConstants.HERMES_LEVEL_IMAGES, hermesDataType);
							imageElement.setResourceContentId(strContentId);
							hermesElements.add(imageElement);
						}					
					}


				}else{
					/*
					 * These aspects will always be updated to handle use case such as:
					 * - image added to the article
					 * - image removed from the article
					 */

					hermesElementAspect = (HermesElementAspect)content.getAspect(hermesAspectName).getData();

					if(contentBean.getChild("images")!=null 
							&& contentBean.getChild("images") instanceof ModelListBase
							&& ((ModelListBase)contentBean.getChild("images")).toArray() != null){	
						
				
						Object[] imgArray = ((ModelListBase)contentBean.getChild("images")).toArray();
						ArrayList<Object> images = new ArrayList<Object>(Arrays.asList(imgArray));
						
						// remove images which are not part of the article anymore

						List<HermesElement> elements = hermesElementAspect.getElements();

						for(int i=0; i< elements.size(); i++){

							HermesElement hermesElement = elements.get(i);
							//if(hermesElement.getName().startsWith(HermesConstants.IMAGE_PREFIX)){
							if(hermesElement.getName().equals(ElementNameEnum.IMAGE.getName())){

								String imageContentId = hermesElement.getResourceContentId();

								Object idContent = null;
								if(imageContentId.startsWith("onecms")){
									com.atex.onecms.content.ContentId cOneCmsId = IdUtil.fromString(imageContentId);
									idContent = cOneCmsId;
								}
								else{
									com.polopoly.cm.ContentId ici = ContentIdFactory.createContentId(imageContentId);
									idContent = ici;
								}						
								
								if(!images.contains(idContent)){
									elements.remove(i);
								}
							}
						}

						//for (ContentId contentId : images) {
						for (Object obj : images) {
							String strContentId = getContentId(obj);
											
							if(hermesElementAspect.findElementByName(HermesConstants.IMAGE_PREFIX+strContentId) == null){							
								HermesElement imageElement = new HermesElement(HermesConstants.IMAGE_PREFIX+strContentId, ElementNameEnum.IMAGE.getPrintName(),
										HermesTypesEnum.IMAGE.getValue(), HermesConstants.HERMES_LEVEL_IMAGES, hermesDataType);
								imageElement.setResourceContentId(strContentId);
								hermesElementAspect.getElements().add(imageElement);
							}
						}
					}
				}
				/*
				 * End Polopoly To Hermes Mapping
				 */				


				/*
				 * Create aspects for hermes mapping
				 */
				ContentWriteBuilder<Object> cwb = new ContentWriteBuilder<Object>();

				cwb.mainAspect(content.getContentAspect());
				cwb.aspect(hermesAspectName, hermesElementAspect);
				cwb.origin(content);
				ContentWrite<Object> cw = cwb.buildUpdate();		
				
				res =  new ContentResult<Object>(articleBeanDataResult, cw.getContentData(), cw.getAspects()); 

			}

		}catch(Exception e){
			e.printStackTrace();
		}

		return res;

	}
	
	private String getContentId(Object obj){
		
		String strContentId = null;
		
		if(obj instanceof com.polopoly.cm.ContentId){
			com.polopoly.cm.ContentId cId = (com.polopoly.cm.ContentId)obj;
			strContentId = cId.getContentIdString();
		}
		else{
			com.atex.onecms.content.ContentId cOneCmsId = (com.atex.onecms.content.ContentId)obj;
			strContentId = cOneCmsId.getDelegationId() + ":" + cOneCmsId.getKey();
		}
		
		return strContentId;
	}
	
}


