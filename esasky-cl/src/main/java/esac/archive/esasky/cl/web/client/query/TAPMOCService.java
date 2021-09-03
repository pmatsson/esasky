package esac.archive.esasky.cl.web.client.query;


import com.allen_sauer.gwt.log.client.Log;

import esac.archive.esasky.cl.web.client.repository.MocRepository;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;
import esac.archive.esasky.cl.web.client.utility.RangeTree;
import esac.archive.esasky.cl.web.client.utility.RangeTree.Interval;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.coordinatesutils.Coordinate;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;

public class TAPMOCService {

    private static TAPMOCService instance = null;

    private TAPMOCService() {
    }

    public static TAPMOCService getInstance() {
        if (instance == null) {
            instance = new TAPMOCService();
        }
        return instance;
    }

    public String getPrecomputedMOCAdql(IDescriptor descriptor) {
    	
    	Coordinate pos = CoordinateUtils.getCenterCoordinateInJ2000().getCoordinate();
    	String adql = "SELECT esasky_q3c_moc_query(\'" + descriptor.getTapTable().replace("public", "moc_schema") 
    			+ "\', " + getGeometricConstraint() + ", \'" + Double.toString(pos.getRa()) + "\', \'" + Double.toString(pos.getDec())
    			+ "\')  as moc from dual";
    	return adql;
    }
    
    public String getFilteredCatalogueMOCAdql(IDescriptor descriptor, String filter) {
    	
    	int order = MocRepository.getTargetOrderFromFoV();
    	Coordinate pos = CoordinateUtils.getCenterCoordinateInJ2000().getCoordinate();
    	
    	filter = filter.replaceAll("'","''");
    	
    	String adql = "SELECT esasky_q3c_filtered_catalogue_moc_query(\'" + descriptor.getTapTable()
		    	+ "\', " + getGeometricConstraint() + ", \'" + filter + "\',\'" + Double.toString(pos.getRa()) + "\', \'" + Double.toString(pos.getDec())
		    	+ "\',\'" + Integer.toString(order) + "\') as moc from dual";
    	
		return adql;
    }
    

    public String getFilteredCatalogueMOCAdql(IDescriptor descriptor, GeneralJavaScriptObject visibleIpixels, String filter) {
    	
    	int targetOrder = MocRepository.getTargetOrderFromFoV();
    	
    	String whereADQL = getWhereQueryFromPixels(descriptor, visibleIpixels, filter);
    	
    	String adql = "SELECT " + Integer.toString(targetOrder) + " as moc_order,"
			+"esasky_q3c_bitshift_right(q3c_ang2ipix(" + descriptor.getTapRaColumn() + "," + descriptor.getTapDecColumn() +"), "
			+ Integer.toString(60 - 2 * targetOrder) + ") as moc_ipix,"
    		+ " count(*) as moc_count FROM " + descriptor.getTapTable()
    		+ whereADQL;
    	
    	adql += " GROUP BY moc_ipix";
    	
    	return adql;
    }
				
	public String getFilteredObservationMOCAdql(IDescriptor descriptor, String filter) {
		
		Coordinate pos = CoordinateUtils.getCenterCoordinateInJ2000().getCoordinate();
		filter = filter.replaceAll("'","''");
		
		String adql = "SELECT esasky_q3c_filtered_catalogue_moc_query(\'" + descriptor.getTapTable()
		+ "\', " + getGeometricConstraint() + ", \'" + filter + "\',\'" + Double.toString(pos.getRa()) + "\', \'" + Double.toString(pos.getDec())
		+ "\',\'8\') as moc from dual";
		
		return adql;
    }
    
    private String getGeometricConstraint() {
    	final String debugPrefix = "[TAPMOCService.getGeometricConstraint]";
        String shape = null;
        double fovDeg = AladinLiteWrapper.getAladinLite().getFovDeg();
        if (AladinLiteWrapper.isCornersInsideHips()) {
            if (fovDeg < 1) {
                Log.debug(debugPrefix + " FoV < 1d");
                shape = "\'(" +  AladinLiteWrapper.getAladinLite().getFovCorners(1).toString() + ")\'";

            } else {
                shape =  "\'(" +  AladinLiteWrapper.getAladinLite().getFovCorners(2).toString() + ")\'";
            }
        } else {

            shape = "\'\'";
        }
        return shape;
    }
    
    public String getWhereQueryFromPixels(IDescriptor descriptor, GeneralJavaScriptObject pixels, String filter) {
    	String[] orderArray = pixels.getProperties().split(",");
    	RangeTree rangeTree = new RangeTree();
    	
    	for(String orderString : orderArray) {
    		int pixelOrder = Integer.parseInt(orderString);
        	String[] pixelArray = GeneralJavaScriptObject.convertToString(pixels.getProperty(orderString)
        			.invokeFunction("toString")).split(",");
        	for(String pixelString : pixelArray){
        		long pixel = Long.parseLong(pixelString);
        		long start = pixel << (60 - 2 * pixelOrder);
        		long end = (pixel + 1) << (60 - 2 * pixelOrder);
        		rangeTree.add(start, end);
        	}
    	}
    	
    	String whereADQL = " WHERE (";
    	String raColumn = descriptor.getTapRaColumn();
    	String decColumn = descriptor.getTapDecColumn();
    	
    	for(Interval i : rangeTree.getTree()) {
    		whereADQL += " q3c_ang2ipix(" + raColumn+ "," + decColumn + ") BETWEEN "+ Long.toString(i.getStart())
				+ " AND " + Long.toString(i.getEnd());
    		whereADQL += " OR ";
    	}
    	
    	whereADQL = whereADQL.substring(0, whereADQL.length() - 3);
    	whereADQL += ")";
    	
    	if(!"".contentEquals(filter)) {
    		whereADQL += " AND " + filter;
    		
    	}
    	
    	return whereADQL;
    }

    public String fetchMinMaxHeaders(IDescriptor descriptor, boolean global) {
    	String adql = "SELECT esasky_q3c_maxmin_query('" + descriptor.getTapTable() + "',";
    	 if (AladinLiteWrapper.isCornersInsideHips()) {
    		 adql += "'{" + AladinLiteWrapper.getAladinLite().getFovCorners(2).toString()+ "}','',''";
    	 }else {
    		 Coordinate coor =  CoordinateUtils.getCenterCoordinateInJ2000().getCoordinate();
    		 adql += "'','" + Double.toString(coor.getRa()) + "','" + Double.toString(coor.getDec()) + "'";
    	 }
    	 
    	 adql += ", '" +  global + "') from public.function_dummy";
    	return adql;
    }
    
}