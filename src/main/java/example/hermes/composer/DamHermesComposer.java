package example.hermes.composer;

import com.atex.onecms.content.ContentResult;
import com.atex.onecms.content.mapping.ContentComposer;
import com.atex.onecms.content.mapping.Context;
import com.atex.onecms.content.mapping.Request;

import example.dam.IDamArchiveBean;
import example.dam.article.DamArticleBean;
import example.dam.composer.CustomDamComposer;
import example.dam.image.DamImageBean;
import example.hermes.mappings.HermesObjectBean;

public class DamHermesComposer extends CustomDamComposer implements ContentComposer<IDamArchiveBean, HermesObjectBean, Object>{

	@Override
	public ContentResult<HermesObjectBean> compose(ContentResult<IDamArchiveBean> damObjectBeanDataResult,
			String s, Request request, Context<Object> context) {

		IDamArchiveBean original = damObjectBeanDataResult.getContent().getContentData();
		HermesObjectBean hermesObjectBean = null;

		try{

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
			}

		}catch(Exception e){
			e.printStackTrace();
		}

		return new ContentResult<HermesObjectBean>(damObjectBeanDataResult, hermesObjectBean);

	}

}
