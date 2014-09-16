package example.hermes.composer;

import java.util.ArrayList;
import java.util.Collection;

import com.atex.onecms.content.ContentManager;
import com.atex.onecms.content.ContentResult;
import com.atex.onecms.content.ContentWrite;
import com.atex.onecms.content.Subject;
import com.atex.onecms.content.aspects.Aspect;
import com.atex.onecms.content.aspects.annotations.AspectDefinition;
import com.atex.onecms.content.mapping.ContentComposer;
import com.atex.onecms.content.mapping.Context;
import com.atex.onecms.content.mapping.Request;
import com.atex.standard.article.ArticleBean;
import com.polopoly.cm.ContentId;
import com.polopoly.cm.ContentIdFactory;

import example.hermes.mappings.HermesConstants;
import example.hermes.mappings.HermesElement;
import example.hermes.mappings.HermesElementAspect;
import example.hermes.mappings.HermesTypesEnum;

public class StandardArticleHermesComposer implements ContentComposer<ArticleBean, ArticleBean, Object>{

	private static final Subject SYSTEM_SUBJECT = new Subject("98", null);
	private static final String hermesAspectName = HermesElementAspect.class.getAnnotation(AspectDefinition.class).value()[0];
	
	
	@Override
	public ContentResult<ArticleBean> compose(ContentResult<ArticleBean> articleBeanDataResult,
			String s, Request request, Context<Object> context) {

		ArticleBean original = articleBeanDataResult.getContent().getContentData();
		

		ContentResult<ArticleBean> res = new ContentResult<ArticleBean>(articleBeanDataResult, original);

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
				if(articleBeanDataResult.getContent().getAspect(hermesAspectName) == null){
					hermesElementAspect = new HermesElementAspect();
					hermesElementAspect.setHermesContentType(HermesTypesEnum.STORY_PACKAGE.getValue());
					
					ArrayList<HermesElement> hermesElements = hermesElementAspect.getElements();
					
					
					HermesElement spElement = new HermesElement("article", HermesTypesEnum.STORY_PACKAGE.getValue(), HermesConstants.HERMES_LEVEL_SP, hermesDataType);
//					spElement.getMetadata().put("WEB/AUTHOR", original.getByline());
					hermesElements.add(spElement);
					
					HermesElement titleElement = new HermesElement("title", HermesTypesEnum.HEADER.getValue(), HermesConstants.HERMES_LEVEL_TEXTS, hermesDataType);
					titleElement.getMetadata().put("WEB/SUBTYPE", "main title");
					hermesElements.add(titleElement);
					
					hermesElements.add(new HermesElement("lead", HermesTypesEnum.HEADER.getValue(), HermesConstants.HERMES_LEVEL_TEXTS, hermesDataType));
					hermesElements.add(new HermesElement("body", HermesTypesEnum.TEXT.getValue(), HermesConstants.HERMES_LEVEL_TEXTS, hermesDataType));
					
					
					ArrayList<ContentId> images = (ArrayList<ContentId>)original.getImages();
					for (ContentId contentId : images) {
						HermesElement imageElement = new HermesElement(HermesConstants.IMAGE_PREFIX+contentId.getContentIdString(), HermesTypesEnum.IMAGE.getValue(), HermesConstants.HERMES_LEVEL_IMAGES, hermesDataType);
						imageElement.setResourceContentId(contentId.getContentIdString());
						hermesElements.add(imageElement);
					}					
					

				}else{
					/*
					 * These aspects will always be updated to handle use case such as:
					 * - image added to the article
					 * - image removed from the article
					 */
					
					hermesElementAspect = (HermesElementAspect)articleBeanDataResult.getContent().getAspect(hermesAspectName).getData();
					
					ArrayList<ContentId> images = (ArrayList<ContentId>)original.getImages();

					// remove images which are not part of the article anymore
					
					ArrayList<HermesElement> elements = hermesElementAspect.getElements();
				
					for(int i=0; i< elements.size(); i++){
				
						HermesElement hermesElement = elements.get(i);
						if(hermesElement.getName().startsWith(HermesConstants.IMAGE_PREFIX)){
							
							String imageContentId = hermesElement.getName().replaceAll(HermesConstants.IMAGE_PREFIX, "");
					
							ContentId ici = ContentIdFactory.createContentId(imageContentId);
							if(!images.contains(ici)){
								elements.remove(i);
							}
						}
					}
					
					
					for (ContentId contentId : images) {
						if(hermesElementAspect.findElementByName(HermesConstants.IMAGE_PREFIX+contentId.getContentIdString()) == null){							
							HermesElement imageElement = new HermesElement(HermesConstants.IMAGE_PREFIX+contentId.getContentIdString(), 
																HermesTypesEnum.IMAGE.getValue(), HermesConstants.HERMES_LEVEL_IMAGES, hermesDataType);
							imageElement.setResourceContentId(contentId.getContentIdString());
							hermesElementAspect.getElements().add(imageElement);
						}
					}
				}
				/*
				 * End Polopoly To Hermes Mapping
				 */				
				
				
				/*
				 * Create aspects for hermes mapping
				 */
				ContentWrite<ArticleBean> cw = new ContentWrite<>(articleBeanDataResult.getContent());
				cw.setAspect(hermesAspectName, hermesElementAspect);
				
				// update/create only the hermesElement aspects in couchbase
				ContentResult<ArticleBean> updatedAspects = cm.update(articleBeanDataResult.getContent().getId().getContentId(), cw, SYSTEM_SUBJECT);
				
				Collection<Aspect> aspects = new ArrayList<Aspect>();	// create an empty aspects collection because the actual aspects collection is unmodifiable 
				aspects.addAll(articleBeanDataResult.getContent().getAspects());	// add all aspects, plus hermesElements just updated
				
				if(articleBeanDataResult.getContent().getAspect(hermesAspectName) != null)	// in case of existing hermesElements aspects clean existing
					aspects.remove(articleBeanDataResult.getContent().getAspect(hermesAspectName));
				
				aspects.addAll(updatedAspects.getContent().getAspects());
				res = new ContentResult<ArticleBean>(res, original, aspects);
		
			}


			
			

		}catch(Exception e){
			e.printStackTrace();
		}
		
		return res;

	}

}
