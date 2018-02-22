package example.hermes.composer;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.atex.nosql.article.ArticleBean;
import com.atex.onecms.content.*;
import com.atex.onecms.content.aspects.Aspect;
import com.atex.onecms.content.aspects.annotations.AspectDefinition;
import com.atex.onecms.content.mapping.ContentComposer;
import com.atex.onecms.content.mapping.Context;
import com.atex.onecms.content.mapping.Request;
import com.atex.plugins.structured.text.Note;
import com.atex.plugins.structured.text.StructuredText;
import com.polopoly.cm.ContentIdFactory;
import com.polopoly.model.Model;
import com.polopoly.model.ModelDomain;
import com.polopoly.model.ModelListBase;
import com.polopoly.model.PojoAsModel;

import example.hermes.mappings.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;


public class StandardArticleHermesComposer implements ContentComposer<Object, Object, Object>{

	private static Logger logger = Logger.getLogger(StandardArticleHermesComposer.class.getName());
	private static final Subject SYSTEM_SUBJECT = new Subject("98", null);
	private static final String hermesAspectName = HermesElementAspect.class.getAnnotation(AspectDefinition.class).value()[0];

	private static final String RESOURCE_TOP_IMAGE = "topImage";
	private static final String RESOURCE_IMAGES = "images";
	private static final String RESOURCE_MULTIMEDIA = "multimedia";

	private static final String CONTENT_TYPE_IMAGE = "atex.dam.standard.Image";
	private static final String CONTENT_TYPE_PROD_IMAGE = "com.atex.nosql.image.ImageContentDataBean";

	private static HashMap<String, String> embedMap = new HashMap<String, String>();

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

					hermesElements.add(new HermesElement(ElementNameEnum.LEAD.getName(),
															ElementNameEnum.LEAD.getPrintName(),
															HermesTypesEnum.CAPTION.getValue(),
															HermesConstants.HERMES_LEVEL_TEXTS,
															hermesDataType));

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

				// Convert embed tags - need to be before addResources to feed embedMap and then setuptypes
				Aspect mainAspect = content.getContentAspect();
				ArticleBean data = (ArticleBean)mainAspect.getData();
				data.setBody(replaceEmbedTags(data.getBody()));
				content.getContentAspect().setData(data);


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


	private StructuredText replaceEmbedTags (StructuredText sourceBody){

		StructuredText parsedBody = sourceBody;

		/*  SMART EMBED SOURCE
		<a class="p-smartembed" data-attr-f="3x2" data-attr-q="0.3" data-attr-w="400" data-onecms-id="onecms:ecfbd6b4-3a7e-4273-a4ea-67a127356d09" data-onecms-type="image" href="javascript:window.parent.actionEventData({$contentId:'onecms:ecfbd6b4-3a7e-4273-a4ea-67a127356d09', $action:'view', $target:'work'})" polopoly:contentid="onecms:ecfbd6b4-3a7e-4273-a4ea-67a127356d09">
			<img src="/image/onecms:ecfbd6b4-3a7e-4273-a4ea-67a127356d09/909e0966-885c-4f4c-a8c9-e21cafc2f390?f=3x2&amp;w=400&amp;q=0.3" />
		</a>
		 */


		if(sourceBody.getText().contains("p-smartembed")){

			String sourceText =  sourceBody.getText();

			org.jsoup.nodes.Document doc = Jsoup.parse(sourceText);
			Elements smartEmbedElements = doc.getElementsByAttributeValue("class", "p-smartembed");
			int i = 1;
			for (org.jsoup.nodes.Element embedElement:smartEmbedElements) {

				String dataonecmsid = embedElement.attr("data-onecms-id");
				embedMap.put(dataonecmsid, String.valueOf(i));
				formatEmbedNode(embedElement, i);
				embedElement.remove();

				if(parsedBody.getNotes() == null)
					parsedBody.setNotes( new HashMap<String, Note>());

				Note note = new Note();
				note.setText("Embedded Content ");
				note.setCreated("");
				note.setModified("");
				note.setUser("");

				int noteCounter = i-1;
				parsedBody.getNotes().put("note-"+noteCounter, note);


				i++;
			}

			parsedBody.setText(doc.outerHtml().replaceAll("\n",""));
			return parsedBody;

		}else
			return sourceBody;

	}


