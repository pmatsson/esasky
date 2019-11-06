package esac.archive.esasky.cl.web.client.utility;

import esac.archive.esasky.ifcs.model.descriptor.ExtTapDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

public class ExtTapHelper {

	public static ExtTapDescriptor createCollectionDescriptor(ExtTapDescriptor tapService, String facilityName) {
		ExtTapDescriptor collectionDescriptor = new ExtTapDescriptor();
		collectionDescriptor.copyParentValues((ExtTapDescriptor) tapService);
		collectionDescriptor.setTreeMapType(EsaSkyConstants.TREEMAP_TYPE_SUBCOLLECTION);
		
		collectionDescriptor.setGuiShortName(facilityName);
		collectionDescriptor.setGuiLongName(collectionDescriptor.getGuiLongName() + "-" + facilityName);
		collectionDescriptor.setMission(collectionDescriptor.getMission() + "-" + facilityName);
		
		String whereADQL = collectionDescriptor.getWhereADQL();
		if(whereADQL != null) {
			whereADQL += " AND ";
		}else {
			whereADQL = "";
		}
		
		whereADQL +=  EsaSkyConstants.OBSCORE_COLLECTION + " IN (";
		for(String collectionName : tapService.getCollections().get(facilityName)) {
			whereADQL += "\'" + collectionName + "\', ";
		}
		//Remove last "," 
		whereADQL = whereADQL.substring(0, whereADQL.length() - 2);
		whereADQL += ")";
		
		collectionDescriptor.setWhereADQL(whereADQL);
		return collectionDescriptor;
	}
	
	public static ExtTapDescriptor createDataproductDescriptor(ExtTapDescriptor parent, String typeName) {
		ExtTapDescriptor typeDescriptor = new ExtTapDescriptor();
		typeDescriptor.copyParentValues(parent);
		typeDescriptor.setTreeMapType(EsaSkyConstants.TREEMAP_TYPE_DATAPRODUCT);

		typeDescriptor.setGuiShortName(typeName);
		typeDescriptor.setGuiLongName(typeDescriptor.getMission() + "-" + typeName);
		
		typeDescriptor.setMission(typeDescriptor.getMission() + "-" + typeName);
		
		String whereADQL = typeDescriptor.getWhereADQL();
		whereADQL += " AND " + EsaSkyConstants.OBSCORE_DATAPRODUCT + " = \'" + typeName + "\'";
		typeDescriptor.setWhereADQL(whereADQL);
		
		typeDescriptor.setSelectADQL("SELECT TOP " + Integer.toString(typeDescriptor.getSourceLimit()) + " *");
		
		return typeDescriptor;
	}
	
}
