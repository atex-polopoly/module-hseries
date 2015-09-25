package example.hermes.composer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.atex.onecms.content.ContentResult;
import com.atex.onecms.content.mapping.ContentComposer;
import com.atex.onecms.content.mapping.Context;
import com.atex.onecms.content.mapping.Request;

import example.hermes.mappings.HermesBean;

public class DamContentPolopolyPreviewComposer implements ContentComposer<Object, Object, Object>{
	@Override
	public ContentResult<Object> compose(ContentResult<Object> result,
			String s, Request request, Context<Object> context) {
		String host = getHost(request);
		LOGGER.log(Level.FINE, "POLOPOLY HOST", host);
		
		String url = host + "/dam/content/galleryurl?contentId=" + result.getContentId().getKey();
		LOGGER.log(Level.FINE, "PREVIEW URL", url);
		
		String preview = "";
		try {
			preview = getPreviewURL(url, getToken(request));
		} catch(Exception e) {
			LOGGER.log(Level.SEVERE, "ERROR IN PREWIEW LOOKUP", e);
		}
		
		HermesBean bean = new HermesBean();
		bean.add(HermesBean.HE_WEB_METADATA_HTTP_LINK_URL, preview);
	
		return new ContentResult<Object>(result,bean,null);
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
	
	private String getToken(Request request) {
		String token = null;
		if ( ( request != null ) && ( request.getSubject() !=null ) ){
			String principal = request.getSubject().getPrincipalId();
			String code = request.getSubject().getPublicCredentials();
			token = principal + "::" + code; 
		}
		return token;
	}
	
	private String getPreviewURL(String url,String token) throws Exception {
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestProperty("X-Auth-Token", token);
		con.setRequestMethod("GET");
		con.setDoOutput(true);
		
		int responseCode = con.getResponseCode();
		LOGGER.log( Level.FINE,"RESPONDE CODE [ " + responseCode + "]" );
		
		String preview = null;
		if( responseCode == 200 ) {
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
	
	private static final Logger LOGGER = Logger.getLogger(DamContentPolopolyPreviewComposer.class.getName());
}