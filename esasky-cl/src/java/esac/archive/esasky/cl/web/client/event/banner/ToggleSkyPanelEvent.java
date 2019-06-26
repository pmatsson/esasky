package esac.archive.esasky.cl.web.client.event.banner;

import com.google.gwt.event.shared.GwtEvent;

public class ToggleSkyPanelEvent extends GwtEvent<ToggleSkyPanelEventHandler> {

    public static Type<ToggleSkyPanelEventHandler> TYPE = new Type<ToggleSkyPanelEventHandler>();

    public ToggleSkyPanelEvent() {
    }

    @Override
    public final Type<ToggleSkyPanelEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final ToggleSkyPanelEventHandler handler) {
        handler.onEvent(this);
    }

}
