package example.hermes.composer;

import java.util.List;

import com.atex.onecms.content.Content;
import com.atex.onecms.content.ContentManager;
import com.atex.onecms.content.ContentResult;
import com.atex.onecms.content.ContentResultBuilder;
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

import example.hermes.mappings.*;

public class DamHermesComposer implements ContentComposer<Object, Object, Object> {

    private static final String MIMETYPE_VIDEO = "application/desk-video";
    private static final String MIMETYPE_TWEET = "application/desk-tweet";
    private static final String MIMETYPE_COLLECTION = "application/desk-collection";

    private static final String hermesAspectName = HermesElementAspect.class.getAnnotation(AspectDefinition.class)
                                                                            .value()[0];

    @Override
    public ContentResult<Object> compose(ContentResult<Object> damObjectBeanDataResult,
                                         String s, Request request, Context<Object> context) {

        ModelDomain modelDomain = context.getModelDomain();
        Content<Object> content = damObjectBeanDataResult.getContent();
        Model contentBean = new PojoAsModel(modelDomain, content.getContentData());


        // IDamArchiveBean original = damObjectBeanDataResult.getContent().getContentData();
        HermesObjectBean hermesObjectBean = null;

        ContentResult<Object> res = null; // new ContentResult<Object>(damObjectBeanDataResult, hermesObjectBean);


        try {
            ContentManager cm = context.getContentManager();


            /*
             *  variant: hermes
             */
            if (s.equals("hermes")) {
                String objectType = contentBean.getChild("objectType").toString();

                hermesObjectBean = new HermesObjectBean();

                hermesObjectBean.setContentId(damObjectBeanDataResult.getContent().getId().getKey());
                if (contentBean.getChild("name") != null) {
                    hermesObjectBean.setName(contentBean.getChild("name").toString());
                }

                if (contentBean.getChild(ElementNameEnum.TEXT.getName()) != null) {
                    hermesObjectBean.setText(contentBean.getChild(ElementNameEnum.TEXT.getName()).toString());
                }
                if (contentBean.getChild("author") != null) {
                    hermesObjectBean.setAuthor(contentBean.getChild("author").toString());
                }
                if (contentBean.getChild(ElementNameEnum.DESCRIPTION.getName()) != null) {
                    hermesObjectBean.setCaption(contentBean.getChild(ElementNameEnum.DESCRIPTION.getName()).toString());
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
                if (content.getAspect(hermesAspectName) == null) {
                    hermesElementAspect = new HermesElementAspect();

                    List<HermesElement> hermesElements = hermesElementAspect.getElements();

                    if (objectType.equals(ElementNameEnum.ARTICLE.getName())) {
                        hermesElementAspect.setHermesContentType(HermesTypesEnum.TEXT.getValue());
                        hermesElements.add(new HermesElement(ElementNameEnum.TEXT.getName(), ElementNameEnum.TEXT.getPrintName(), HermesTypesEnum.TEXT
                                .getValue(), HermesConstants.HERMES_LEVEL_TEXTS, hermesDataType));
                    }

                    if (objectType.equals(ElementNameEnum.IMAGE.getName())) {
                        hermesElementAspect.setHermesContentType(HermesTypesEnum.IMAGE.getValue());
                        HermesElement imageElement = new HermesElement(ElementNameEnum.IMAGE.getName(), ElementNameEnum.IMAGE
                                .getPrintName(), HermesTypesEnum.IMAGE.getValue(), HermesConstants.HERMES_LEVEL_IMAGES, hermesDataType);
                        imageElement.setResourceContentId(hermesObjectBean.getContentId());
                        hermesElements.add(imageElement);

                        if (content.getAspectNames().contains(ImageInfoAspectBean.ASPECT_NAME)) {
                            ImageInfoAspectBean imageInfoAspectBean = (ImageInfoAspectBean) content.getAspectData(ImageInfoAspectBean.ASPECT_NAME);
                            String filePath = imageInfoAspectBean.getFilePath();
                            if (content.getAspectNames().contains(FilesAspectBean.ASPECT_NAME)) {
                                FilesAspectBean filesAspectBean = (FilesAspectBean) content.getAspectData(FilesAspectBean.ASPECT_NAME);
                                String fileUri = filesAspectBean.getFiles().get(filePath).getFileUri();
                                fileUri = fileUri.replaceFirst("://", "/");
                                hermesObjectBean.setFileurl(fileUri);
                            }
                        }
                    }

                    // http links
                    else if (objectType.equals("video")) {
                        HermesElement httpLinkElement = new HermesElement(ElementNameEnum.HTTP_LINK.getName(), ElementNameEnum.HTTP_LINK
                                .getPrintName(), HermesTypesEnum.HTTP_LINK.getValue(), HermesConstants.HERMES_LEVEL_HTTP_LINK, MIMETYPE_VIDEO);
                        httpLinkElement.setResourceContentId(hermesObjectBean.getContentId());
                        hermesElements.add(httpLinkElement);
                    } else if (objectType.equals("tweet")) {
                        HermesElement httpLinkElement = new HermesElement(ElementNameEnum.HTTP_LINK.getName(), ElementNameEnum.HTTP_LINK
                                .getPrintName(), HermesTypesEnum.HTTP_LINK.getValue(), HermesConstants.HERMES_LEVEL_HTTP_LINK, MIMETYPE_TWEET);
                        httpLinkElement.setResourceContentId(hermesObjectBean.getContentId());
                        hermesElements.add(httpLinkElement);
                    } else if (objectType.equals("collection")) {
                        HermesElement httpLinkElement = new HermesElement(ElementNameEnum.HTTP_LINK.getName(), ElementNameEnum.HTTP_LINK
                                .getPrintName(), HermesTypesEnum.HTTP_LINK.getValue(), HermesConstants.HERMES_LEVEL_HTTP_LINK, MIMETYPE_COLLECTION);
                        httpLinkElement.setResourceContentId(hermesObjectBean.getContentId());
                        hermesElements.add(httpLinkElement);
                    }

                } else {
                    hermesElementAspect = (HermesElementAspect) content.getAspect(hermesAspectName).getData();
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

                // I don't know why we create a content result out of a contentWrite we just built.
                res = ContentResultBuilder.copy(damObjectBeanDataResult)
                                          .mainAspectData(cw.getContentData())
                                          .aspects(cw.getAspects())
                                          .build();

                // there is no need to really create the hermes element aspect
                // res = cm.update(content.getId().getContentId(), cw, SYSTEM_SUBJECT);


            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;

    }

}

