package esac.archive.esasky.cl.web.client.utility;

import esac.archive.ammi.ifcs.model.coordinatesutils.Coordinate;
import esac.archive.ammi.ifcs.model.coordinatesutils.CoordinatesConversion;
import esac.archive.ammi.ifcs.model.coordinatesutils.SkyViewPosition;

public class CoordinateUtils {

    public static SkyViewPosition getCenterCoordinateInJ2000() {
        double ra = AladinLiteWrapper.getInstance().getCenterLongitudeDeg();
        double dec = AladinLiteWrapper.getInstance().getCenterLatitudeDeg();
        double fov = AladinLiteWrapper.getInstance().getFovDeg();
        if (EsaSkyWebConstants.ALADIN_GALACTIC_COOFRAME.equalsIgnoreCase(AladinLiteWrapper
                .getInstance().getCooFrame())) {
            // convert to J2000
            Double[] ccInJ2000 = CoordinatesConversion.convertPointGalacticToJ2000(ra, dec);
            ra = ccInJ2000[0];
            dec = ccInJ2000[1];
        }
        return new SkyViewPosition(new Coordinate(ra, dec), fov);
    }

    public static SkyViewPosition getCenterCoordinateInGalactic() {
    	double ra = AladinLiteWrapper.getInstance().getCenterLongitudeDeg();
    	double dec = AladinLiteWrapper.getInstance().getCenterLatitudeDeg();
    	double fov = AladinLiteWrapper.getInstance().getFovDeg();
    	if (!EsaSkyWebConstants.ALADIN_GALACTIC_COOFRAME.equalsIgnoreCase(AladinLiteWrapper
    			.getInstance().getCooFrame())) {
    		// convert to Galactic
    		Double[] ccInGal = CoordinatesConversion.convertPointEquatorialToGalactic(ra, dec);
    		ra = ccInGal[0];
    		dec = ccInGal[1];
    	}
    	return new SkyViewPosition(new Coordinate(ra, dec), fov);
    }
    
    public static Coordinate getCoordinateInJ2000(double ra, double dec) {
    	if (EsaSkyWebConstants.ALADIN_GALACTIC_COOFRAME.equalsIgnoreCase(AladinLiteWrapper
    			.getInstance().getCooFrame())) {
    		// convert to J2000
    		Double[] ccInJ2000 = CoordinatesConversion.convertPointGalacticToJ2000(ra, dec);
    		ra = ccInJ2000[0];
    		dec = ccInJ2000[1];
    	}
    	return new Coordinate(ra, dec);
    }
    
    public static boolean isTargetOutOfFocus(Double ra, Double dec, Double fovDeg) {
    	double aladinRaDeg = AladinLiteWrapper.getCenterRaDeg();
    	double aladinDecDeg = AladinLiteWrapper.getCenterDecDeg();
        return Math.abs(ra - aladinRaDeg) > (fovDeg / 2)
                || Math.abs(dec - aladinDecDeg) > (fovDeg / 2);
    }
    
}
