package example.hermes.composer;

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
import com.polopoly.model.Model;
import com.polopoly.model.ModelDomain;
import com.polopoly.model.PojoAsModel;

import example.hermes.mappings.HermesConstants;
import example.hermes.mappings.HermesElement;
import example.hermes.mappings.HermesElementAspect;
import example.hermes.mappings.HermesTypesEnum;

public class ImageHermesComposer implements ContentComposer<Object, Object, Object>{

	private static final Subject SYSTEM_SUBJECT = new Subject("98", null);
	private static final String hermesAspectName = HermesElementAspect.class.getAnnotation(AspectDefinition.class).value()[0];

	@Override
	public ContentResult<Object> compose(ContentResult<Object> imageBeanDataResult,
			String s, Request request, Context<Object> context) {

		ModelDomain modelDomain = context.getModelDomain();
		Content<Object>  content = imageBeanDataResult.getContent();
		Model contentBean = new PojoAsModel(modelDomain, content.getContentData());


		ContentResult<Object> res = new ContentResult<Object>(imageBeanDataResult, contentBean);

		try{
			ContentManager cm = context.getContentManager();

			HermesElementAspect hermesElementAspect = null;
			List<HermesElement> hermesElements = null;
			
			String hermesDataType = "NewsRoom";
			
			/*
			 *  variant: hermesStory
			 */
			if(s.equals("hermes")){

				/*
				 * create aspects for hermes
				 * 
				 */

				/*
				 * Start Polopoly To Hermes Mapping
				 */
				if(content.getAspect(hermesAspectName) == null){
					hermesElementAspect = new HermesElementAspect();
					hermesElementAspect.setHermesContentType(HermesTypesEnum.IMAGE.getValue());

					hermesElements = hermesElementAspect.getElements();

					
					HermesElement imageElement = new HermesElement("image", "image", HermesTypesEnum.IMAGE.getValue(), HermesConstants.HERMES_LEVEL_IMAGES, hermesDataType);
					//imageElement.getMetadata().put("WEB/AUTHOR", original.getByline());
					hermesElements.add(imageElement);
					
					
				}else{
					// in case the aspect exists preserve current aspect data as hermesPk
					hermesElementAspect = (HermesElementAspect)content.getAspect(hermesAspectName).getData();
					hermesElements = hermesElementAspect.getElements();
				}

				if(contentBean.getChild("description")!= null && contentBean.getChild("description").toString().trim().length() > 0)
					hermesElements.add(new HermesElement("description", "description", HermesTypesEnum.CAPTION.getValue(), HermesConstants.HERMES_LEVEL_TEXTS, hermesDataType));

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
				res =  new ContentResult<Object>(imageBeanDataResult, cw.getContentData(), cw.getAspects()); 

			}





		}catch(Exception e){
			e.printStackTrace();
		}

		return res;

	}

}