	private org.jsoup.nodes.Node formatEmbedNode(org.jsoup.nodes.Node currentElement, int i){

		/* NOTES RESULT

                <span data-atex-uat=\"{ }\" data-atex-id=\"cmd\" data-atex-name=\"ET\">&thinsp;</span>
                <span data-atex-af=\"$ID/PoynterOSTextThreeL Roman\" data-atex-fs=\"Normal\" data-atex-cfill=\"Color/Black\">
                               <span class=\"x-atex-note\" x-atex-user=\"\" x-atex-created=\"\" x-atex-modified=\"\">Embedded content </span>
                </span>
                <span data-atex-cstyle=\"EMBED2\" data-atex-af=\"$ID/TitlingGothicFB Normal Black\" data-atex-fs=\"Black\" data-atex-cfill=\"Color/Black\">
                </span>
                <span data-atex-uat=\"{ }\" data-atex-id=\"cmd\" data-atex-name=\"EI\" data-atex-v1=\"2\" data-atex-cstyle=\"EMBED2\">&nbsp;</span>
                <span data-atex-cstyle=\"EMBED2\" data-atex-af=\"$ID/TitlingGothicFB Normal Black\" data-atex-fs=\"Black\" data-atex-cfill=\"Color/Black\">
                </span>

		 */

		String index = String.valueOf(i);

		org.jsoup.nodes.Node span1Node = new Element(Tag.valueOf("span"),"");
		span1Node.attr("data-atex-uat","{ }");
		span1Node.attr("data-atex-id","cmd");
		span1Node.attr("data-atex-name","ET");
		((Element) span1Node).text(Entities.getCharacterByName("thinsp").toString());


		org.jsoup.nodes.Node span2Node = new Element(Tag.valueOf("span"),"");
		span2Node.attr("data-atex-af","$ID/PoynterOSTextThreeL Roman");
		span2Node.attr("data-atex-fs", "Normal");
		span2Node.attr("data-atex-cfill", "Color/Black");

		org.jsoup.nodes.Node nestedSpan1Node = new Element(Tag.valueOf("span"),"");
		nestedSpan1Node.attr("class", "x-atex-note");
		nestedSpan1Node.attr("x-atex-user", "");
		nestedSpan1Node.attr("x-atex-created", "");
		nestedSpan1Node.attr("x-atex-modified", "");
		((Element) nestedSpan1Node).text("Embedded content ");

		((Element) span2Node).appendChild(nestedSpan1Node);

		org.jsoup.nodes.Node span3Node = new Element(Tag.valueOf("span"),"");
		span3Node.attr("data-atex-cstyle", "EMBED"+index);
		span3Node.attr("data-atex-af", "$ID/TitlingGothicFB Normal Black");
		span3Node.attr("data-atex-fs", "Black");
		span3Node.attr("data-atex-cfill", "Color/Black");


		org.jsoup.nodes.Node span4Node = new Element(Tag.valueOf("span"),"");
		span4Node.attr("data-atex-uat","{ }");
		span4Node.attr("data-atex-id","cmd");
		span4Node.attr("data-atex-name","EI");
		span4Node.attr("data-atex-v1",index);
		span4Node.attr("data-atex-cstyle","EMBED"+index);
		((Element) span4Node).text(Entities.getCharacterByName("nbsp").toString());

		org.jsoup.nodes.Node span5Node = new Element(Tag.valueOf("span"),"");
		span5Node.attr("data-atex-cstyle","EMBED"+index);
		span5Node.attr("data-atex-af","$ID/TitlingGothicFB Normal Black");
		span5Node.attr("data-atex-fs", "Black");
		span5Node.attr("data-atex-cfill", "Color/Black");

		currentElement.after(span5Node);
		currentElement.after(span4Node);
		currentElement.after(span3Node);
		currentElement.after(span2Node);
		currentElement.after(span1Node);
		return currentElement;
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

				String contentIdStr = contentId.getDelegationId()+":"+contentId.getKey();
				if(embedMap.containsKey(contentIdStr)){
					resourceElement.getMetadata().put("WEB.SUBTYPE", "EMBED"+embedMap.get(contentIdStr));
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


