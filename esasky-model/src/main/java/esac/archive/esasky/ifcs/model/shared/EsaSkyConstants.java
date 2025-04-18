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

package esac.archive.esasky.ifcs.model.shared;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;

import esac.archive.absi.modules.cl.aladinlite.widget.client.model.ColorPalette;
import esac.archive.esasky.ifcs.model.client.HiPS.HiPSImageFormat;
import esac.archive.esasky.ifcs.model.client.HipsWavelength;

/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class EsaSkyConstants {


    /*************************************************************************/
    /** GENERAL CONSTANTS **/
    /*************************************************************************/

    /**
     * Replace pattern.
     */
    public static final String REPLACE_PATTERN = "@@";

    /**
     * App name.
     */
    public static final String APP_NAME = "ESASky";

    /**
     * Server Context.
     */
    public static final String PROP_SERVER_CONTEXT = "server.context";
    /**
     * Tap Context.
     */
    public static final String PROP_TAP_CONTEXT = "tap.context";

    /**
     * Not target found.
     */
    public static final String NOT_TARGET_FOUND = "No astronomical object found";

    /**
     * Default FoV.
     */
    public static final Double DEFAULT_FOV = 0.1; // 6 arcmin

    /**
     * Default FoV.
     */
    public static final Double MIN_ALLOWED_DEFAULT_FOV = 0.09; // < 6 arcmin

    /**
     * JSON format.
     */
    public static final String JSON = "json";

    /**
     * VOTABLE format.
     */
    public static final String VOTABLE = "votable";

    public static final String INTERNATIONALIZATION_LANGCODE_URL_PARAM = "lang";


    /*************************************************************************/
    /** DEFAULT INIT VALUES **/
    /*************************************************************************/

    /**
     * AladinLite ID name
     */
    public static final String ALADIN_DIV_NAME = "aladin-container";

    /**
     * Default Hips map.
     */
    public static final String ALADIN_DEFAULT_HIPS_MAP = "DSS2 color";
    /**
     * Default survey Id.
     */
    public static final String ALADIN_DEFAULT_SURVEY_ID = "DSS2 Color";
    /**
     * Default survey name.
     */
    public static final String ALADIN_DEFAULT_SURVEY_NAME = "DSS2 color";

    /**
     * Default survey name.
     */
    public static final String DEFAULT_WAVELENGTH = HipsWavelength.OPTICAL;

    /**
     * Default survey URL.
     */
    public static final String ALADIN_DEFAULT_SURVEY_URL = "//cdn.skies.esac.esa.int/DSSColor";
    /**
     * Default HiPS cooframe.
     */
    public static final String ALADIN_HiPS_DEFAULT_COO_FRAME = "equatorial";
    /**
     * Default Hips Image format.
     */
    public static final HiPSImageFormat ALADIN_DEFAULT_IMG_FORMAT = HiPSImageFormat.jpg;
    /**
     * Default color palette.
     */
    public static final ColorPalette ALADIN_DEFAULT_COLOR_MAP = ColorPalette.NATIVE;
    /**
     * Default norder.
     */
    public static final Integer ALADIN_DEFAULT_NORDER = 9;
    /**
     * Default target.
     */
    public static final String ALADIN_DEFAULT_TARGET = "202.469575 +47.19525833333333";
    /**
     * Default FoV value.
     */
    public static final double initFoV = 1.0;

    /*************************************************************************/
    /** Patterns used to validate RA/DEC format **/
    /*************************************************************************/

    private static final String BLANK = "\\s*";

    private static final String SIGN = "([+-])?";

    private static final String BEGIN = "^" + BLANK;

    private static final String END = BLANK + "$";

    // Atomic elements
    private static final String HOUR = "([0-2]?[0-9])";

    private static final String MIN = "([0-5]?[0-9])";

    private static final String SEC = "([0-5]?[0-9]\\.?\\d*)";

    private static final String SEP2 = "[\\s\\:\\,]+";

    private static final String DEG = "(\\d{1,2})";

    private static final String HOUR_UNIT = "(?:([0-2]?[0-9])h)?";

    private static final String MIN_UNIT = "(?:([0-5]?[0-9])m)?";

    private static final String SEC_UNIT = "(?:([0-5]?[0-9]\\.?\\d*)s)?";

    private static final String DEG_UNIT = "(?:(\\d{1,2})d)?";

    private static final String AMIN_UNIT = "(?:([0-5]?[0-9])[m\\'])?";

    private static final String ASEC_UNIT = "(?:([0-5]?[0-9]\\.?\\d*)(?:s|\\'{2}))?";

    /**
     * RA format: 1, 2 or 3 numbers without units or any with them.
     */
    public static final String RA_DECIMAL_PATTERN = BEGIN + "([\\d\\.]+)([dh])?" + END;

    public static final String RA_TWO_NUMBERS_PATTERN = BEGIN + HOUR + SEP2 + MIN + END;

    public static final String RA_THREE_NUMBERS_PATTERN = BEGIN + HOUR + SEP2 + MIN + SEP2 + SEC
            + END;

    public static final String RA_UNITS_PATTERN = BEGIN + HOUR_UNIT + BLANK + MIN_UNIT + BLANK
            + SEC_UNIT + END;

    /**
     * DEC format: 1, 2 or 3 numbers without units or any with them.
     */
    public static final String DEC_DECIMAL_PATTERN = BEGIN + SIGN + BLANK + "([\\d\\.]+)d?" + END;

    public static final String DEC_TWO_NUMBERS_PATTERN = BEGIN + SIGN + BLANK + DEG + SEP2 + MIN
            + END;

    public static final String DEC_THREE_NUMBERS_PATTERN = BEGIN + SIGN + BLANK + DEG + SEP2 + MIN
            + SEP2 + SEC + END;

    public static final String DEC_UNITS_PATTERN = BEGIN + SIGN + DEG_UNIT + BLANK + AMIN_UNIT
            + BLANK + ASEC_UNIT + END;
    /**
     * String starts with character.
     */
    public static final String TARGET_NAME = BEGIN + "[a-zA-Z,-]+";

    /*************************************************************************/
    /** View Constants **/
    /*************************************************************************/

    /**
     * Buttons Standard width.
     */
    public static final String BUTTONS_STANDARD_WIDTH = "145px";

    /**
     * Type of button.
     */
    public static final String SAVE_BUTTON_TYPE = "save";
    /**
     * Type of button.
     */
    public static final String SEND_BUTTON_TYPE = "send";
    /**
     * Max catalogue search size.
     */
    public static final Integer MAX_CATALOG_SEARCH_SIZE = 50000;
    /**
     * FoV multiplier for multi-target player.
     */
    public static final double MULTITARGET_FOV = 2.0;

    /**
     * Default HiPS cooframe.
     */
    public static final String SIMBAD_J2000_COO_FRAME = "ICRS";
    /**
     * Default HiPS cooframe.
     */
    public static final String SIMBAD_GALACTIC_COO_FRAME = "Gal";

    public static final String SIMBAD_OBJECT_NOT_IDENTIFIED = "No astronomical object found";

    /**
     * USER ERROR MESSAGES
     */
    public static final String ERROR_MSG_TOO_MANY_USERS = "The server is taking too long to answer, sorry. "
            + "Please, either submit again your request or refine your search. "
            + "It could be that in this moment there are many users connected.";

    public static final String ERROR_MSG_UPLOAD_FILE = "The server is taking too long to answer, sorry. "
            + "Please, either try to submit again your file. "
            + "It could be that in this moment there are many users connected.";

    public static final String ERROR_MSG_SIMBAD = "Sorry for the inconvinience, "
            + "but it seems that the connection between our servers and SIMABD "
            + "resolver service has some problems. Please retry your search.";

    public static final String ERROR_MSG_SAMP = "Something went wrong in the comunication between SAMP applications. Please retry your search and if the problem persists, tell us using either the userecho page or the helpdesk.";


    public static final String DATALINK_URL_PARAM = "url";
    public static final String IMAGELOADER_URL_PARAM = "url";
    public static final String HST_IMAGE_ID_PARAM = "id";
    public static final String HST_IMAGE_ACTION_PARAM = "action";
    public static final String HST_MISSION = "HST";
    public static final String JWST_MISSION = "JWST";

    public static final String EUCLID_MISSION = "EUCLID";

    /*************************************************************************/
    /** INTERNATIONALIZATION VALUES **/
    /*************************************************************************/
    public static final String DEFAULT_LANGCODE = "en";
    private static final List<SimpleEntry<String, String>> AVAILABLE_LANGUAGES = new LinkedList<>(
            Arrays.asList(
                    new SimpleEntry<>("en", "En"),
                    new SimpleEntry<>("es", "Es"),
                    new SimpleEntry<>("zh", "中文")
            )
    );

    private static final List<SimpleEntry<String, String>> LIMITED_AVAILABLE_LANGUAGES = new LinkedList<>(
            Arrays.asList(new SimpleEntry<>("fr", "Fr"))
    );

    /*************************************************************************/
    /** COMMUNICATION CONSTANTS **/
    /*************************************************************************/

    public static final String EXT_TAP_ACTION_FLAG = "ACTION";
    public static final String EXT_TAP_TARGET_FLAG = "TAP_TARGET";
    public static final String EXT_TAP_ADQL_FLAG = "ADQL";
    public static final String EXT_TAP_ACTION_DESCRIPTORS = "DESCRIPTORS";
    public static final String EXT_TAP_ACTION_RESET = "RESET";
    public static final String EXT_TAP_ACTION_REQUEST = "REQUEST";
    public static final String EXT_TAP_CAPABILITIES_REQUEST = "CAPABILITIES";
//    public static final String EXT_TAP_ACTION_UPLOAD = "UPLOAD";

    public static final String EXT_TAP_URL_FLAG = "TAP_URL";
    public static final String EXT_TAP_RESPONSE_FORMAT = "RESPONSE_FORMAT";
    public static final String EXT_TAP_MAX_REC_FLAG = "MAX_REC";
    public static final String REGISTRY_TAP_TARGET = "REGISTRY_TAP_TARGET";
    public static final String REGISTRY_TAP_IVOID = "REGISTRY_IVOID";

    /*************************************************************************/
    /** IVOA OBSCORE **/
    /*************************************************************************/

    public static final String OBSCORE_COLLECTION = "obs_collection";
    public static final String OBSCORE_FACILITY = "facility_name";
    public static final String OBSCORE_DATAPRODUCT = "dataproduct_type";
    public static final String OBSCORE_FOV = "s_fov";
    public static final String OBSCORE_SREGION = "s_region";
    public static final String CATALOGUE = "catalogue";
    public static final String TABLE_NAME = "table_name";
    public static final String HEASARC_TABLE = "table_name";


    /*************************************************************************/
    /** TREE MAP **/
    /*************************************************************************/

    public static final String TREEMAP_TYPE_MISSION = "mission";
    public static final String TREEMAP_TYPE_SERVICE = "service";
    public static final String TREEMAP_TYPE_SUBCOLLECTION = "collection";
    public static final String TREEMAP_TYPE_DATAPRODUCT = "dataproduct";

    public static final int TREEMAP_LEVEL_SERVICE = 0;
    public static final int TREEMAP_LEVEL_1 = 1;
    public static final int TREEMAP_LEVEL_2 = 2;
    public static final int TREEMAP_LEVEL_3 = 3;
    public static final int DEFAULT_TREEMAP_HEIGHT_DESKTOP = 400;
    public static final int DEFAULT_TREEMAP_HEIGHT_TABLET = 800;
    public static final int DEFAULT_TREEMAP_WIDTH_TABLET = 800;
    public static final int DEFAULT_TREEMAP_WIDTH_DESKTOP = 500;
    public static final int DEFAULT_TREEMAP_HEIGHT_MOBILE = 1000;
    public static final int DEFAULT_TREEMAP_WIDTH_MOBILE = 1000;

    /*************************************************************************/
    /** MOC **/
    /*************************************************************************/

    public static final String HEALPIX_IPIX = "healpix_index";
    public static final String HEALPIX_ORDER = "healpix_order";
    public static final String HEALPIX_COUNT = "healpix_count";

    public static final String Q3C_IPIX = "moc_ipix";
    public static final String Q3C_ORDER = "moc_order";
    public static final String Q3C_COUNT = "moc_count";


    /*************************************************************************/
    /** MODULES **/
    /*************************************************************************/
    public static class ModuleConstants {
        public static final String MODULE_SCIENTIFIC_BUTTON = "scientific_toggle_button";
        public static final String MODULE_LANGUAGE = "language_button";
        public static final String MODULE_COOR_GRID = "coordinate_grid_button";
        public static final String MODULE_SCREENSHOT = "screenshot_button";
        public static final String MODULE_SHARE = "share_button";
        public static final String MODULE_HELP = "help_button";
        public static final String MODULE_DROPDOWN = "dropdown_menu";
        public static final String MODULE_SKIESMENU = "skies_menu";
        public static final String MODULE_FEEDBACK = "feedback_button";
        public static final String MODULE_LOGIN = "login_button";
        public static final String MODULE_OBS = "observations_button";
        public static final String MODULE_CAT = "catalogues_button";
        public static final String MODULE_SPE = "spectra_button";
        public static final String MODULE_EXTTAP = "exttap_button";
        public static final String MODULE_GW = "gw_button";
        public static final String MODULE_OUTREACH_IMAGE = "outreach_button";
        public static final String MODULE_OUTREACH_JWST = "outreach_jwst_button";
        public static final String MODULE_SSO = "sso_button";
        public static final String MODULE_PUBLICATIONS = "publications_button";
        public static final String MODULE_TARGETLIST = "target_list_button";
        public static final String MODULE_TARGETLIST_UPLOAD = "target_list_upload";
        public static final String MODULE_JWST_PLANNING = "jwst_planning_button";
        public static final String MODULE_DICE = "dice_button";
        public static final String MODULE_SCIENCE_MODE = "science_mode";
        public static final String MODULE_SESSION = "session";
        public static final String MODULE_EVA_MENU = "eva_menu";
        public static final String MODULE_EVA = "eva";
        public static final String MODULE_SEARCH_TOOL = "search_tool";
        public static final String MODULE_SEARCH_IN_MENU = "search_in";
        public static final String MODULE_KIOSK_BUTTONS = "kiosk_buttons";
        public static final String MODULE_WELCOME_DIALOG = "welcome_dialog";

        // Create a Map to store the constants
        private static final Map<String, String> MODULE_MAP = new HashMap<>();

        // Static block to initialize the map
        static {
            MODULE_MAP.put("MODULE_SCIENTIFIC_BUTTON", MODULE_SCIENTIFIC_BUTTON);
            MODULE_MAP.put("MODULE_LANGUAGE", MODULE_LANGUAGE);
            MODULE_MAP.put("MODULE_COOR_GRID", MODULE_COOR_GRID);
            MODULE_MAP.put("MODULE_SCREENSHOT", MODULE_SCREENSHOT);
            MODULE_MAP.put("MODULE_SHARE", MODULE_SHARE);
            MODULE_MAP.put("MODULE_HELP", MODULE_HELP);
            MODULE_MAP.put("MODULE_DROPDOWN", MODULE_DROPDOWN);
            MODULE_MAP.put("MODULE_SKIESMENU", MODULE_SKIESMENU);
            MODULE_MAP.put("MODULE_FEEDBACK", MODULE_FEEDBACK);
            MODULE_MAP.put("MODULE_LOGIN", MODULE_LOGIN);
            MODULE_MAP.put("MODULE_OBS", MODULE_OBS);
            MODULE_MAP.put("MODULE_CAT", MODULE_CAT);
            MODULE_MAP.put("MODULE_SPE", MODULE_SPE);
            MODULE_MAP.put("MODULE_EXTTAP", MODULE_EXTTAP);
            MODULE_MAP.put("MODULE_GW", MODULE_GW);
            MODULE_MAP.put("MODULE_OUTREACH_IMAGE", MODULE_OUTREACH_IMAGE);
            MODULE_MAP.put("MODULE_OUTREACH_JWST", MODULE_OUTREACH_JWST);
            MODULE_MAP.put("MODULE_SSO", MODULE_SSO);
            MODULE_MAP.put("MODULE_PUBLICATIONS", MODULE_PUBLICATIONS);
            MODULE_MAP.put("MODULE_TARGETLIST", MODULE_TARGETLIST);
            MODULE_MAP.put("MODULE_TARGETLIST_UPLOAD", MODULE_TARGETLIST_UPLOAD);
            MODULE_MAP.put("MODULE_JWST_PLANNING", MODULE_JWST_PLANNING);
            MODULE_MAP.put("MODULE_DICE", MODULE_DICE);
            MODULE_MAP.put("MODULE_SCIENCE_MODE", MODULE_SCIENCE_MODE);
            MODULE_MAP.put("MODULE_SESSION", MODULE_SESSION);
            MODULE_MAP.put("MODULE_EVA_MENU", MODULE_EVA_MENU);
            MODULE_MAP.put("MODULE_EVA", MODULE_EVA);
            MODULE_MAP.put("MODULE_SEARCH_TOOL", MODULE_SEARCH_TOOL);
            MODULE_MAP.put("MODULE_SEARCH_IN_MENU", MODULE_SEARCH_IN_MENU);
            MODULE_MAP.put("MODULE_KIOSK_BUTTONS", MODULE_KIOSK_BUTTONS);
            MODULE_MAP.put("MODULE_WELCOME_DIALOG", MODULE_WELCOME_DIALOG);
        }

        // Method to get the module value based on the key
        public static String getModuleValue(String key) {
            return MODULE_MAP.get(key);
        }


    }


    /*************************************************************************/
    /** JWST Instruments **/
    /*************************************************************************/

    public enum JWSTInstrument {
        FGS1("FGS", "FGS1_FULL"),
        FGS2("FGS", "FGS2_FULL"),
        NIRISS_CEN("NIRISS", "NIS_CEN"),
        NIRSPEC_MSA("NIRSpec", "NRS_FULL_MSA"),
        NIRSPEC_MSA1("NIRSpec", "NRS_FULL_MSA1"),
        NIRSPEC_MSA2("NIRSpec", "NRS_FULL_MSA2"),
        NIRSPEC_MSA3("NIRSpec", "NRS_FULL_MSA3"),
        NIRSPEC_MSA4("NIRSpec", "NRS_FULL_MSA4"),
        NIRSPEC_IFU("NIRSpec", "NRS_FULL_IFU"),
        NIRSPEC_SLIT1("NIRSpec", "NRS_S200A1_SLIT"),
        NIRSPEC_SLIT2("NIRSpec", "NRS_S200A2_SLIT"),
        NIRSPEC_SLIT3("NIRSpec", "NRS_S200B1_SLIT"),
        NIRSPEC_SLIT4("NIRSpec", "NRS_S400A1_SLIT"),
        NIRSPEC_SLIT5("NIRSpec", "NRS_S1600A1_SLIT"),
        NIRCAFULL("NIRCam", "NRCALL_FULL"),
        NIRCA1("NIRCam", "NRCA1_FULL"),
        NIRCA2("NIRCam", "NRCA2_FULL"),
        NIRCA3("NIRCam", "NRCA3_FULL"),
        NIRCA4("NIRCam", "NRCA4_FULL"),
        NIRCA5("NIRCam", "NRCA5_FULL_OSS"),
        NIRCB1("NIRCam", "NRCB1_FULL"),
        NIRCB2("NIRCam", "NRCB2_FULL"),
        NIRCB3("NIRCam", "NRCB3_FULL"),
        NIRCB4("NIRCam", "NRCB4_FULL"),
        NIRCB5("NIRCam", "NIRCB5_FULL"),
        NIRCA2_MASK210R("NIRCam", "NRCA2_MASK210R"),
        NIRCA5_MASK335R("NIRCam", "NRCA5_MASK335R"),
        NIRCA5_MASK430R("NIRCam", "NRCA5_MASK430R"),
        NIRCA4_MASKSWB("NIRCam", "NRCA4_MASKSWB"),
        NIRCA5_MASKLWB("NIRCam", "NRCA5_MASKLWB"),
        NIRCB1_MASK210R("NIRCam", "NRCB1_MASK210R"),
        NIRCB5_MASK335R("NIRCam", "NRCB5_MASK335R"),
        NIRCB5_MASK430R("NIRCam", "NRCB5_MASK430R"),
        NIRCB3_MASKSWB("NIRCam", "NRCB3_MASKSWB"),
        NIRCB5_MASKLWB("NIRCam", "NRCB5_MASKLWB"),
        MIRIM_FULL("MIRI", "MIRIM_FULL"),
        MIRIM_MASK1065("MIRI", "MIRIM_MASK1065"),
        MIRIM_ILLUM("MIRI", "MIRIM_ILLUM"),
        MIRIM_FP1MIMF("MIRI", "MIRIM_FP1MIMF"),
        MIRIM_MASK1140("MIRI", "MIRIM_MASK1140"),
        MIRIM_MASK1550("MIRI", "MIRIM_MASK1550"),
        MIRIM_MASKLYOT("MIRI", "MIRIM_MASKLYOT"),
        MIRIM_CHANNEL1A("MIRI", "MIRIFU_CHANNEL1A");

        private String instrName;
        private String aperName;

        private JWSTInstrument(String instrName, String aperName) {
            this.instrName = instrName;
            this.aperName = aperName;
        }

        @Override
        public String toString() {
            return instrName;
        }

        public String getAperName() {
            return aperName;
        }


    }



    public enum XMMInstrument {
        XMM_EPIC_PN("EPIC-pn", "CCD_FULL");

        private String instrName;
        private String aperName;

        private XMMInstrument(String instrName, String aperName) {
            this.instrName = instrName;
            this.aperName = aperName;
        }

        @Override
        public String toString() {
            return instrName;
        }

        public String getAperName() {
            return aperName;
        }


    }

    /*************************************************************************/
    /** DEFAULT INIT VALUES **/
    /*************************************************************************/
    public enum ReturnType {
        JSON("json", "application/json"), VOTABLE("vot", "application/x-votable+xml"), CSV("csv", "text/csv"), ASCII("ascii", ""), JUPYTER("ipynb", "application/x-ipynb+json");

        private String name;
        private String mimeType;

        private ReturnType(String name, String mimeType) {
            this.name = name;
            this.mimeType = mimeType;
        }

        @Override
        public String toString() {
            return name;
        }

        public String getMimeType() {
            return mimeType;
        }


    }

    /**
     * HTTP GET actions enum.
     *
     * @author mhsarmiento
     */
    public enum HttpServlet {
        DATA_GET_SERVLET("/servlet/data-action?"), METADATA_GET_SERVLET("/servlet/metadata-action?"), METADATA_COUNT_SERVLET(
                "/servlet/metadata-count-action?"), SUGGESTION_SERVLET(
                "/servlet/suggestion-action?"), RSS_GET_SERVLET("/servlet/rss-action?"), TARGET_NAME_RESOLVER(
                "/servlet/targetresolver-action?"), FITS_SPECTRA_SERVLET(
                "/servlet/fits-spectra-action?"), FITS_METADATA_SERVLET(
                "/servlet/fits-metadata-action?"), FITS_IMAGE_SERVLET("/servlet/fits-image-action?"), QUERY_RESULTS_DOWNLOAD_SERVLET(
                "/servlet/query-results-download-action?"), TINY_URL_SERVLET(
                "/servlet/tiny-url-action?"), VOTABLE_BUFFER_SERVLET("/servlet/votable-buffer?");

        /**
         * enum attrubite.
         */
        private String value;

        /**
         * classConstrutor.
         *
         * @param inputValue Input String.
         */
        HttpServlet(final String inputValue) {
            this.value = inputValue;
        }

        @Override
        public String toString() {
            return value;
        }

        /**
         * getValue().
         *
         * @return String
         */
        public String getValue() {
            return value;
        }

    }

    /**
     * Prevents Utility class calls.
     */
    protected EsaSkyConstants() {
        // prevents calls from subclass
        throw new UnsupportedOperationException();
    }

    public static List<SimpleEntry<String, String>> getAvailableLanguages(boolean includeLimited) {
        if (includeLimited) {
            List<SimpleEntry<String, String>> result = new ArrayList<>(AVAILABLE_LANGUAGES.size() + LIMITED_AVAILABLE_LANGUAGES.size());
            result.addAll(AVAILABLE_LANGUAGES);
            result.addAll(LIMITED_AVAILABLE_LANGUAGES);
            return result;
        } else {
            return AVAILABLE_LANGUAGES;
        }
    }
}
