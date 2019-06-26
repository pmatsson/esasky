package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

import esac.archive.absi.modules.wcstransform.module.utility.Constants.Detectors;
import esac.archive.absi.modules.wcstransform.module.utility.Constants.Instrument;

public class DetectorChangeEvent extends GwtEvent<DetectorChangeEventHandler> {

    public static Type<DetectorChangeEventHandler> TYPE = new Type<DetectorChangeEventHandler>();

    private Instrument instrument;
    private Detectors detector;

    public DetectorChangeEvent(final Instrument instrument, final Detectors detector) {
        this.instrument = instrument;
        this.detector = detector;
    }

    public final Detectors getDetector() {
    	return this.detector;
    }
    
    public final Instrument getInstrument() {
    	return this.instrument;
    }

    @Override
    public final Type<DetectorChangeEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final DetectorChangeEventHandler handler) {
        handler.onChangeEvent(this);
    }

}
