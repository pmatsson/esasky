package esac.archive.esasky.cl.web.client.event.hips;

import com.google.gwt.event.shared.GwtEvent;
import esac.archive.esasky.ifcs.model.client.HiPS;

public class HipsAddedEvent extends GwtEvent<HipsAddedEventHandler> {

    public final static Type<HipsAddedEventHandler> TYPE = new Type<>();

    private HiPS hips;
    private boolean isUserHips;

    public HipsAddedEvent(final HiPS inputHips, final boolean isUserHips) {
        this.hips = inputHips;
        this.isUserHips = isUserHips;
    }

    public final HiPS getHiPS() {
        return hips;
    }
    
    public final boolean isUserHips() {
    	return isUserHips;
    }

    @Override
    public final Type<HipsAddedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final HipsAddedEventHandler handler) {
        handler.onEvent(this);
    }
}
