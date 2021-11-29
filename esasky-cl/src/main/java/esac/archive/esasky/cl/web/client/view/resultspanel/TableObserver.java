package esac.archive.esasky.cl.web.client.view.resultspanel;

import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;

public interface TableObserver {
	void numberOfShownRowsChanged(int numberOfShownRows);
	void onSelection(ITablePanel selectedTablePanel);
	void onUpdateStyle(ITablePanel panel);
	void onDataLoaded(int numberOfRows);
	void onRowSelected(GeneralJavaScriptObject row);
	void onRowDeselected(GeneralJavaScriptObject row);

}
