package example.hermes.composer;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.atex.onecms.content.*;
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


public class StandardArticleHermesComposer implements ContentComposer<Object, Object, Object>{

	private static Logger logger = Logger.getLogger(StandardArticleHermesComposer.class.getName());
	private static final Subject SYSTEM_SUBJECT = new Subject("98", null);
	private static final String hermesAspectName = HermesElementAspect.class.getAnnotation(AspectDefinition.class).value()[0];

	private static final String RESOURCE_TOP_IMAGE = "topImage";
	private static final String RESOURCE_IMAGES = "images";
	private static final String RESOURCE_MULTIMEDIA = "multimedia";

	private static final String CONTENT_TYPE_IMAGE = "atex.dam.standard.Image";
	private static final String CONTENT_TYPE_PROD_IMAGE = "com.atex.nosql.image.ImageContentDataBean";

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
			List<HermesElement> hermesElements = null;

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

					hermesElements = hermesElementAspect.getElements();


					HermesElement spElement = new HermesElement(ElementNameEnum.ARTICLE.getName(), ElementNameEnum.ARTICLE.getPrintName(), HermesTypesEnum.STORY_PACKAGE.getValue(), HermesConstants.HERMES_LEVEL_SP, hermesDataType);
					//					spElement.getMetadata().put("WEB/AUTHOR", original.getByline());
					hermesElements.add(spElement);

					HermesElement titleElement = new HermesElement(ElementNameEnum.HEADLINE.getName(), ElementNameEnum.HEADLINE.getPrintName(), HermesTypesEnum.HEADER.getValue(), HermesConstants.HERMES_LEVEL_TEXTS, hermesDataType);
					// titleElement.getMetadata().put("WEB/SUBTYPE", "main title");
					hermesElements.add(titleElement);

					hermesElements.add(new HermesElement(ElementNameEnum.LEAD.getName(), ElementNameEnum.LEAD.getPrintName(), HermesTypesEnum.CAPTION.getValue(), HermesConstants.HERMES_LEVEL_TEXTS, hermesDataType));
					hermesElements.add(new HermesElement(ElementNameEnum.TEXT.getName(), ElementNameEnum.TEXT.getPrintName(), HermesTypesEnum.TEXT.getValue(), HermesConstants.HERMES_LEVEL_TEXTS, hermesDataType));

				}else{
					/*
					 * These aspects will always be updated to handle use case such as:
					 * - image added to the article
					 * - image removed from the article
					 */

					hermesElementAspect = (HermesElementAspect)content.getAspect(hermesAspectName).getData();
					hermesElements = hermesElementAspect.getElements();

					// Remove resources, then add new resources, to account for deleted resources
					removeResources(hermesElementAspect);

				}

				// add resources
				addResources (cm, contentBean, hermesElements, hermesDataType, RESOURCE_IMAGES, "", false);
				addResources (cm, contentBean, hermesElements, hermesDataType, RESOURCE_MULTIMEDIA, "", false);


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


	/**
	 * Create an HermesElement of type image or of type httplink.
	 * @param contentBean
	 * @param hermesElements
	 * @param hermesDataType
	 * @param resourceListName
	 * @param subtype
	 * @param firstOnly
	 */
	private void addResources (ContentManager contentManager, Model contentBean, List<HermesElement> hermesElements, String hermesDataType,
							   String resourceListName, String subtype, boolean firstOnly) {

		logger.log(Level.FINE, "Adding resource of subtype {0} to {1}", new Object[] {subtype,resourceListName} );
		if(contentBean.getChild(resourceListName)!=null && ((List<?>)contentBean.getChild(resourceListName)).toArray() != null){

			@SuppressWarnings({ "unchecked", "rawtypes" })
			com.atex.onecms.content.ContentId[] resourcesArray = (com.atex.onecms.content.ContentId[])
					((List)contentBean.getChild(resourceListName)).toArray(new com.atex.onecms.content.ContentId[0]);
			ArrayList<com.atex.onecms.content.ContentId> resources =
					new ArrayList<com.atex.onecms.content.ContentId>(Arrays.asList(resourcesArray));

			// Add resources to hermes SP
			for (com.atex.onecms.content.ContentId contentId : resources) {
				// Get content type: image or link
				ContentVersionId cvid = contentManager.resolve(contentId, SYSTEM_SUBJECT);
				ContentResult<Object> cr = contentManager.get(cvid, null, Object.class, null, SYSTEM_SUBJECT);
				String contentType = cr.getContent().getContentDataType();

				logger.log(Level.FINE, "Resource with content data type {0}", contentType);

				String elementName = ElementNameEnum.HTTP_LINK.getName();
				String elementPrintName = ElementNameEnum.HTTP_LINK.getPrintName();

				boolean isImage = (contentType != null && (contentType.equals(CONTENT_TYPE_IMAGE)
														|| (contentType.equals(CONTENT_TYPE_PROD_IMAGE))));

				int hermesType = HermesTypesEnum.HTTP_LINK.getValue();
				if (isImage)  {
					elementName = ElementNameEnum.IMAGE.getName() + "_"+getContentId(contentId);
					elementPrintName = ElementNameEnum.IMAGE.getPrintName();
					hermesType = HermesTypesEnum.IMAGE.getValue();
				}
				String resourceId  = contentId.getDelegationId()  + ':' + contentId.getKey();
				HermesElement resourceElement = new HermesElement(elementName, elementPrintName, hermesType, HermesConstants.HERMES_LEVEL_IMAGES, hermesDataType, subtype);
				resourceElement.setResourceContentId(resourceId);

				// Add metadata for links
				if (!isImage) {
					Map<String, String> metadata = new HashMap<String, String>();
					metadata.put("WEB.CONTENT_ID",resourceId);
					resourceElement.setMetadata(metadata);
				}

				hermesElements.add(resourceElement);

				logger.log(Level.FINE, "addResources: resourceElement {0} added", resourceElement.getName());
				if (firstOnly) {
					break;
				}
			}
		}

	}

	/**
	 * Remove image and http link elements from Hermes aspect
	 * @param hermesElementAspect
	 */
	private void removeResources (HermesElementAspect hermesElementAspect) {
		List<HermesElement> elements = hermesElementAspect.getElements();
		int size = elements.size();
		if (size > 0) {
			for(int i=size - 1; i >= 0; i--){
				HermesElement hermesElement = elements.get(i);
				logger.log(Level.FINE, "Resource named {0}", hermesElement.getName());
				if (hermesElement.getHermesType() == HermesTypesEnum.IMAGE.getValue() ||
						hermesElement.getHermesType() == HermesTypesEnum.HTTP_LINK.getValue()) {
					elements.remove(i);
					logger.log(Level.FINE, "Removing resouce #{0}", i);
				}
			}
		}
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


