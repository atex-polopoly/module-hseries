package example.hermes.composer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.atex.onecms.content.Content;
import com.atex.onecms.content.ContentResult;
import com.atex.onecms.content.ContentResultBuilder;
import com.atex.onecms.content.ContentVersionId;
import com.atex.onecms.content.mapping.ContentComposer;
import com.atex.onecms.content.mapping.Context;
import com.atex.onecms.content.mapping.Request;
import com.polopoly.model.Model;
import com.polopoly.model.ModelDomain;
import com.polopoly.model.PojoAsModel;
import com.polopoly.util.StringUtil;

import example.hermes.mappings.HermesBean;

public class DamContentHermesComposer implements ContentComposer<Object, Object, Object> {

    private static final Logger LOGGER = Logger.getLogger(DamContentHermesComposer.class.getName());

    @Override
    public ContentResult<Object> compose(ContentResult<Object> result,
                                         String s, Request request, Context<Object> context) {
        ModelDomain modelDomain = context.getModelDomain();
        Content<Object> content = result.getContent();
        Model contentBean = new PojoAsModel(modelDomain, content.getContentData());

        String objectType = ((String) contentBean.getChild("objectType"));
        if ((!StringUtil.isEmpty(objectType)) && (objectType.equalsIgnoreCase("collection"))) {
            HermesBean bean = new HermesBean();

            String host = getHost(request);
            LOGGER.log(Level.FINE, "POLOPOLY HOST", host);

            bean.add(HermesBean.HE_WEB_METADATA_EXTERNAL_LINK, ((String) contentBean.getChild("preview")));
            bean.add(HermesBean.HE_WEB_METADATA_INTERNAL_LINK, getDeskPreviewUrl("collection", getContentId(result), host));

            return createContentResult(result, bean);
        } else if ((!StringUtil.isEmpty(objectType)) && (objectType.equalsIgnoreCase("video"))) {
            HermesBean bean = new HermesBean();

            String host = getHost(request);
            LOGGER.log(Level.FINE, "POLOPOLY HOST", host);

            bean.add(HermesBean.HE_WEB_METADATA_VIDEO_BRIGHTCOVE_ID, ((String) contentBean.getChild("brightcoveId")));
            bean.add(HermesBean.HE_WEB_METADATA_INTERNAL_LINK, getDeskPreviewUrl("video", getContentId(result), host));

            return createContentResult(result, bean);
        } else {
            String host = getHost(request);
            LOGGER.log(Level.FINE, "POLOPOLY HOST", host);

            String url = host + "/onecms/dam/content/galleryurl?contentId=" + result.getContentId().getKey();
            LOGGER.log(Level.FINE, "PREVIEW URL", url);

            String preview = "";
            try {
                preview = getPreviewURL(url, getToken(request));
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "ERROR IN PREVIEW LOOKUP", e);
            }

            HermesBean bean = new HermesBean();
            bean.add(HermesBean.HE_WEB_METADATA_EXTERNAL_LINK, preview);

            return createContentResult(result, bean);
        }
    }

    private ContentResult<Object> createContentResult(final ContentResult<Object> result,
                                                      final HermesBean bean) {
        return ContentResultBuilder.copy(result)
                                   .mainAspectData(bean)
                                   .clearAspects()
                                   .build();
    }

    private String getToken(Request request) {
        String token = null;
        if ((request != null) && (request.getSubject() != null)) {
            String principal = request.getSubject().getPrincipalId();
            String code = request.getSubject().getPublicCredentials();
            token = principal + "::" + code;
        }
        return token;
    }

    private String getPreviewURL(String url, String token) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestProperty("X-Auth-Token", token);
        con.setRequestMethod("GET");
        con.setDoOutput(true);

        int responseCode = con.getResponseCode();
        LOGGER.log(Level.FINE, "RESPONSE CODE [ " + responseCode + "]");

        String preview = null;
        if (responseCode == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            preview = response.toString();
        }

        return preview;
    }

    @SuppressWarnings("unchecked")
    private String getHost(Request request) {
        String host = null;
        if ((request.getRequestParameters() != null) && (request.getRequestParameters().containsKey("host"))) {
            LinkedList<String> list = (LinkedList<String>) request.getRequestParameters().get("host");
            host = list.getFirst();
        }

        return host;
    }

    private String getDeskPreviewUrl(String type, String contentId, String polopoly) {
        String preview = (polopoly != null) ? polopoly : "";
        if (type.equalsIgnoreCase("collection")) {
            preview += "/desk/#/collection/policy:" + contentId;
        } else {
            preview += "/desk/?previewid=" + contentId;
        }

        return preview;
    }

    private String getContentId(ContentResult<Object> result) {
        String key = null;

        if (result != null) {
            ContentVersionId contentId = result.getContentId();
            if (contentId != null) {
                key = contentId.getKey();
            }
        }
        return key;
    }

}