package esac.archive.esasky.cl.web.client.query;

import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;

public class TAPImageListService extends AbstractTAPService {

    private static TAPImageListService instance = null;

    private TAPImageListService() {
    }

    public static TAPImageListService getInstance() {
        if (instance == null) {
            instance = new TAPImageListService();
        }
        return instance;
    }

    
    @Override
    public String getMetadataAdql(IDescriptor descriptorInput) {
    	return getMetadataAdql(descriptorInput, "");
    }
    
    @Override
    public String getMetadataAdql(IDescriptor descriptor, String filter) {
    	return "SELECT * from " + descriptor.getTapTable();
    }

	@Override
	public String getMetadataAdqlRadial(IDescriptor descriptor, SkyViewPosition conePos) {
		// TODO Auto-generated method stub
		return null;
	}
    
}