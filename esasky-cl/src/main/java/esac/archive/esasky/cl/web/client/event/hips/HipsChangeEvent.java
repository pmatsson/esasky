package esac.archive.esasky.cl.web.client.event.hips;

import com.google.gwt.event.shared.GwtEvent;

// import esac.archive.esasky.ifcs.model.client.ColorPalette;
import esac.archive.esasky.ifcs.model.client.HiPS;
import esac.archive.esasky.ifcs.model.shared.ColorPalette;

/**
 * Event to record closing results event tab sent by Tab components.
 */
public class HipsChangeEvent extends GwtEvent<HipsChangeEventHandler> {

    /** Event type. */
    public final static Type<HipsChangeEventHandler> TYPE = new Type<HipsChangeEventHandler>();

    private HiPS hips;
    private ColorPalette colorPalette;
    private boolean isBaseImage;
    private double opacity;
    private String skyRowId;

    public HipsChangeEvent(final String skyRowId, final HiPS inputHips, final ColorPalette colorPalette, final boolean isBaseImage, final double opacity) {
        this.skyRowId = skyRowId;
        this.hips = inputHips;
        this.colorPalette = colorPalette;
        this.isBaseImage = isBaseImage;
        this.opacity = opacity;
    }

    public final HiPS getHiPS() {
        return hips;
    }
    
    public final boolean isBaseImage() {
    	return isBaseImage;
    }

    public final ColorPalette getColorPalette() {
        return colorPalette;
    }
    
    public final double getOpacity() {
    	return opacity;
    }

    @Override
    public final Type<HipsChangeEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final HipsChangeEventHandler handler) {
        handler.onChangeEvent(this);
    }

    public String getSkyRowId() {
        return skyRowId;
    }
}
