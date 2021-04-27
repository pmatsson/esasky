package esac.archive.esasky.cl.web.client.api;

import java.util.LinkedList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

import esac.archive.esasky.cl.web.client.Controller;
import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.utility.exceptions.MapKeyException;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.descriptor.BaseDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.CustomTreeMapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;

public class ApiModules extends ApiBase{
	
	private ApiEvents apiEvents;
	
	private final String MISSING_TREEMAP_PROPERTY = "Missing treeMap property name";
	
	public ApiModules(Controller controller, ApiEvents apiEvents) {
		this.controller = controller;
		this.apiEvents = apiEvents;
	}
	
	public void showCoordinateGrid(boolean show) {
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_showCoordinateGrid,Boolean.toString(show));
		controller.getRootPresenter().getHeaderPresenter().toggleGrid(show);
	}

	public void removeCustomButton(GeneralJavaScriptObject input, JavaScriptObject widget) {
		String buttonName = "";
		if(input.hasProperty(ApiConstants.BUTTON_NAME)) {
			buttonName = input.getStringProperty(ApiConstants.BUTTON_NAME);
			
		} else {
			sendBackErrorMsgToWidget(MISSING_TREEMAP_PROPERTY, widget);
			return;
		}
		controller.getRootPresenter().getCtrlTBPresenter().removeCustomButton(buttonName);
		
	}
	public void addCustomButton(GeneralJavaScriptObject input, JavaScriptObject widget) {
		String buttonName = "";
		if(input.hasProperty(ApiConstants.BUTTON_NAME)) {
			buttonName = input.getStringProperty(ApiConstants.BUTTON_NAME);
			
		} else {
			sendBackErrorMsgToWidget(MISSING_TREEMAP_PROPERTY, widget);
			return;
		}
		
		String description = "";
		if(input.hasProperty(ApiConstants.BUTTON_DESCRIPTION)) {
			description = input.getStringProperty(ApiConstants.BUTTON_DESCRIPTION);
		}
		
		String iconText = buttonName;
		if(input.hasProperty(ApiConstants.BUTTON_ICON_TEXT)) {
			iconText = input.getStringProperty(ApiConstants.BUTTON_ICON_TEXT);
		}
		
		
		EsaSkyButton button = controller.getRootPresenter().getCtrlTBPresenter().addCustomButton(buttonName, iconText, description);
		final String returnName = buttonName;
		button.addClickHandler(event -> apiEvents.ctrlBarButtonClicked(returnName));
		
	}

	public void addCustomTreeMap(GeneralJavaScriptObject input, JavaScriptObject widget) {
		String treeMapName = "";
		if(input.hasProperty(ApiConstants.TREEMAP_NAME_OLD)) {
			treeMapName = input.getStringProperty(ApiConstants.TREEMAP_NAME_OLD);
			
		}else if(input.hasProperty(ApiConstants.TREEMAP_NAME)) {
				treeMapName = input.getStringProperty(ApiConstants.TREEMAP_NAME);
		
		} else {
			sendBackErrorMsgToWidget(MISSING_TREEMAP_PROPERTY, widget);
			return;
		}
		
		String description = "";
		if(input.hasProperty(ApiConstants.TREEMAP_DESCRIPTION)) {
			description = input.getStringProperty(ApiConstants.TREEMAP_DESCRIPTION);
		}
		
		String iconText = treeMapName;
		if(input.hasProperty(ApiConstants.TREEMAP_ICON_TEXT)) {
			iconText = input.getStringProperty(ApiConstants.TREEMAP_ICON_TEXT);
		}
		
		GeneralJavaScriptObject descriptorArray = input.getProperty(ApiConstants.TREEMAP_MISSIONS);
		if(descriptorArray == null) {
			sendBackErrorMsgToWidget(MISSING_TREEMAP_PROPERTY, widget);
			return;
		}
		
		List<IDescriptor> descriptors = createTreeMapDescriptors(widget, descriptorArray);
		if(descriptors.isEmpty()) {
		    return;
		}
		
		CustomTreeMapDescriptor customTreeMapDescriptor = new CustomTreeMapDescriptor(treeMapName, description, iconText, descriptors);
		
		customTreeMapDescriptor.setOnMissionClicked(mission -> apiEvents.treeMapClicked(customTreeMapDescriptor.getName(), mission));
		
		controller.getRootPresenter().getCtrlTBPresenter().addCustomTreeMap(customTreeMapDescriptor);
	}
	
	public void updateTreeMapMission(GeneralJavaScriptObject input, boolean add, JavaScriptObject widget) {
		
		String treeMapName = "";
		if(input.hasProperty(ApiConstants.TREEMAP_NAME_OLD)) {
			treeMapName = input.getStringProperty(ApiConstants.TREEMAP_NAME_OLD);
			
		}else if(input.hasProperty(ApiConstants.TREEMAP_NAME)) {
				treeMapName = input.getStringProperty(ApiConstants.TREEMAP_NAME);
		
		} else {
			sendBackErrorMsgToWidget("Missing treeMap property name", widget);
			return;
		}
		
		GeneralJavaScriptObject descriptorArray = input.getProperty(ApiConstants.TREEMAP_MISSIONS);
		if(descriptorArray == null) {
			sendBackErrorMsgToWidget("Missing treeMap missions", widget);
			return;
		}
		
		CustomTreeMapDescriptor customTreeMapDescriptor = controller.getRootPresenter().getCtrlTBPresenter().getCustomTreeMapDescriptor(treeMapName);
		List<IDescriptor> newMissions = createTreeMapDescriptors(widget, descriptorArray);
		if(add) {
			customTreeMapDescriptor.getMissionDescriptors().addAll(newMissions);
		}else {
			for(IDescriptor missionToRemove : newMissions) {
				for(IDescriptor existingMission : customTreeMapDescriptor.getMissionDescriptors()) {
					if(missionToRemove.getDescriptorId().equals(existingMission.getDescriptorId())){
						customTreeMapDescriptor.getMissionDescriptors().remove(existingMission);
					}
				}
			}
		}
		controller.getRootPresenter().getCtrlTBPresenter().updateCustomTreeMap(customTreeMapDescriptor);
		
	}

    private List<IDescriptor> createTreeMapDescriptors(JavaScriptObject widget,
            GeneralJavaScriptObject descriptorArray) {
        List<IDescriptor> descriptors = new LinkedList<>();
		int i = 0;
		while(descriptorArray.hasProperty(Integer.toString(i))) {
			
			GeneralJavaScriptObject mission = descriptorArray.getProperty(Integer.toString(i));
			
			BaseDescriptor descriptor = new BaseDescriptor() {
				@Override
				public String getIcon() {
					return null;
				}
			};
			
			String missionName = "";
			if(mission.hasProperty(ApiConstants.TREEMAP_MISSION_NAME)) {
				missionName = mission.getStringProperty(ApiConstants.TREEMAP_MISSION_NAME);
			} else {
				sendBackErrorMsgToWidget("Missing mission property \"" + ApiConstants.TREEMAP_MISSION_NAME + "\"", widget);
				return descriptors;
			}
			
			String color = "";
			if(mission.hasProperty(ApiConstants.TREEMAP_MISSION_COLOR)) {
				color = mission.getStringProperty(ApiConstants.TREEMAP_MISSION_COLOR);
			} else {
				sendBackErrorMsgToWidget("Missing mission property \"" + ApiConstants.TREEMAP_MISSION_COLOR +"\"", widget);
				return descriptors;
			}
			
			descriptor.setMission(missionName);
			descriptor.setGuiShortName(missionName);
			descriptor.setGuiLongName(missionName);
			descriptor.setDescriptorId(missionName);
			descriptor.setPrimaryColor(color);
			descriptor.setUniqueIdentifierField(ApiConstants.OBS_NAME);
			descriptor.setTapSTCSColumn("");
			descriptor.setSampEnabled(false);
			descriptor.setFovLimit(360.0);
			descriptor.setTapTable("<not_set>");
			descriptor.setTabCount(0);
			descriptors.add(descriptor);
			i++;
		}
        return descriptors;
    }
	
	public void setModuleVisibility(GeneralJavaScriptObject data, JavaScriptObject widget) {
		try {
			String propertiesString = data.getProperties();
			String[] properties = {propertiesString};
			if(propertiesString.contains(",")) {
				properties = propertiesString.split(",");
			}
			for(String key : properties) {
				Modules.setModule(key, GeneralJavaScriptObject.convertToBoolean(data.getProperty(key)));
			}
			controller.getRootPresenter().updateModuleVisibility();
			
		}catch(MapKeyException e) {
			
			Log.debug("[ApiModules]" + e.getMessage(), e);
			
			String message = "Input needs to be on format {key:boolean}";
			JSONObject error = new JSONObject();
			error.put(ApiConstants.MESSAGE, new JSONString(message));
			
			JSONArray availableModules = getAvailableModules();
			error.put(ApiConstants.ERROR_AVAILABLE, availableModules);
			sendBackErrorToWidget(error, widget);
		}
	}
	
	public void getAvailableModules(JavaScriptObject widget) {
		JSONArray availableModules = getAvailableModules();
		JSONObject obj = new JSONObject();
		obj.put(ApiConstants.ERROR_AVAILABLE, availableModules);
		sendBackValuesToWidget(obj, widget);
	}
	
	public JSONArray getAvailableModules() {
		JSONArray availableModules = new JSONArray();
		int i = 0;
		for(String module : Modules.getModuleKeys()) {
			availableModules.set(i, new JSONString(module));
			i++;
		}
		return availableModules;
	}
}
