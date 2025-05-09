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

package esac.archive.esasky.cl.web.client.api;


import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Window;

import esac.archive.esasky.cl.web.client.Controller;
import esac.archive.esasky.cl.web.client.callback.JsonRequestCallback;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.utility.IniFileParser;
import esac.archive.esasky.cl.web.client.utility.JSONUtils;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky.SelectSkyPanel;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.client.HiPS;
import esac.archive.esasky.ifcs.model.client.HiPSCoordsFrame;
import esac.archive.esasky.ifcs.model.client.HipsWavelength;
import esac.archive.esasky.ifcs.model.client.SkiesMenu;
import esac.archive.esasky.ifcs.model.client.SkiesMenuEntry;
import esac.archive.esasky.ifcs.model.shared.ColorPalette;
import esac.archive.esasky.ifcs.model.client.HiPS.HiPSImageFormat;

public class ApiHips extends ApiBase{
	
	private long lastGASliderSent = 0;
	
	public ApiHips(Controller controller) {
		this.controller = controller;
	}
	
	
	public void openSkyPanel() {
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_PYESASKY_OPENSKYPANEL, "");
		if(!controller.getRootPresenter().getCtrlTBPresenter().getSelectSkyPresenter().isShowing()) {
			controller.getRootPresenter().getCtrlTBPresenter().getSelectSkyPresenter().toggle();
		}
	}

	public void closeSkyPanel() {
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_PYESASKY_CLOSESKYPANEL, "");
		if(controller.getRootPresenter().getCtrlTBPresenter().getSelectSkyPresenter().isShowing()) {
			controller.getRootPresenter().getCtrlTBPresenter().getSelectSkyPresenter().toggle();
		}
	}
	
	public void addHiPS(String wantedHiPSName, JavaScriptObject widget) {
		SelectSkyPanel.getInstance().createSky(true);
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_PYESASKY_ADDHIPS, wantedHiPSName);
		if(!setHiPS(wantedHiPSName, widget)) {
			SelectSkyPanel.getSelectedSky().notifyClose();
		}
	}
	
	public void addHiPSWithParams(String surveyName, String surveyRootUrl, String surveyFrame,
			int maximumNorder, String imgFormat, String category, boolean isDefault, boolean useCredentials) {
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_PYESASKY_ADDHIPS, surveyRootUrl);
		if(!isDefault) {
			// CASE 1: normal case, just adding HiPS
			SelectSkyPanel.getInstance().createSky(true, category, isDefault);
			setHiPSWithParams(surveyName, surveyRootUrl, surveyFrame, maximumNorder, imgFormat, category, isDefault, useCredentials);

		} else {
			if(HipsWavelength.getListOfUserHips().keySet().contains(category)) {
				// CASE 2: add default HiPS in an existing category
				// Simply add the HiPS to the existing category, no new row is needed
				addHipsToCategory(surveyName, surveyRootUrl, surveyFrame, maximumNorder, imgFormat, category, isDefault, useCredentials);
				SelectSkyPanel.updateCustomHiPS();
			}else {
				// CASE 3: add default HiPS in a new category
				// Create the category, create row and add the HiPS
				SelectSkyPanel.getInstance().createSky(true, category, isDefault);
				setHiPSWithParams(surveyName, surveyRootUrl, surveyFrame, maximumNorder, imgFormat, category, isDefault, useCredentials);

			}
		}
		
	}
	
	public void removeSkyRow(int index, JavaScriptObject widget) {
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_PYESASKY_REMOVEHIPSONINDEX, "");
		String msg = "";
		if(index < 0) {
			for(int i = SelectSkyPanel.getInstance().getNumberOfSkyRows() - 1; i > 0 ;i--) {
				SelectSkyPanel.getInstance().removeSky(i);
			}
			sendBackSuccessToWidget(widget);

		}else {
			
			if(SelectSkyPanel.getInstance().removeSky(index)) {
				sendBackSuccessToWidget(widget);
			}
			
			else {
				msg = "Index out of bounds. Max number is: " + Integer.toString(SelectSkyPanel.getInstance().getNumberOfSkyRows());
				sendBackErrorMsgToWidget(msg, widget);
			}
		}
	}
	
	public void getNumberOfSkyRows(JavaScriptObject widget) {
		int count = SelectSkyPanel.getInstance().getNumberOfSkyRows();
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_PYESASKY_GETNUMBEROFSKYROWS, Integer.toString(count));
		sendBackSingleValueToWidget(new JSONNumber(count), widget);
	}
	
	public void setHiPSSliderValue(double value) {
		if(System.currentTimeMillis() - lastGASliderSent > 1000) {
			lastGASliderSent = System.currentTimeMillis();
			GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_PYESASKY_SETHIPSSLIDERVALUE, Double.toString(value));
		}
		SelectSkyPanel.getInstance().setSliderValue(value);
	}

	public boolean setHiPS(String wantedHiPSName, JavaScriptObject widget) {
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_PYESASKY_CHANGEHIPS, wantedHiPSName);
		if (!SelectSkyPanel.getSelectedSky().setSelectHips(wantedHiPSName, false, null)) {
			JSONObject error = new JSONObject();
			error.put(ApiConstants.MESSAGE, new JSONString("No HiPS called: " + wantedHiPSName + " found"));

			JSONObject wavelengthMap = getAvailableHiPS("", true);
			error.put(ApiConstants.ERROR_AVAILABLE, wavelengthMap);
			sendBackErrorToWidget(error, widget);
			return false;
		}
		sendBackSuccessToWidget(widget);
		return true;
	}
	
	public void setHiPSWithParams(String surveyName, String baseUrl, String category, Boolean isDefault, boolean add, final JavaScriptObject widget, Boolean useCredentials) {
		
		if("https:".equals(Window.Location.getProtocol()) && baseUrl.startsWith("http:")){
			baseUrl = baseUrl.replaceFirst("http:", "https:");
		}
		
		final String surveyRootUrl = baseUrl;
		
		final String propertiesUrl = surveyRootUrl +  "/" +  ApiConstants.HIPS_PROPERTIES_FILE;
		JSONUtils.getJSONFromUrl(propertiesUrl, new JsonRequestCallback("", propertiesUrl) {

			@Override
			protected void onSuccess(Response response) {
				GeneralJavaScriptObject props = IniFileParser.parseIniString(response.getText());
				
				if(detectHipSPropertyErrors(props, widget)) {
					return;
				}
				
				String imgFormat = parseImgFormat(props);
				
				String surveyFrame = props.getStringProperty(ApiConstants.HIPS_PROP_FRAME);
				
				int maximumNorder = (int) props.getDoubleProperty(ApiConstants.HIPS_PROP_ORDER);
				
				if(add) {
					addHiPSWithParams(surveyName, surveyRootUrl, surveyFrame, maximumNorder, imgFormat, category, isDefault, useCredentials);
				}else {
					setHiPSWithParams(surveyName, surveyRootUrl, surveyFrame, maximumNorder, imgFormat, category, isDefault, useCredentials);
				}
				sendBackSuccessToWidget(widget);

			}
			
			@Override
			public void onError(Request request, Throwable exception) {
				sendBackErrorMsgToWidget(ApiConstants.HIPS_PROP_ERROR_LOADING + propertiesUrl, widget);
			}
			
		}, useCredentials);
		
	}
	
	public boolean detectHipSPropertyErrors(GeneralJavaScriptObject props, JavaScriptObject widget) {
		if(!props.hasProperty(ApiConstants.HIPS_PROP_FRAME )|| "".equals(props.getStringProperty(ApiConstants.HIPS_PROP_FRAME))) {
			sendBackErrorMsgToWidget(ApiConstants.HIPS_PROP_ERROR + ApiConstants.HIPS_PROP_FRAME, widget);
			return true;
		}
		if(!props.hasProperty(ApiConstants.HIPS_PROP_FORMAT )|| "".equals(props.getStringProperty(ApiConstants.HIPS_PROP_FORMAT))) {
			sendBackErrorMsgToWidget(ApiConstants.HIPS_PROP_ERROR + ApiConstants.HIPS_PROP_FORMAT, widget);
			return true;
		}
		if(!props.hasProperty(ApiConstants.HIPS_PROP_ORDER )|| "".equals(props.getStringProperty(ApiConstants.HIPS_PROP_ORDER))) {
			sendBackErrorMsgToWidget(ApiConstants.HIPS_PROP_ERROR + ApiConstants.HIPS_PROP_ORDER, widget);
			return true;
		}
		return false;
	}
	
	public String parseImgFormat(GeneralJavaScriptObject props) {
		String[] imgFormats = props.getStringProperty(ApiConstants.HIPS_PROP_FORMAT).split(" ");

		String imgFormat = "jpg";
		if(imgFormats.length > 1) {
			for(String format : imgFormats) {
				if(format.equalsIgnoreCase("png") || format.equalsIgnoreCase("jpg") || format.equalsIgnoreCase("jpeg")) {
					imgFormat = format;
					break;
				}
			}
		}else {
			imgFormat = imgFormats[0];
		}
		
		return imgFormat;
	}
	
	public void setHiPSWithParams(String surveyName, String surveyRootUrl, String surveyFrame,
			int maximumNorder, String imgFormat, String category, boolean isDefault, boolean useCredentials) {
		
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_PYESASKY_CHANGEHIPSWITHPARAMS, surveyRootUrl);

		HiPS hips = new HiPS();
		hips.setSurveyId(surveyName);
		hips.setSurveyName(surveyName);
		hips.setSurveyRootUrl(surveyRootUrl);
		HiPSCoordsFrame surveyFrameEnum = HiPSCoordsFrame.GALACTIC.name().toLowerCase()
				.contains(surveyFrame.toLowerCase()) ? HiPSCoordsFrame.GALACTIC : HiPSCoordsFrame.EQUATORIAL;
		hips.setSurveyFrame(surveyFrameEnum);
		hips.setMaximumNorder(maximumNorder);
		HiPSImageFormat hipsImageFormatEnum = HiPSImageFormat.png.name().toLowerCase().contains(imgFormat.toLowerCase())
				? HiPSImageFormat.png : HiPSImageFormat.jpg;
		hips.setImgFormat(hipsImageFormatEnum);
		if(category != null && !category.equals("")) {
			hips.setHipsCategory(category);
		}

		hips.setDefaultHIPS(isDefault);
		hips.setUseCredentials(useCredentials);
		SelectSkyPanel.setHiPSFromAPI(hips, true);
	}
	
	public void addHipsToCategory(String surveyName, String surveyRootUrl, String surveyFrame,
			int maximumNorder, String imgFormat, String category, boolean isDefault, boolean useCredentials) {
		
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_PYESASKY_CHANGEHIPSWITHPARAMS, surveyRootUrl);

		HiPS hips = new HiPS();
		hips.setSurveyId(surveyName);
		hips.setSurveyName(surveyName);
		hips.setSurveyRootUrl(surveyRootUrl);
		HiPSCoordsFrame surveyFrameEnum = HiPSCoordsFrame.GALACTIC.name().toLowerCase()
				.contains(surveyFrame.toLowerCase()) ? HiPSCoordsFrame.GALACTIC : HiPSCoordsFrame.EQUATORIAL;
		hips.setSurveyFrame(surveyFrameEnum);
		hips.setMaximumNorder(maximumNorder);
		HiPSImageFormat hipsImageFormatEnum = HiPSImageFormat.png.name().toLowerCase().contains(imgFormat.toLowerCase())
				? HiPSImageFormat.png : HiPSImageFormat.jpg;
		hips.setImgFormat(hipsImageFormatEnum);
		if(category != null && !category.equals("")) {
			hips.setHipsCategory(category);
		}

		hips.setDefaultHIPS(isDefault);
		hips.setUseCredentials(useCredentials);
		HipsWavelength.getListOfUserHips().get(category).add(hips);
		SelectSkyPanel.updateCustomHiPS();
	}

	public void setHiPSColorPalette(String colorPalette, JavaScriptObject widget) {
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_PYESASKY_SETHIPSCOLORPALETTE, colorPalette);
		
		JSONArray available = new JSONArray();
		int i = 0;
		ColorPalette colorPaletteEnum = null;
		for(ColorPalette c : ColorPalette.values()) {
			if(colorPalette.toUpperCase().equals(c.toString())) {
				colorPaletteEnum = ColorPalette.valueOf(colorPalette.toUpperCase());
				break;
			}
			available.set(i, new JSONString(c.toString()));
			i++;
		}
		
		if(colorPaletteEnum == null) {
			JSONObject error = new JSONObject();
			error.put(ApiConstants.MESSAGE, new JSONString(ApiConstants.HIPS_ERROR_COLORPALETTE));
			error.put(ApiConstants.ERROR_AVAILABLE, available);
			sendBackErrorToWidget(error, widget);
			return;
		}
		
		SelectSkyPanel.getSelectedSky().setColorPalette(colorPaletteEnum);
		sendBackSuccessToWidget(widget);
	}
	
	
	public JSONObject getHipsAllWavelengths( boolean onlyName) {
		SkiesMenu skiesMenu = controller.getRootPresenter().getCtrlTBPresenter().getSelectSkyPresenter().getSkiesMenu();

		JSONObject wavelengthMap = new  JSONObject();
		for (SkiesMenuEntry currSkiesMenuEntry : skiesMenu.getMenuEntries()) {
			String currWavelength = currSkiesMenuEntry.getWavelength();
			JSONObject hips = getHiPSByWavelength(currWavelength);
			if(onlyName) {
				JSONArray hipsNames = new JSONArray();
				int i = 0;
				for(String key : hips.keySet()) {
					hipsNames.set(i++, new JSONString(key));
				}
				wavelengthMap.put(currWavelength, hipsNames);
			}else {
				wavelengthMap.put(currWavelength, hips);
			}
		}
		return wavelengthMap;

	}	
	
	public JSONObject getAvailableHiPS(String wavelength, boolean onlyName) {
		//TODO Looks in skiesMenu which doesn't contain user added HiPS
		
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_PYESASKY_GETAVAILABLEHIPS, wavelength);
		
		String hipsWavelength;
		if(wavelength == null && "".equals(wavelength)) {
			hipsWavelength = null;
		}else {
			try {
				hipsWavelength = wavelength;	
			}catch(Exception e) {
				Log.debug("[APIHips]" + e.getMessage(),e);
				hipsWavelength = null;
			}
		}
		if (null == hipsWavelength || wavelength.equalsIgnoreCase("ALL") || wavelength.equals("")) {
			return getHipsAllWavelengths(onlyName);
		} else {
			return getHiPSByWavelength(hipsWavelength);
		}
	}
	
	public void getAvailableHiPS(String wavelength, JavaScriptObject widget) {
		JSONObject wavelengthMap = getAvailableHiPS(wavelength, false);
		sendBackToWidget(wavelengthMap, null, widget);
	}

	private JSONObject getHiPSByWavelength(String wavelength) {

		JSONObject hipsMap = new JSONObject();
		SkiesMenu skiesMenu = controller.getRootPresenter().getCtrlTBPresenter().getSelectSkyPresenter().getSkiesMenu();
		for (SkiesMenuEntry currSkiesMenuEntry : skiesMenu.getMenuEntries()) {
			if (currSkiesMenuEntry.getWavelength() == wavelength) {
				for (HiPS currHiPS : currSkiesMenuEntry.getHips()) {
					JSONObject currHiPSJSON = new JSONObject();
					currHiPSJSON.put(ApiConstants.HIPS_LABEL, new JSONString(currHiPS.getSurveyId()));
					currHiPSJSON.put(ApiConstants.HIPS_URL, new JSONString(currHiPS.getSurveyRootUrl()));
					currHiPSJSON.put(ApiConstants.HIPS_FRAME, new JSONString(currHiPS.getSurveyFrame().getName()));
					currHiPSJSON.put(ApiConstants.HIPS_MAX_ORDER, new JSONString(Integer.toString(currHiPS.getMaximumNorder())));
					currHiPSJSON.put(ApiConstants.HIPS_FORMAT, new JSONString(currHiPS.getImgFormat().name()));
					hipsMap.put(currHiPS.getSurveyId(),currHiPSJSON);
				}
			}
		}
		return hipsMap;
	}
	
}
