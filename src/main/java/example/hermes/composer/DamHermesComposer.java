package example.hermes.composer;

import java.util.ArrayList;
import java.util.Collection;

import com.atex.onecms.app.dam.IDamArchiveBean;
import com.atex.onecms.app.dam.article.DamArticleBean;
import com.atex.onecms.app.dam.composer.CustomDamComposer;
import com.atex.onecms.app.dam.image.DamImageBean;
import com.atex.onecms.content.ContentManager;
import com.atex.onecms.content.ContentResult;
import com.atex.onecms.content.ContentWrite;
import com.atex.onecms.content.Subject;
import com.atex.onecms.content.aspects.Aspect;
import com.atex.onecms.content.aspects.annotations.AspectDefinition;
import com.atex.onecms.content.mapping.ContentComposer;
import com.atex.onecms.content.mapping.Context;
import com.atex.onecms.content.mapping.Request;

import example.hermes.mappings.HermesConstants;
import example.hermes.mappings.HermesElement;
import example.hermes.mappings.HermesElementAspect;
import example.hermes.mappings.HermesObjectBean;
import example.hermes.mappings.HermesTypesEnum;

public class DamHermesComposer extends CustomDamComposer implements ContentComposer<IDamArchiveBean, HermesObjectBean, Object>{

	private static final Subject SYSTEM_SUBJECT = new Subject("98", null);
	private static final String hermesAspectName = HermesElementAspect.class.getAnnotation(AspectDefinition.class).value()[0];
	
	@Override
	public ContentResult<HermesObjectBean> compose(ContentResult<IDamArchiveBean> damObjectBeanDataResult,
			String s, Request request, Context<Object> context) {

		IDamArchiveBean original = damObjectBeanDataResult.getContent().getContentData();
		HermesObjectBean hermesObjectBean = null;

		ContentResult<HermesObjectBean> res = new ContentResult<HermesObjectBean>(damObjectBeanDataResult, hermesObjectBean);
		

		try{
			ContentManager cm = context.getContentManager();


			/*
			 *  variant: hermes
			 */
			if(s.equals("hermes")){
				hermesObjectBean = new HermesObjectBean();
				
	            hermesObjectBean.setContentId(damObjectBeanDataResult.getContent().getId().getKey());
	            hermesObjectBean.setName(original.getName());
	            
	            if(original instanceof DamArticleBean){
	            	DamArticleBean articleBean = (DamArticleBean)original;
	            	hermesObjectBean.setText(articleBean.getBody());
	            	hermesObjectBean.setAuthor(articleBean.getAuthor());
	            }
	            if(original instanceof DamImageBean){
	            	DamImageBean imageBean = (DamImageBean)original;
	            	hermesObjectBean.setCaption(imageBean.getDescription());
	            	hermesObjectBean.setAuthor(imageBean.getAuthor());
	            	
	            	/*
	            	 * Temporary patch for file service storage
	            	 */
	            	if (imageBean.getUrl()!= null && !imageBean.getUrl().contains("polopoly_fs")){						
						imageBean.setUrl("/dam/content/file?file=" + imageBean.getUrl());
						imageBean.setThumbUrl("/dam/content/file?file=" + imageBean.getThumbUrl());
					}
	            	hermesObjectBean.setFileurl(imageBean.getUrl());
	            }
	            
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
				if(damObjectBeanDataResult.getContent().getAspect(hermesAspectName) == null){
					hermesElementAspect = new HermesElementAspect();
					
					ArrayList<HermesElement> hermesElements = hermesElementAspect.getElements();

					if(original instanceof DamArticleBean){
						hermesElementAspect.setHermesContentType(HermesTypesEnum.TEXT.getValue());
						hermesElements.add(new HermesElement("body", HermesTypesEnum.TEXT.getValue(), HermesConstants.HERMES_LEVEL_TEXTS, hermesDataType));
					}

					if(original instanceof DamImageBean){
						hermesElementAspect.setHermesContentType(HermesTypesEnum.IMAGE.getValue());
						HermesElement imageElement = new HermesElement("image", HermesTypesEnum.IMAGE.getValue(), HermesConstants.HERMES_LEVEL_IMAGES, hermesDataType);
						imageElement.setResourceContentId(hermesObjectBean.getContentId());
						hermesElements.add(imageElement);
					}
					
									

				}else{
					hermesElementAspect = (HermesElementAspect)damObjectBeanDataResult.getContent().getAspect(hermesAspectName).getData();	
				}
					
					
				/*
				 * End Polopoly To Hermes Mapping
				 */				
				
				
				/*
				 * Create aspects for hermes mapping
				 */
				ContentWrite<IDamArchiveBean> cw = new ContentWrite<>(damObjectBeanDataResult.getContent());
				cw.setAspect(hermesAspectName, hermesElementAspect);
				
				// update/create only the hermesElement aspects in couchbase
				ContentResult<IDamArchiveBean> updatedAspects = cm.update(damObjectBeanDataResult.getContent().getId().getContentId(), cw, SYSTEM_SUBJECT);
				
				Collection<Aspect> aspects = new ArrayList<Aspect>();	// create an empty aspects collection because the actual aspects collection is unmodifiable 
				aspects.addAll(damObjectBeanDataResult.getContent().getAspects());	// add all aspects, plus hermesElements just updated
				
				if(damObjectBeanDataResult.getContent().getAspect(hermesAspectName) != null)	// in case of existing hermesElements aspects clean existing
					aspects.remove(damObjectBeanDataResult.getContent().getAspect(hermesAspectName));
				
				aspects.addAll(updatedAspects.getContent().getAspects());
				res = new ContentResult<HermesObjectBean>(damObjectBeanDataResult, hermesObjectBean, aspects);	            
	            
	            
			}

		}catch(Exception e){
			e.printStackTrace();
		}

		return res;

	}

}

