package esac.archive.esasky.cl.web.client.view.ctrltoolbar;

import esac.archive.esasky.cl.web.client.repository.DescriptorRepository;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

public class OutreachJwstPanel extends OutreachImagePanel {

    public OutreachJwstPanel() {
        super(EsaSkyConstants.JWST_MISSION, GoogleAnalytics.CAT_JWSTOUTREACHIMAGES);
    }
    @Override
    protected CommonTapDescriptor getOutreachImageDescriptor() {
        return DescriptorRepository.getInstance().getFirstDescriptor(EsaSkyWebConstants.CATEGORY_IMAGES, EsaSkyConstants.JWST_MISSION);
    }
}
