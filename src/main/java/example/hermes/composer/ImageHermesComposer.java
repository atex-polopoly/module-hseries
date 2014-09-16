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
import com.atex.standard.image.ImageContentDataBean;

import example.hermes.mappings.HermesConstants;
import example.hermes.mappings.HermesElement;
import example.hermes.mappings.HermesElementAspect;
import example.hermes.mappings.HermesTypesEnum;

public class ImageHermesComposer implements ContentComposer<ImageContentDataBean, ImageContentDataBean, Object>{

	private static final Subject SYSTEM_SUBJECT = new Subject("98", null);
	private static final String hermesAspectName = HermesElementAspect.class.getAnnotation(AspectDefinition.class).value()[0];

	@Override
	public ContentResult<ImageContentDataBean> compose(ContentResult<ImageContentDataBean> imageBeanDataResult,
			String s, Request request, Context<Object> context) {

		ImageContentDataBean original = imageBeanDataResult.getContent().getContentData();


		ContentResult<ImageContentDataBean> res = new ContentResult<ImageContentDataBean>(imageBeanDataResult, original);

		try{
			ContentManager cm = context.getContentManager();

			HermesElementAspect hermesElementAspect = null;
			ArrayList<HermesElement> hermesElements = null;
			
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
				if(imageBeanDataResult.getContent().getAspect(hermesAspectName) == null){
					hermesElementAspect = new HermesElementAspect();
					hermesElementAspect.setHermesContentType(HermesTypesEnum.IMAGE.getValue());

					hermesElements = hermesElementAspect.getElements();

					
					HermesElement imageElement = new HermesElement("image", HermesTypesEnum.IMAGE.getValue(), HermesConstants.HERMES_LEVEL_IMAGES, hermesDataType);
					imageElement.getMetadata().put("WEB/AUTHOR", original.getByline());
					hermesElements.add(imageElement);
					
					
				}else{
					// in case the aspect exists preserve current aspect data as hermesPk
					hermesElementAspect = (HermesElementAspect)imageBeanDataResult.getContent().getAspect(hermesAspectName).getData();
					hermesElements = hermesElementAspect.getElements();
				}



				if(original.getDescription()!= null && original.getDescription().trim().length() > 0)
					hermesElements.add(new HermesElement("description", HermesTypesEnum.CAPTION.getValue(), HermesConstants.HERMES_LEVEL_TEXTS, hermesDataType));




				/*
				 * End Polopoly To Hermes Mapping
				 */				



				/*
				 * Create aspects for hermes mapping
				 */
				ContentWrite<ImageContentDataBean> cw = new ContentWrite<>(imageBeanDataResult.getContent());
				cw.setAspect(hermesAspectName, hermesElementAspect);

				// update/create only the hermesElement aspects in couchbase
				ContentResult<ImageContentDataBean> updatedAspects = cm.update(imageBeanDataResult.getContent().getId().getContentId(), cw, SYSTEM_SUBJECT);

				Collection<Aspect> aspects = new ArrayList<Aspect>();	// create an empty aspects collection because the actual aspects collection is unmodifiable 
				aspects.addAll(imageBeanDataResult.getContent().getAspects());	// add all aspects, plus hermesElements just updated

				if(imageBeanDataResult.getContent().getAspect(hermesAspectName) != null)	// in case of existing hermesElements aspects clean existing
					aspects.remove(imageBeanDataResult.getContent().getAspect(hermesAspectName));

				aspects.addAll(updatedAspects.getContent().getAspects());
				res = new ContentResult<ImageContentDataBean>(res, original, aspects);
			}





		}catch(Exception e){
			e.printStackTrace();
		}

		return res;

	}

}
