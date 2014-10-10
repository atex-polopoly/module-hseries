package example.hermes.composer;

import com.atex.onecms.content.ContentManager;
import com.atex.onecms.content.ContentResult;
import com.atex.onecms.content.ContentWrite;
import com.atex.onecms.content.Subject;
import com.atex.onecms.content.aspects.Aspect;
import com.atex.onecms.content.aspects.annotations.AspectDefinition;
import com.atex.onecms.content.mapping.ContentComposer;
import com.atex.onecms.content.mapping.Context;
import com.atex.onecms.content.mapping.Request;
import com.polopoly.cm.ContentId;
import com.polopoly.cm.ContentIdFactory;
import example.hermes.mappings.HermesConstants;
import example.hermes.mappings.HermesElement;
import example.hermes.mappings.HermesElementAspect;
import example.hermes.mappings.HermesTypesEnum;
import ie.irishtimes.content.article.EdgeCaseArticleBean;

import java.util.ArrayList;
import java.util.Collection;

public class StandardArticleHermesComposer implements ContentComposer<EdgeCaseArticleBean, EdgeCaseArticleBean, Object>{

	private static final Subject SYSTEM_SUBJECT = new Subject("98", null);
	private static final String hermesAspectName = HermesElementAspect.class.getAnnotation(AspectDefinition.class).value()[0];

    public static final String EDGECASE_GENRE_GENERAL = "general";
    public static final String EDGECASE_GENRE_ALBUMREVIEW = "AlbumReview";
    public static final String EDGECASE_GENRE_BOOKREVIEW = "BookReview";
    public static final String EDGECASE_GENRE_CARREVIEW = "CarReview";
    public static final String EDGECASE_GENRE_CULTURESTAGEREVIEW = "CultureStageReview";
    public static final String EDGECASE_GENRE_EVENTREVIEW = "EventReview";
    public static final String EDGECASE_GENRE_FILMREVIEW = "FilmReview";
    public static final String EDGECASE_GENRE_GAMEREVIEW = "GameReview";
    public static final String EDGECASE_GENRE_HOUSEREVIEW = "HouseReview";
    public static final String EDGECASE_GENRE_MATCHPREVIEW = "MatchPreview";
    public static final String EDGECASE_GENRE_MATCHREVIEW = "MatchReview";
    public static final String EDGECASE_GENRE_RECIPE = "Recipe";
    public static final String EDGECASE_GENRE_WINEREVIEW = "WineReview";
    public static final String EDGECASE_GENRE_RESTAURANTREVIEW = "RestaurantReview";


