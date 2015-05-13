package example.hermes.composer;

import java.util.List;

import com.atex.onecms.content.Content;
import com.atex.onecms.content.ContentManager;
import com.atex.onecms.content.ContentResult;
import com.atex.onecms.content.ContentWrite;
import com.atex.onecms.content.ContentWriteBuilder;
import com.atex.onecms.content.FilesAspectBean;
import com.atex.onecms.content.aspects.annotations.AspectDefinition;
import com.atex.onecms.content.mapping.ContentComposer;
import com.atex.onecms.content.mapping.Context;
import com.atex.onecms.content.mapping.Request;
import com.atex.onecms.image.ImageInfoAspectBean;
import com.polopoly.model.Model;
import com.polopoly.model.ModelDomain;
import com.polopoly.model.PojoAsModel;

import example.hermes.mappings.HermesConstants;
import example.hermes.mappings.HermesElement;
import example.hermes.mappings.HermesElementAspect;
import example.hermes.mappings.HermesObjectBean;
import example.hermes.mappings.HermesTypesEnum;

public class DamHermesComposer implements ContentComposer<Object, Object, Object>{

	// private static final Subject SYSTEM_SUBJECT = new Subject("98", null);
	private static final String hermesAspectName = HermesElementAspect.class.getAnnotation(AspectDefinition.class).value()[0];
	
	@Override
	public ContentResult<Object> compose(ContentResult<Object> damObjectBeanDataResult,
			String s, Request request, Context<Object> context) {

		ModelDomain modelDomain = context.getModelDomain();
		Content<Object>  content = damObjectBeanDataResult.getContent();
		Model contentBean = new PojoAsModel(modelDomain, content.getContentData());
		
		
		// IDamArchiveBean original = damObjectBeanDataResult.getContent().getContentData();
		HermesObjectBean hermesObjectBean = null;

		ContentResult<Object> res = null; // new ContentResult<Object>(damObjectBeanDataResult, hermesObjectBean);
		

		try{
			ContentManager cm = context.getContentManager();


			/*
			 *  variant: hermes
			 */
			if(s.equals("hermes")){
				hermesObjectBean = new HermesObjectBean();
				
	            hermesObjectBean.setContentId(damObjectBeanDataResult.getContent().getId().getKey());
	            if(contentBean.getChild("name") != null)
	            	hermesObjectBean.setName(contentBean.getChild("name").toString());
	            
	            if(contentBean.getChild("body") != null)
	            	hermesObjectBean.setText(contentBean.getChild("body").toString());
	            if(contentBean.getChild("author") != null)
	            	hermesObjectBean.setAuthor(contentBean.getChild("author").toString());
	            if(contentBean.getChild("description") != null)
	            	hermesObjectBean.setCaption(contentBean.getChild("description").toString());

	            	
	            	/*
	            	 * Temporary patch for file service storage
	            	 */
//	            	if (imageBean.getUrl()!= null && !imageBean.getUrl().contains("polopoly_fs")){						
//						imageBean.setUrl("/dam/content/file?file=" + imageBean.getUrl());
//						imageBean.setThumbUrl("/dam/content/file?file=" + imageBean.getThumbUrl());
//					}
//	            	hermesObjectBean.setFileurl(imageBean.getUrl());
//	            }
	            
	            /*
	             * Add hermes element aspects to allow copy/paste to ACT and then import into Hermes Production 
	             */
	            
	            
				/*
				 * create aspects for hermes
				 * 
				 * 
				 */

	            String hermesDataType = "NewsRoom";
				
				HermesElementAspect hermesElementAspect = null;
				
				/*
				 * Start Polopoly To Hermes Mapping
				 */
				if(content.getAspect(hermesAspectName) == null){
					hermesElementAspect = new HermesElementAspect();
					
					List<HermesElement> hermesElements = hermesElementAspect.getElements();

					if(contentBean.getChild("objectType").toString().equals("article")){
						hermesElementAspect.setHermesContentType(HermesTypesEnum.TEXT.getValue());
						hermesElements.add(new HermesElement("body", HermesTypesEnum.TEXT.getValue(), HermesConstants.HERMES_LEVEL_TEXTS, hermesDataType));
					}

					if(contentBean.getChild("objectType").toString().equals("image")){
						hermesElementAspect.setHermesContentType(HermesTypesEnum.IMAGE.getValue());
						HermesElement imageElement = new HermesElement("image", HermesTypesEnum.IMAGE.getValue(), HermesConstants.HERMES_LEVEL_IMAGES, hermesDataType);
						imageElement.setResourceContentId(hermesObjectBean.getContentId());
						hermesElements.add(imageElement);

						if(content.getAspectNames().contains(ImageInfoAspectBean.ASPECT_NAME)){
							ImageInfoAspectBean imageInfoAspectBean = (ImageInfoAspectBean)content.getAspectData(ImageInfoAspectBean.ASPECT_NAME);
							String filePath = imageInfoAspectBean.getFilePath();
							if(content.getAspectNames().contains(FilesAspectBean.ASPECT_NAME)){
								FilesAspectBean filesAspectBean = (FilesAspectBean)content.getAspectData(FilesAspectBean.ASPECT_NAME);
								String fileUri = filesAspectBean.getFiles().get(filePath).getFileUri();
								fileUri = fileUri.replaceFirst("://", "/");
								hermesObjectBean.setFileurl(fileUri);
							}
						}

					}
					
									

				}else{
					hermesElementAspect = (HermesElementAspect)content.getAspect(hermesAspectName).getData();	
				}
					
					
				/*
				 * End Polopoly To Hermes Mapping
				 */				
				
				/*
				 * Create aspects for hermes mapping
				 */
				ContentWriteBuilder<Object> cwb = new ContentWriteBuilder<Object>();

				cwb.mainAspectData(hermesObjectBean);
				cwb.aspects(content.getAspects());
				cwb.aspect(hermesAspectName, hermesElementAspect);
				cwb.origin(content);
				
				ContentWrite<Object> cw = cwb.buildUpdate();
				res =  new ContentResult<Object>(damObjectBeanDataResult, cw.getContentData(), cw.getAspects()); 
				
				// there is no need to really create the hermes element aspect
				// res = cm.update(content.getId().getContentId(), cw, SYSTEM_SUBJECT);
			

	            
			}

		}catch(Exception e){
			e.printStackTrace();
		}

		return res;

	}

}

