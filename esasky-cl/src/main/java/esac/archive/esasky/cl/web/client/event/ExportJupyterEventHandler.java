package esac.archive.esasky.cl.web.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface ExportJupyterEventHandler extends EventHandler {
    void onExportClick(ExportJupyterEvent clickEvent);
}