    @Override
	public ContentResult<EdgeCaseArticleBean> compose(ContentResult<EdgeCaseArticleBean> articleBeanDataResult,
			String s, Request request, Context<Object> context) {

		EdgeCaseArticleBean original = articleBeanDataResult.getContent().getContentData();
		

		ContentResult<EdgeCaseArticleBean> res = new ContentResult<EdgeCaseArticleBean>(articleBeanDataResult, original);

		try{
			ContentManager cm = context.getContentManager();
			
			String hermesDataType = "NewsRoom";
			
			HermesElementAspect hermesElementAspect = null;
			
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
				if(articleBeanDataResult.getContent().getAspect(hermesAspectName) == null){
					hermesElementAspect = new HermesElementAspect();
					hermesElementAspect.setHermesContentType(HermesTypesEnum.STORY_PACKAGE.getValue());
					
					ArrayList<HermesElement> hermesElements = hermesElementAspect.getElements();
					
					
					HermesElement spElement = new HermesElement("article", HermesTypesEnum.STORY_PACKAGE.getValue(), HermesConstants.HERMES_LEVEL_SP, hermesDataType);
					spElement.getMetadata().put("WEB/AUTHOR", original.getAuthor());
					hermesElements.add(spElement);
					
					HermesElement titleElement = new HermesElement("name", HermesTypesEnum.HEADER.getValue(), HermesConstants.HERMES_LEVEL_TEXTS, hermesDataType);
					titleElement.getMetadata().put("WEB/SUBTYPE", "main title");
					hermesElements.add(titleElement);
					
					hermesElements.add(new HermesElement("lead", HermesTypesEnum.HEADER.getValue(), HermesConstants.HERMES_LEVEL_TEXTS, hermesDataType));
					hermesElements.add(new HermesElement("body", HermesTypesEnum.TEXT.getValue(), HermesConstants.HERMES_LEVEL_TEXTS, hermesDataType));
					
					
					ArrayList<ContentId> images = (ArrayList<ContentId>)original.getImages();
                    if (images != null)
					    for (ContentId contentId : images) {
						    HermesElement imageElement = new HermesElement(HermesConstants.IMAGE_PREFIX+contentId.getContentIdString(), HermesTypesEnum.IMAGE.getValue(), HermesConstants.HERMES_LEVEL_IMAGES, hermesDataType);
						    imageElement.setResourceContentId(contentId.getContentIdString());
						    hermesElements.add(imageElement);
					    }

                    // EdgeCase Mappings
                    hermesElements = addEdgeCaseMetadata(hermesElements, original, hermesDataType);


					

				}else{
					/*
					 * These aspects will always be updated to handle use case such as:
					 * - image added to the article
					 * - image removed from the article
					 */
					
					hermesElementAspect = (HermesElementAspect)articleBeanDataResult.getContent().getAspect(hermesAspectName).getData();
					
					ArrayList<ContentId> images = (ArrayList<ContentId>)original.getImages();

					// remove images which are not part of the article anymore
					
					ArrayList<HermesElement> elements = hermesElementAspect.getElements();
				
					for(int i=0; i< elements.size(); i++){
				
						HermesElement hermesElement = elements.get(i);
						if(hermesElement.getName().startsWith(HermesConstants.IMAGE_PREFIX)){
							
							String imageContentId = hermesElement.getName().replaceAll(HermesConstants.IMAGE_PREFIX, "");
					
							ContentId ici = ContentIdFactory.createContentId(imageContentId);
							if(!images.contains(ici)){
								elements.remove(i);
							}
						}
					}
					
					if (images != null)
					    for (ContentId contentId : images) {
						    if(hermesElementAspect.findElementByName(HermesConstants.IMAGE_PREFIX+contentId.getContentIdString()) == null){
							    HermesElement imageElement = new HermesElement(HermesConstants.IMAGE_PREFIX+contentId.getContentIdString(),
																HermesTypesEnum.IMAGE.getValue(), HermesConstants.HERMES_LEVEL_IMAGES, hermesDataType);
							    imageElement.setResourceContentId(contentId.getContentIdString());
							    hermesElementAspect.getElements().add(imageElement);
						}
					}
				}
				/*
				 * End Polopoly To Hermes Mapping
				 */				
				
				
				/*
				 * Create aspects for hermes mapping
				 */
				ContentWrite<EdgeCaseArticleBean> cw = new ContentWrite<>(articleBeanDataResult.getContent());
				cw.setAspect(hermesAspectName, hermesElementAspect);
				
				// update/create only the hermesElement aspects in couchbase
				ContentResult<EdgeCaseArticleBean> updatedAspects = cm.update(articleBeanDataResult.getContent().getId().getContentId(), cw, SYSTEM_SUBJECT);
				
				Collection<Aspect> aspects = new ArrayList<Aspect>();	// create an empty aspects collection because the actual aspects collection is unmodifiable 
				aspects.addAll(articleBeanDataResult.getContent().getAspects());	// add all aspects, plus hermesElements just updated
				
				if(articleBeanDataResult.getContent().getAspect(hermesAspectName) != null)	// in case of existing hermesElements aspects clean existing
					aspects.remove(articleBeanDataResult.getContent().getAspect(hermesAspectName));
				
				aspects.addAll(updatedAspects.getContent().getAspects());
				res = new ContentResult<EdgeCaseArticleBean>(res, original, aspects);
		
			}


			
			

		}catch(Exception e){
			e.printStackTrace();
		}
		
