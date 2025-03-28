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

public class ApiConstants {
	
	public static final String CAT_NAME = "name";
	
	public static final String OBS_NAME = "name";

	public static final String SHAPE_NAME = "name";
	public static final String SHAPE_ID = "id";
	public static final String SHAPE_OVERLAY = "overlay";
	public static final String SHAPE_OVERLAYS = "overlays";
	public static final String SHAPE_AREA_POLYGON = "selection_area";
	
	public static final String PANEL_ID = "id";
	public static final String PANEL_DATA_ERROR = "No panel with data open";

	public static final String TREEMAP = "treeMap";
	public static final String TREEMAP_MISSION = "mission";
	public static final String TREEMAP_MISSIONS = "missions";
	public static final String TREEMAP_MISSION_NAME = "name";
	public static final String TREEMAP_MISSION_COLOR = "color";

	public static final String TREEMAP_LOCATION = "location";
	public static final String TREEMAP_INFO = "info";
	public static final String TREEMAP_NAME_OLD = "name";
	public static final String TREEMAP_NAME = "treeMapName";
	public static final String TREEMAP_ICON_TEXT = "iconText";
	public static final String TREEMAP_DESCRIPTION = "description";

	
	public static final String BUTTON = "button";
	public static final String BUTTON_DESCRIPTION = "description";
	public static final String BUTTON_NAME = "name";
	public static final String BUTTON_ICON_TEXT = "iconText";
	public static final String BUTTON_ICON = "iconName";
	
	
	public static final String COUNT_TOTAL = "total";
	
	public static final String HIPS_LABEL = "label";
	public static final String HIPS_FRAME = "frame";
	public static final String HIPS_URL = "url";
	public static final String HIPS_MAX_ORDER = "maxOrder";
	public static final String HIPS_FORMAT = "format";
	public static final String HIPS_PROPERTIES_FILE = "properties";
	public static final String HIPS_ERROR_COLORPALETTE = "Wrong colorpalette value";
	
	public static final String HIPS_PROP_FRAME = "hips_frame";
	public static final String HIPS_PROP_FORMAT = "hips_tile_format";
	public static final String HIPS_PROP_ORDER = "hips_order";
	public static final String HIPS_PROP_TITLE = "obs_title";
	public static final String HIPS_PROP_DESC = "obs_description";
	public static final String HIPS_PROP_DATE = "hips_creation_date";
	public static final String HIPS_PROP_ERROR = "Failed to parse properties file for property: ";
	public static final String HIPS_PROP_ERROR_LOADING = "Failed to load properties file: ";

	public static final String MOC_LINE_STYLE = "lineStyle";
	public static final String MOC_OPACITY = "opacity";
	public static final String MOC_MODE = "mode";
	public static final String MOC_HEALPIX = "healpix";
	public static final String MOC_Q3C = "q3c";
	public static final String MOC_ADD_TAB = "addTab";
	
	
	public static final String EXTTAP_SERVICES = "tapServices";
	public static final String EXTTAP_STCS_COLUMN = "STCSColumn";
	public static final String EXTTAP_RA_COLUMN = "raColumn";
	public static final String EXTTAP_DEC_COLUMN = "decColumn";
	public static final String EXTTAP_INTERSECT_COLUMN = "intersectColumn";
	public static final String EXTTAP_INTERSECT_COLUMN_OLD_API = "IntersectColumn";
	public static final String EXTTAP_BULK_DOWNLOAD_URL = "bulkDownloadUrl";
	public static final String EXTTAP_BULK_DOWNLOAD_ID_COLUMN = "bulkDownloadIdColumn";
	public static final String EXTTAP_NAME_COLUMN = "name";
	public static final String EXTTAP_TAP_URL_COLUMN = "tapUrl";
	public static final String EXTTAP_ADQL_COLUMN = "adql";
	public static final String EXTTAP_COLOR_COLUMN = "color";
	public static final String EXTTAP_MSG_UNKNOWN = "Unknown TAP Service: ";
	public static final String EXTTAP_MSG_MISING = "Missing option: ";
	
	public static final String EVENT_SHAPE_SELECTION = "shape_selected";
	public static final String EVENT_SHAPE_AREA_SELECTION = "area_shape_selection";
	public static final String EVENT_SEARC_AREA = "search_area_used";
	public static final String EVENT_VIEW_CHANGED = "view_changed";
	public static final String EVENT_PANEL_OPENED = "result_panel_opened";
	public static final String EVENT_PANEL_CLOSED = "result_panel_closed";
	public static final String EVENT_TREEMAP_MISSION_CLICKED = "treemap_mission_clicked";
	public static final String EVENT_BUTTON_CLICKED = "button_clicked";

	public static final String EVENT_REFRESH_CLICKED = "refresh_clicked";

	public static final String FOOTPRINT_STCS = "stcs";

	public static final String CENTER_RA_DEG = "ra_deg";
	public static final String CENTER_DEC_DEG = "dec_deg";
	
	
	public static final String RA = "ra";
	public static final String DEC = "dec";
	public static final String RADIUS = "radius";
	public static final String FOV = "fov";
	public static final String FOVRA = "fovRa";
	public static final String FOVDEC = "fovDec";
	
	public static final String SELECT_MODE_BOX = "BOX";
	public static final String SELECT_MODE_CIRCLE = "CIRCLE";
	public static final String SELECT_MODE_POLY = "POLYGON";

	public static final String SEARCH_AREA_TYPE = "type";
	public static final String SEARCH_AREA_CIRCLE = "circle";
	public static final String SEARCH_AREA_POLY = "polygon";
	public static final String SEARCH_AREA_COORDINATES = "coordinates";

	
	public static final String ERROR_AVAILABLE = "available";
	public static final String ERROR_MISSING_EVENT = "Not a valid event: ";

	public static final String EXTRAS = "extras";
	public static final String ERROR = "error";
	public static final String EVENT = "event";
	public static final String SUCCESS = "success";
	public static final String ORIGIN = "origin";
	public static final String VALUES = "values";
	public static final String VALUE = "value";
	public static final String MESSAGE = "message";
	public static final String INITIALISED = "initialised";
	public static final String MSGID = "msgId";
	public static final String DATA = "data";
	public static final String RESULT = "result";
	public static final String ACTION = "action";
	public static final String WAVELENGTH = "wavelength";
	public static final String WAVELENGTHS = "wavelengths";

	public static final String JUPYTER_ACTION_DOWNLOAD = "esasky_jupyter_download";
	

}
