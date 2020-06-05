package esac.archive.esasky.ifcs.model.descriptor;


/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class ObservationDescriptor extends CommonObservationDescriptor {

    @Override
    public String getIcon() {
        return "galaxy";
    }
    
    @Override
    public String getDescriptorId() {
        if(descriptorId == null || descriptorId.isEmpty()) {
            return "ASTRO_IMAGING_" + getMission();
        }
        return descriptorId;
    }
}