		return res;

	}

    private ArrayList<HermesElement> addEdgeCaseMetadata(ArrayList<HermesElement> hermesElements, EdgeCaseArticleBean original, String hermesDataType) {

        HermesElement spElement = new HermesElement("article", HermesTypesEnum.STORY_PACKAGE.getValue(), HermesConstants.HERMES_LEVEL_SP, hermesDataType);
        spElement.getMetadata().put("SKED/TEMPLATE", original.getPrismgenre());


        switch (original.getPrismgenre()) {
            case EDGECASE_GENRE_ALBUMREVIEW:
                spElement.getMetadata().put("SKED/ALBUMREVIEW_ARTISTS", original.getArtists());
                spElement.getMetadata().put("SKED/ALBUMREVIEW_TITLE", original.getAlbumTitle());
                spElement.getMetadata().put("SKED/ALBUMREVIEW_RECORDLABEL", original.getRecordLabel());
                spElement.getMetadata().put("SKED/ALBUMREVIEW_STARRATING", original.getStarrating());
                spElement.getMetadata().put("SKED/ALBUMREVIEW_ALBUMGENRE", original.getAlbumGenre());
                spElement.getMetadata().put("SKED/ALBUMREVIEW_ARTISTURL", original.getArtistUrl());
                spElement.getMetadata().put("SKED/ALBUMREVIEW_TRACKURL", original.getTrackUrl());
            case EDGECASE_GENRE_BOOKREVIEW:
                spElement.getMetadata().put("SKED/BOOKREVIEW_ISBN13", original.getIsbnNum());
                spElement.getMetadata().put("SKED/BOOKREVIEW_TITLE", original.getBookTitle());
                spElement.getMetadata().put("SKED/BOOKREVIEW_AUTHORS", original.getAuthors());
                spElement.getMetadata().put("SKED/BOOKREVIEW_PUBLISHER", original.getPublisher());
                spElement.getMetadata().put("SKED/BOOKREVIEW_BINDING", original.getBinding());
                spElement.getMetadata().put("SKED/BOOKREVIEW_GUIDELINEPRICE", original.getGuidelinePrice());
                spElement.getMetadata().put("SKED/BOOKREVIEW_BOOKGENRE", original.getBookGenre());
                spElement.getMetadata().put("SKED/BOOKREVIEW_CURRENCY", original.getCurrency());
                spElement.getMetadata().put("SKED/BOOKREVIEW_NUMBERPAGES", original.getNumOfPages());
            case EDGECASE_GENRE_CARREVIEW:
                spElement.getMetadata().put("SKED/CARREVIEW_MARQUE", original.getMarque());
                spElement.getMetadata().put("SKED/CARREVIEW_MODEL", original.getModel());
                spElement.getMetadata().put("SKED/CARREVIEW_YEAR", original.getYear());
                spElement.getMetadata().put("SKED/CARREVIEW_ENGINE", original.getEngine());
                spElement.getMetadata().put("SKED/CARREVIEW_EXTRA", original.getExtra());
                spElement.getMetadata().put("SKED/CARREVIEW_BODY", original.getBodyType());
                spElement.getMetadata().put("SKED/CARREVIEW_FUEL", original.getFuel());
                spElement.getMetadata().put("SKED/CARREVIEW_STARRATING", original.getStarrating());
                spElement.getMetadata().put("SKED/CARREVIEW_DATEREVIEWED", original.getReviewDate());
            case EDGECASE_GENRE_CULTURESTAGEREVIEW:
                spElement.getMetadata().put("SKED/CULTURESTAGEREVIEW_TITLE", original.getCulStagTitle());
                spElement.getMetadata().put("SKED/CULTURESTAGEREVIEW_VENUE", original.getVenue());
                spElement.getMetadata().put("SKED/CULTURESTAGEREVIEW_PHONE", original.getPhoneNum());
                spElement.getMetadata().put("SKED/CULTURESTAGEREVIEW_WEBSITE", original.getWebsite());
                spElement.getMetadata().put("SKED/CULTURESTAGEREVIEW_CURRENCY", original.getCurrency());
                spElement.getMetadata().put("SKED/CULTURESTAGEREVIEW_GUIDELINEPRICE", original.getGuidelinePrice());
                spElement.getMetadata().put("SKED/CULTURESTAGEREVIEW_DATEREVIEWED", original.getReviewDate());
            case EDGECASE_GENRE_EVENTREVIEW:
                spElement.getMetadata().put("SKED/EVENTREVIEW_TITLE", original.getEventTitle());
                spElement.getMetadata().put("SKED/EVENTREVIEW_ARTISTS", original.getArtists());
                spElement.getMetadata().put("SKED/EVENTREVIEW_VENUE", original.getVenue());
                spElement.getMetadata().put("SKED/EVENTREVIEW_EVENTGENRE", original.getEventGenre());
                spElement.getMetadata().put("SKED/EVENTREVIEW_STARRATING", original.getStarrating());
                spElement.getMetadata().put("SKED/EVENTREVIEW_DATEREVIEWED", original.getReviewDate());
            case EDGECASE_GENRE_FILMREVIEW:
                spElement.getMetadata().put("SKED/FILMREVIEW_TITLE", original.getTitle());
                spElement.getMetadata().put("SKED/FILMREVIEW_IMDBID", original.getImdbid());
                spElement.getMetadata().put("SKED/FILMREVIEW_DIRECTORS", original.getDirectors());
                spElement.getMetadata().put("SKED/FILMREVIEW_ACTORS", original.getActors());
                spElement.getMetadata().put("SKED/FILMREVIEW_FILMGENRE", original.getFilmGenre());
                spElement.getMetadata().put("SKED/FILMREVIEW_CERT", original.getCert());
                spElement.getMetadata().put("SKED/FILMREVIEW_STARRATING", original.getStarrating());
                spElement.getMetadata().put("SKED/FILMREVIEW_RUNTIMEMINS", original.getRuntime());
            case EDGECASE_GENRE_GAMEREVIEW:
                spElement.getMetadata().put("SKED/GAMEREVIEW_TITLE", original.getGameTitle() );
                spElement.getMetadata().put("SKED/GAMEREVIEW_PUBLISHER", original.getPublisher() );
                spElement.getMetadata().put("SKED/GAMEREVIEW_CERT", original.getCert() );
                spElement.getMetadata().put("SKED/GAMEREVIEW_AVAILABLEPLATFORMS", original.getAvailablePlatforms() );
                spElement.getMetadata().put("SKED/GAMEREVIEW_REVIEWPLATFORM", original.getReviewPlatform() );
                spElement.getMetadata().put("SKED/GAMEREVIEW_STARRATING", original.getStarrating() );
            case EDGECASE_GENRE_HOUSEREVIEW:
                spElement.getMetadata().put("SKED/MATCHPREVIEW_COMPETITION", original.getCompetition());
                spElement.getMetadata().put("SKED/MATCHPREVIEW_TEAM1", original.getTeam1());
                spElement.getMetadata().put("SKED/MATCHPREVIEW_TEAM2", original.getTeam2());
                spElement.getMetadata().put("SKED/MATCHPREVIEW_VENUE", original.getVenue());
                spElement.getMetadata().put("SKED/MATCHPREVIEW_BROADCASTER", original.getBroadcaster());
                spElement.getMetadata().put("SKED/MATCHPREVIEW_MATCHDATE", original.getMatchdate());
            case EDGECASE_GENRE_MATCHPREVIEW:
                spElement.getMetadata().put("SKED/MATCHPREVIEW_MATCHTITLE", original.getMatchPreviewTitle());
                spElement.getMetadata().put("SKED/MATCHPREVIEW_COMPETITION", original.getCompetition());
                spElement.getMetadata().put("SKED/MATCHPREVIEW_TEAM1", original.getTeam1());
                spElement.getMetadata().put("SKED/MATCHPREVIEW_TEAM2", original.getTeam2());
                spElement.getMetadata().put("SKED/MATCHPREVIEW_VENUE", original.getVenue());
                spElement.getMetadata().put("SKED/MATCHPREVIEW_BROADCASTER", original.getBroadcaster());
                spElement.getMetadata().put("SKED/MATCHPREVIEW_MATCHDATE", original.getMatchdate());
            case EDGECASE_GENRE_MATCHREVIEW:
                spElement.getMetadata().put("SKED/MATCHREPORT_MATCHTITLE", original.getMatchReportTitle());
                spElement.getMetadata().put("SKED/MATCHREPORT_COMPETITION", original.getCompetition());
                spElement.getMetadata().put("SKED/MATCHREPORT_TEAM1", original.getTeam1());
                spElement.getMetadata().put("SKED/MATCHREPORT_TEAM2", original.getTeam2());
                spElement.getMetadata().put("SKED/MATCHREPORT_TEAM1SCORE", original.getTeam1Score());
                spElement.getMetadata().put("SKED/MATCHREPORT_TEAM2SCORE", original.getTeam2Score());
                spElement.getMetadata().put("SKED/MATCHREPORT_VENUE", original.getVenue());
                spElement.getMetadata().put("SKED/MATCHREPORT_BROADCASTER", original.getBroadcaster());
                spElement.getMetadata().put("SKED/MATCHREPORT_MATCHDATE", original.getMatchdate());
            case EDGECASE_GENRE_RECIPE:
                spElement.getMetadata().put("SKED/RECIPE_TITLE", original.getRecipeTitle());
                spElement.getMetadata().put("SKED/RECIPE_CUISINE", original.getCuisine());
                spElement.getMetadata().put("SKED/RECIPE_COURSE", original.getCourse());
                spElement.getMetadata().put("SKED/RECIPE_SERVES", original.getServes());
                spElement.getMetadata().put("SKED/RECIPE_MAKES", original.getMakes());
                spElement.getMetadata().put("SKED/RECIPE_TOTALCOOKINGMINS", original.getCookingTime());
                spElement.getMetadata().put("SKED/RECIPE_METHOD", original.getMethod());
            case EDGECASE_GENRE_RESTAURANTREVIEW:
                spElement.getMetadata().put("SKED/RESTAURANTREVIEW_TITLE", original.getRestaurantTitle());
                spElement.getMetadata().put("SKED/RESTAURANTREVIEW_PROPRIETOR", original.getProprietor());
                spElement.getMetadata().put("SKED/RESTAURANTREVIEW_ADDRESS1", original.getAddress1());
                spElement.getMetadata().put("SKED/RESTAURANTREVIEW_ADDRESS2", original.getAddress2());
                spElement.getMetadata().put("SKED/RESTAURANTREVIEW_ADDRESS3", original.getAddress3());
                spElement.getMetadata().put("SKED/RESTAURANTREVIEW_CITY", original.getCity());
                spElement.getMetadata().put("SKED/RESTAURANTREVIEW_PHONE", original.getPhoneNum());
                spElement.getMetadata().put("SKED/RESTAURANTREVIEW_WEBSITE", original.getWebsite());
                spElement.getMetadata().put("SKED/RESTAURANTREVIEW_CUISINE", original.getCuisine());
                spElement.getMetadata().put("SKED/RESTAURANTREVIEW_GEOLOCATION", original.getGeoLocation());
                spElement.getMetadata().put("SKED/RESTAURANTREVIEW_PRICERANGE", original.getPriceRange());
                spElement.getMetadata().put("SKED/RESTAURANTREVIEW_STARRATING", original.getStarrating());
            case EDGECASE_GENRE_WINEREVIEW:
                spElement.getMetadata().put("SKED/WINEREVIEW_TITLE", original.getWineTitle());
                spElement.getMetadata().put("SKED/WINEREVIEW_COUNTRY", original.getCountry());
                spElement.getMetadata().put("SKED/WINEREVIEW_COLOUR", original.getColour());
                spElement.getMetadata().put("SKED/WINEREVIEW_GRAPE", original.getGrape());
                spElement.getMetadata().put("SKED/WINEREVIEW_REGION", original.getRegion());
                spElement.getMetadata().put("SKED/WINEREVIEW_VINTAGE", original.getVintage());
                spElement.getMetadata().put("SKED/WINEREVIEW_WINEMAKER", original.getWinemaker());
                spElement.getMetadata().put("SKED/WINEREVIEW_CURRENCY", original.getCurrency());
                spElement.getMetadata().put("SKED/WINEREVIEW_GUIDELINEPRICE", original.getGuidelinePrice());
                spElement.getMetadata().put("SKED/WINEREVIEW_ALCOHOLPERCENTAGE", original.getAlchoholPercent());
                spElement.getMetadata().put("SKED/WINEREVIEW_AVAILABLEOUTLETS", original.getOutlets());
            case EDGECASE_GENRE_GENERAL:
            default:
        }

        hermesElements.add(spElement);

        return hermesElements;
    }

}
