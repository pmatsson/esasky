package esac.archive.esasky.cl.web.client.utility;

import com.allen_sauer.gwt.log.client.Log;

public final class GoogleAnalytics {

    //Categories
    public static final String CAT_Outbound = "outbound";
	public static final String CAT_SourceTooltip = "SourceTooltip";
	public static final String CAT_ContextMenu = "ContextMenu";
	public static final String CAT_TreeMap_Resize = "TreeMap_Resize";
	public static final String CAT_Tab_Resize = "Tab_Resize";
    public static final String CAT_TabOpened = "TabOpened";
    public static final String CAT_Download_CSV = "Download_CSV";
    public static final String CAT_Download_VOT = "Download_VOT";
    public static final String CAT_Download_Preview = "Download_Preview";
    public static final String CAT_Download_DD = "Download_DD";
    public static final String CAT_CtrlToolbar = "CtrlToolbar";
    public static final String CAT_SkiesMenu = "SkiesMenu";
    public static final String CAT_Convenience = "Convenience";
    public static final String CAT_Help = "Help";
    public static final String CAT_Header = "Header";
    public static final String CAT_Header_Status = "Header_Status";
    public static final String CAT_TargetList = "TargetList";
    public static final String CAT_PlanningTool = "PlanningTool";
    public static final String CAT_TabToolbar_Recenter = "TabToolbar_Recenter";
    public static final String CAT_TabToolbar_Refresh = "TabToolbar_Refresh";
    public static final String CAT_TabToolbar_CloseAll = "TabToolbar_CloseAll";
    public static final String CAT_TabToolbar_SendToSAMP = "TabToolbar_SendToSAMP";
    public static final String CAT_TabToolbar_SetStyle = "TabToolbar_SetStyle";
    public static final String CAT_TabRow_SendToVOTools = "TabRow_SendToVOTools";
    public static final String CAT_TabRow_SourcesInPublication = "TabRow_SourcesInPublication";
    public static final String CAT_TabRow_Download = "TabRow_Download";
    public static final String CAT_TabRow_Recenter = "TabRow_Recenter";
    public static final String CAT_API = "API";
    public static final String CAT_Publication = "Publication";
    public static final String CAT_Welcome = "Welcome";
    public static final String CAT_Preview = "Preview";
    public static final String CAT_Datalink = "Datalink";
    public static final String CAT_DownloadRow = "DownloadRow";
    public static final String CAT_Search = "Search";
    public static final String CAT_SAMP = "Samp";
    public static final String CAT_Count = "Count";
    public static final String CAT_Filter = "Filter";
    public static final String CAT_Screenshot = "Screenshot";
    public static final String CAT_Internationalization = "Internationalization";
    public static final String CAT_Pyesasky = "Pyesasky";
    public static final String CAT_JavaScriptAPI = "JavaScriptAPI";
    public static final String CAT_Slider = "Slider";
    public static final String CAT_RequestError = "RequestError";
    public static final String CAT_ExternalTaps = "ExternalTaps";
    public static final String CAT_TextManager = "TextManager";
    public static final String CAT_ToggleColumns = "ToggleColumns";
    public static final String CAT_BrowseHips = "BrowseHips";
    public static final String CAT_IMAGES = "Images";
    
    //Actions
    public static final String ACT_MissingTranslation = "MissingTranslation";
    public static final String ACT_LoadingOfXMLFailed = "LoadingOfXMLFailed";
    public static final String ACT_Header_ViewInWwt = "ViewInWWT";
    public static final String ACT_Header_HipsName = "HipsName";
    public static final String ACT_Header_Share = "Share";
    public static final String ACT_Header_Help = "Help";
    public static final String ACT_Header_Menu = "Menu";
    public static final String ACT_Header_Feedback = "Feedback";
    public static final String ACT_Header_VideoTutorials = "VideoTutorials";
    public static final String ACT_Header_ReleaseNotes = "ReleaseNotes";
    public static final String ACT_Header_CoordinateGrid = "CoordinateGrid";
    public static final String ACT_Header_Newsletter = "Newsletter";
    public static final String ACT_Header_AboutUs = "AboutUs";
    public static final String ACT_Header_ScreenShot = "ScreenShot";
    public static final String ACT_Header_SciMode = "SciMode";
    public static final String ACT_Header_Language = "Language";
    public static final String ACT_Header_Status_Error = "Error";
    
    public static final String ACT_ToggleColumnsOpen = "ToggleColumnsOpen";
    public static final String ACT_ToggleColumnsShow = "ShowingColumn";
    public static final String ACT_ToggleColumnsHide = "HidingColumn";
    
    public static final String ACT_ContextMenu_ViewInWwt = "ViewInWWT";
    public static final String ACT_ContextMenu_SearchInSimbad = "SearchInSimbad";
    public static final String ACT_ContextMenu_SearchInNed = "SearchInNed";
    public static final String ACT_ContextMenu_SearchInVizier = "SearchInVizier";
    public static final String ACT_ContextMenu_SearchInVizierPhotometry = "SearchInVizierPhotometry";
    
