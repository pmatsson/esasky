/*
ESASky
Copyright (C) 2025 European Space Agency

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published
by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package esac.archive.esasky.cl.web.client.utility;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Navigator;
import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky.SelectSkyPanel;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

import java.util.AbstractMap.SimpleEntry;

public final class UrlUtils {

	private static String selectedHstImageId = null;
	private static String selectedJwstImageId = null;
	private static String selectedEuclidImageId = null;

	public static String getUrlForCurrentState() {
	    
		String encodedRaDeg = URL.encodeQueryString(new Double(AladinLiteWrapper.getCenterRaDeg()).toString());
		String encodedDecDeg = URL.encodeQueryString(new Double(AladinLiteWrapper.getCenterDecDeg()).toString());
		String encodedFov = URL.encodeQueryString(new Double(AladinLiteWrapper.getAladinLite().getFovDeg()).toString());
		String encodedHips = URL.encodeQueryString(SelectSkyPanel.getNameOfSelectedHips());
		String encodedCooFrame = URL.encodeQueryString(AladinLiteWrapper.getCoordinatesFrame().toString());
		String encodedProjection = URL.encodeQueryString(AladinLiteWrapper.getCurrentProjection());
		String hostName = Window.Location.getHost();
		String baseUrl = Window.Location.getPath();
		
		String bibcodeOrAuthor = "";
        if(urlHasBibcode()){
            bibcodeOrAuthor = "&" + EsaSkyWebConstants.PUBLICATIONS_BIBCODE_URL_PARAM + "=" + URL.encodeQueryString(Window.Location.getParameterMap().get(EsaSkyWebConstants.PUBLICATIONS_BIBCODE_URL_PARAM).get(0));
        } else if(urlHasAuthor()){
            bibcodeOrAuthor = "&" + EsaSkyWebConstants.PUBLICATIONS_AUTHOR_URL_PARAM + "=" + URL.encodeQueryString(Window.Location.getParameterMap().get(EsaSkyWebConstants.PUBLICATIONS_AUTHOR_URL_PARAM).get(0));
        }
        
		String codeServer = getCodeServer(hostName);
		
		String logLevel = "";
		if(Window.Location.getParameterMap().containsKey("log_level")){
			logLevel = "&log_level=" + Window.Location.getParameterMap().get("log_level").get(0);
		}

		String language = "";
		if(Modules.getModule(EsaSkyWebConstants.MODULE_INTERNATIONALIZATION)) {
			language = "&" + EsaSkyConstants.INTERNATIONALIZATION_LANGCODE_URL_PARAM + "=" + GUISessionStatus.getCurrentLanguage();
		}
		
		String toggleColumns = "";
		if(Window.Location.getParameterMap().containsKey(EsaSkyWebConstants.URL_PARAM_TOGGLE_COLUMNS)) {
		    toggleColumns = "&" + EsaSkyWebConstants.URL_PARAM_TOGGLE_COLUMNS + "=" + Window.Location.getParameterMap().get(EsaSkyWebConstants.URL_PARAM_TOGGLE_COLUMNS).get(0);
		}
		
		String layout = "";
		if(Modules.getMode() != null && !"".equals(Modules.getMode())) {
			layout = "&" + EsaSkyWebConstants.URL_PARAM_LAYOUT + "=" + Modules.getMode();
		}

		String hstImage = "";
		String jwstImage = "";
		String euclidImage = "";
		if (selectedHstImageId != null) {
			hstImage = "&" + EsaSkyWebConstants.URL_PARAM_HST_IMAGE + "=" + selectedHstImageId;
		} else if (selectedJwstImageId != null) {
			jwstImage = "&" + EsaSkyWebConstants.URL_PARAM_JWST_IMAGE + "=" + selectedJwstImageId;
		} else if (selectedEuclidImageId != null) {
			euclidImage = "&" + EsaSkyWebConstants.URL_PARAM_EUCLID_IMAGE + "=" + selectedEuclidImageId;
		}
		
		String bookmarkUrl = baseUrl 
				+ "?" + EsaSkyWebConstants.URL_PARAM_TARGET + "=" + encodedRaDeg + "%20" + encodedDecDeg 
				+ "&" + EsaSkyWebConstants.URL_PARAM_HIPS + "=" + encodedHips 
				+ "&" + EsaSkyWebConstants.URL_PARAM_FOV + "=" + encodedFov
				+ "&" + EsaSkyWebConstants.URL_PARAM_PROJECTION + "=" + encodedProjection
				+ "&" + EsaSkyWebConstants.URL_PARAM_FRAME_COORD + "=" + encodedCooFrame
				+ "&" + EsaSkyWebConstants.URL_PARAM_SCI_MODE + "=" + GUISessionStatus.getIsInScienceMode()
				+ language
				+ bibcodeOrAuthor 
				+ codeServer 
				+ toggleColumns
				+ logLevel
				+ layout
				+ hstImage
				+ jwstImage
				+ euclidImage;
		return bookmarkUrl;
	}
	
	private static String getCodeServer(String hostName) {
	    String codeServer = "";
	    String userAgent = Navigator.getUserAgent();
        if (hostName.contains("localhost") && userAgent.contains("Firefox") && new Double(userAgent.substring( (userAgent.indexOf("Firefox") + 8) )) < 30) {
            codeServer = "&gwt.codesvr=127.0.0.1:9997";
        }
        return codeServer;
	}
	
	public static native void updateURLWithoutReloadingJS(String newUrl) /*-{
		$wnd.history.replaceState(newUrl, "", newUrl);
	}-*/;
	
    public static boolean urlHasBibcode() {
        return Window.Location.getParameterMap().containsKey(EsaSkyWebConstants.PUBLICATIONS_BIBCODE_URL_PARAM);
    }
    
    public static boolean urlHasAuthor() {
        return Window.Location.getParameterMap().containsKey(EsaSkyWebConstants.PUBLICATIONS_AUTHOR_URL_PARAM);
    }

	public static boolean urlLayoutIsKiosk() {
		return "kiosk".equalsIgnoreCase(Window.Location.getParameter(EsaSkyWebConstants.URL_PARAM_LAYOUT));

	}

	public static boolean urlHasOutreachImage() {
		return Window.Location.getParameterMap().containsKey(EsaSkyWebConstants.URL_PARAM_JWST_IMAGE)
				|| Window.Location.getParameterMap().containsKey(EsaSkyWebConstants.URL_PARAM_HST_IMAGE)
				|| Window.Location.getParameterMap().containsKey(EsaSkyWebConstants.URL_PARAM_EUCLID_IMAGE);
	}

    public static String getUrlLangCode() {
        if (Window.Location.getParameterMap().containsKey(EsaSkyConstants.INTERNATIONALIZATION_LANGCODE_URL_PARAM)) {
            final String langCode = Window.Location.getParameter(EsaSkyConstants.INTERNATIONALIZATION_LANGCODE_URL_PARAM).toLowerCase();
			final boolean includeLimitedLang = urlLayoutIsKiosk();
			for(SimpleEntry<String, String> entry : EsaSkyConstants.getAvailableLanguages(includeLimitedLang)) {
    			if(entry.getKey().equalsIgnoreCase(langCode)) {
    				return langCode;
    			}
    		}
        }
        Log.debug("Lang code empty");
        return "";
    }
    
    public static void openUrl(String url) {
        if("https:".equals(Window.Location.getProtocol()) && url.startsWith("http:")){
            url = url.replaceFirst("http:", "https:");
        }
        Window.open(url, "", "");
    }

	public static void setSelectedOutreachImageId(String id, CommonTapDescriptor descriptor) {
		switch (descriptor.getMission()) {
            case EsaSkyConstants.JWST_MISSION:
				selectedJwstImageId = id;
				break;
			case EsaSkyConstants.EUCLID_MISSION:
				selectedEuclidImageId = id;
				break;
			default:
				selectedHstImageId = id;
		}

		UrlUtils.updateURLWithoutReloadingJS(UrlUtils.getUrlForCurrentState());
	}
}
