package example.hermes.composer;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.atex.onecms.content.Content;
import com.atex.onecms.content.ContentResult;
import com.atex.onecms.content.ContentVersionId;
import com.atex.onecms.content.mapping.ContentComposer;
import com.atex.onecms.content.mapping.Context;
import com.atex.onecms.content.mapping.Request;
import com.polopoly.model.Model;
import com.polopoly.model.ModelDomain;
import com.polopoly.model.PojoAsModel;

import example.hermes.mappings.HermesBean;

public class DamContentHermesComposer  implements ContentComposer<Object, Object, Object>{
	@Override
	public ContentResult<Object> compose(ContentResult<Object> result,
			String s, Request request, Context<Object> context) {
		ModelDomain modelDomain = context.getModelDomain();
		Content<Object>  content = result.getContent();
		Model contentBean = new PojoAsModel(modelDomain, content.getContentData());
		
		if( ((String)contentBean.getChild("objectType")).equalsIgnoreCase("collection")) {
			HermesBean bean = new HermesBean();
			
			String host = getHost(request);
			LOGGER.log(Level.FINE, "POLOPOLY HOST", host);
			
			bean.add(HermesBean.HE_WEB_METADATA_COLLECTION_GALLERY_LINK, ((String)contentBean.getChild("preview")));
			bean.add(HermesBean.HE_WEB_METADATA_DESK_PREVIEW_LINK, getDeskPreviewUrl("collection", getContentId(result), host));
			return new ContentResult<Object>(result,bean,null);
		} else if( ((String)contentBean.getChild("objectType")).equalsIgnoreCase("video")) {
			HermesBean bean = new HermesBean();
			
			String host = getHost(request);
			LOGGER.log(Level.FINE, "POLOPOLY HOST", host);
			
			bean.add(HermesBean.HE_WEB_METADATA_VIDEO_BRIGHTCOVE_ID, ((String)contentBean.getChild("brightcoveId")));
			bean.add(HermesBean.HE_WEB_METADATA_DESK_PREVIEW_LINK, getDeskPreviewUrl("video", getContentId(result), host));
			return new ContentResult<Object>(result,bean,null);
		} else {
			throw new RuntimeException();
		}
	}
	
	@SuppressWarnings("unchecked")
	private String getHost(Request request) {
		String host = null;
		if( ( request.getRequestParameters() != null ) && (request.getRequestParameters().containsKey("host")) ) {
			LinkedList<String> list =  (LinkedList<String>) request.getRequestParameters().get("host");
			host = list.getFirst();
		}
		
		return host;
	}

	private String getDeskPreviewUrl( String type, String contentId, String polopoly ) {
		String preview = (polopoly != null) ? polopoly : "";
		if(type.equalsIgnoreCase("collection")) {
			preview += "/desk/#/collection/policy:" + contentId;
		} else {
			preview += "/desk/?previewid=" + contentId;
		}

		return preview;
	}
	
	private String getContentId(ContentResult<Object> result) {
		String key = null;
		
		if( result != null ) {
			 ContentVersionId contentId = result.getContentId();
			 if(contentId != null) {
				 key = contentId.getKey();
			 }
		}
		return key;
	}
	
	private static final Logger LOGGER = Logger.getLogger(DamContentHermesComposer.class.getName());
}