    public static final String ACT_SourceTooltip_ViewInSimbad = "ViewInSimbad";
    public static final String ACT_SourceTooltip_ViewInNed = "ViewInNed";
    public static final String ACT_SourceTooltip_ViewInVizier = "ViewInVizier";
    public static final String ACT_SourceTooltip_ViewInVizierPhotometry = "ViewInVizierPhotometry";
    public static final String ACT_SourceTooltip_ViewInWWT = "ViewInWWT";
    
    public static final String ACT_CtrlToolbar_Skies = "Skies";
    public static final String ACT_CtrlToolbar_TargetList = "TargetList";
    public static final String ACT_CtrlToolbar_PlanningTool = "PlanningTool";
    public static final String ACT_CtrlToolbar_Publications = "Publications";
    public static final String ACT_CtrlToolbar_Dice = "Dice";
    public static final String ACT_CtrlToolbar_CustomButton = "CustomButton";
    
    public static final String ACT_SkiesMenu_SelectedSky = "SelectedSky";
    public static final String ACT_SkiesMenu_SkyInfoShown = "SkyInfoShown";
    public static final String ACT_SkiesMenu_AddSkyClick = "AddSkyClick";
    public static final String ACT_SkiesMenu_AddUrl = "AddUrl";
    public static final String ACT_SkiesMenu_AddUrl_Fail = "AddUrlFail";
    public static final String ACT_SkiesMenu_BrowseHips = "BrowseHips";
    public static final String ACT_SkiesMenu_AddLocal = "AddLocal";
    public static final String ACT_SkiesMenu_AddLocalClick = "AddLocalClick";
    
    public static final String ACT_TargetList_ListSelected = "ListSelected";
    public static final String ACT_TargetList_UploadError = "UploadError";
    public static final String ACT_TargetList_UploadSuccess = "UploadSuccess";
    
    public static final String ACT_PlanningTool_InstrumentSelected = "InstrumentSelected";
    public static final String ACT_PlanningTool_AllInstrumentsClick = "AllInstrumentsClick";
    public static final String ACT_PlanningTool_CopyCoordinates = "CopyCoordinates";
    public static final String ACT_PlanningTool_DetectorSelected = "DetectorSelected";

    public static final String ACT_SAMP_ERROR = "SAMP failed";
    
    public static final String ACT_API_AuthorInURL = "AuthorInURL";
    public static final String ACT_API_BibcodeInURL = "BibcodeInURL";
    
    public static final String ACT_DataPanel_Pager_NextPage = "NextPage";
    public static final String ACT_DataPanel_Pager_PreviousPage = "PreviousPage";
    public static final String ACT_Moved = "Moved";
    
    public static final String ACT_Search_SearchQuery = "SearchQuery";
    public static final String ACT_Search_SearchResultClick = "SearchResultClick";
    public static final String ACT_Search_SearchAuthorResultClick = "SearchResultAuthorClick";
    public static final String ACT_Search_SearchAuthorResultShowMoreClick = "SearchResultAuthorShowMoreClick";
    public static final String ACT_Search_SearchBibcodeResultClick = "SearchResultBibcodeClick";
    public static final String ACT_Search_SearchBibcodeResultShowMoreClick = "SearchResultBibcodeShowMoreClick";
    public static final String ACT_Search_SearchSsoResultShowMoreClick = "SearchResultSsoShowMoreClick";
    public static final String ACT_Search_SearchResultAuto = "SearchResultAuto";
    public static final String ACT_Search_SearchTargetNotFound = "SearchTargetNotFound";
    public static final String ACT_Search_SearchWrongCoords = "SearchWrongCoords";
    
    public static final String ACT_Preview_PostcardLoadFailed = "PostcardLoadFailed";
    
    public static final String ACT_Datalink_LoadFailed = "DatalinkLoadFailed";
    
    public static final String ACT_Publication_BoxQuery = "PublicationBoxQueryTotal";
    public static final String ACT_Publication_BoxQueryFailed = "PublicationBoxQueryFailed";
    public static final String ACT_Publication_Update = "PublicationUpdateButton";
    public static final String ACT_Publication_Remove = "PublicationRemoveButton";
    public static final String ACT_Publication_UpdateOnMove = "PublicationUpdateOnMoveChanged";
    public static final String ACT_Publication_MostOrLeast = "PublicationMostOrLeastChanged";
    public static final String ACT_Publication_MoveTriggeredBoxQuery = "PublicationBoxQueryTriggeredByMoveOperation";
    public static final String ACT_Publication_TruncationNumberChanged = "PublicationTruncationNumbrtChanged";
    
    public static final String ACT_Tab_Resize = "TabResize";
    public static final String ACT_TreeMap_Resize = "TreeMapResize";
    
    public static final String ACT_Tab_Download = "TabDownload";
    public static final String ACT_Tab_Download_Failure = "TabDownloadFailure";

    public static final String ACT_Count_Count = "Count";
    
    public static final String ACT_Player_Play = "Play";
    public static final String ACT_Player_Pause = "Pause";
    public static final String ACT_Player_Next = "Next";
    public static final String ACT_Player_Previous = "Previous";
    
    public static final String ACT_Welcome_Science = "Science";
    public static final String ACT_Welcome_Explorer = "Explorer";
    public static final String ACT_Welcome_Close = "CloseWithoutSelection";
    public static final String ACT_Welcome_DoNotShowAgain = "DoNotShowAgain";
    
    public static final String ACT_Slider_Moved = "Moved";
    
    public static final String ACT_Pyesasky_goToTargetName = "goToTargetName";
    public static final String ACT_Pyesasky_setFoV = "setFoV";
    public static final String ACT_Pyesasky_goToRADec = "goToRADec";
    public static final String ACT_Pyesasky_setHiPSColorPalette = "setHiPSColorPalette";
    public static final String ACT_Pyesasky_changeHiPS = "changeHiPS";
    public static final String ACT_Pyesasky_changeHiPSWithParams = "changeHiPSWithParams";
    public static final String ACT_Pyesasky_overlayFootprints = "overlayFootprints";
    public static final String ACT_Pyesasky_overlayFootprintsWithDetails = "overlayFootprintsWithDetails";
    public static final String ACT_Pyesasky_clearFootprintsOverlay = "clearFootprintsOverlay";
    public static final String ACT_Pyesasky_deleteFootprintsOverlay = "deleteFootprintsOverlay";
    public static final String ACT_Pyesasky_overlayCatalogue = "overlayCatalogue";
    public static final String ACT_Pyesasky_overlayCatalogueWithDetails = "overlayCatalogueWithDetails";
    public static final String ACT_Pyesasky_removeCatalogue = "removeCatalogue";
    public static final String ACT_Pyesasky_clearCatalogue = "clearCatalogue";
    public static final String ACT_Pyesasky_getAvailableHiPS = "getAvailableHiPS";
    public static final String ACT_Pyesasky_addJwstWithCoordinates = "addJwstWithCoordinates";
    public static final String ACT_Pyesasky_addJwst = "addJwst";
    public static final String ACT_Pyesasky_closeJwstPanel = "closeJwstPanel";
    public static final String ACT_Pyesasky_openJwstPanel = "openJwstPanel";
    public static final String ACT_Pyesasky_clearJwstAll = "clearJwstAll";
    public static final String ACT_Pyesasky_getCenter = "getCenter";
    public static final String ACT_Pyesasky_getObservationsCount = "getObservationsCount";
    public static final String ACT_Pyesasky_count = "count";
    public static final String ACT_Pyesasky_getCataloguesCount = "getCataloguesCount";
    public static final String ACT_Pyesasky_getSpectraCount = "getSpectraCount";
    public static final String ACT_Pyesasky_plotObservations = "plotObservations";
    public static final String ACT_Pyesasky_plotCatalogues = "plotCatalogues";
    public static final String ACT_Pyesasky_plotSpectra = "plotSpectra";
    public static final String ACT_Pyesasky_plotPublications = "plotPublications";
    public static final String ACT_Pyesasky_getResultPanelData = "getResultPanelData";
    public static final String ACT_Pyesasky_showCoordinateGrid = "showCoordinateGrid";
    public static final String ACT_Pyesasky_openSkyPanel = "openSkyPanel";
    public static final String ACT_Pyesasky_closeSkyPanel = "closeSkyPanel";
    public static final String ACT_Pyesasky_addHips = "addHips";
    public static final String ACT_Pyesasky_removeHipsOnIndex = "removeHipsOnIndex";
    public static final String ACT_Pyesasky_getNumberOfSkyRows = "getNumberOfSkyRows";
    public static final String ACT_Pyesasky_setHipsSliderValue = "setHipsSliderValue";
    
    public static final String ACT_ExtTap_missingCollection = "missingCollection";
    public static final String ACT_ExtTap_missingProductType = "missingDataproduct";
    public static final String ACT_ExtTap_gettingData = "gettingData";
    public static final String ACT_ExtTap_browsing = "browsing";
    public static final String ACT_ExtTap_count = "count";
    
    public static final String ACT_TextManager_SetLang = "setLang";
    
    public static final String ACT_Outbound_click = "click";
    
    public static final String ACT_Images_hstImage_Success = "hstImageSuccess";
    public static final String ACT_Images_hstImage_Fail = "hstImageFail";

    
    //Send events methods
	public static native void sendEvent(String eventCategory, String eventAction, String eventLabel)/*-{
		try{
        	$wnd._paq.push(['trackEvent', eventCategory, eventAction, eventLabel]);
        	@esac.archive.esasky.cl.web.client.utility.GoogleAnalytics::logEvent(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(eventCategory, eventAction, eventLabel);
		}
		catch(e){}			
	}-*/; 

	public static void sendEventWithURL(String eventCategory, String eventAction){
	    sendEventWithURL(eventCategory, eventAction, "");
    }
	
	public static void sendEventWithURL(String eventCategory, String eventAction, String extra){
        sendEvent(eventCategory, eventAction, UrlUtils.getUrlForCurrentState() + "Extras: " + extra);
    }
	
	public static void logEvent(String eventCategory, String eventAction, String label){
	    Log.debug("GA Event - Category: " + eventCategory + " - Action: " + eventAction + " - Label: " + label);
	}
}
