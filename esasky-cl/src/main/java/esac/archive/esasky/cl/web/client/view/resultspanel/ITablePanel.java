package esac.archive.esasky.cl.web.client.view.resultspanel;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.Widget;
import esac.archive.esasky.cl.web.client.model.entities.GeneralEntityInterface;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;

import java.util.List;
import java.util.Map;

public interface ITablePanel {

	void insertData(String url);
	void insertData(GeneralJavaScriptObject data);

	IDescriptor getDescriptor();

	GeneralEntityInterface getEntity();

	GeneralJavaScriptObject[] getSelectedRows();

	GeneralJavaScriptObject[] getAllRows();

	void clearTable();
	
	String getEsaSkyUniqID();

	String getLabel();

	void selectRow(int rowId);
	void selectRow(int rowId, boolean delay);
	
	void selectRows(int[] rowIds);

	void deselectRow(int rowId);
	
	void deselectRows(int[] rowIds);

	void deselectAllRows();

	void hoverStartRow(int rowId);

	void hoverStopRow(int rowId);

	void selectTablePanel();
	
	void deselectTablePanel();

	void closeTablePanel();
	boolean hasBeenClosed();

	boolean getIsHidingTable();

	void registerObserver(TableObserver observer);

	void unregisterObserver(TableObserver observer);
	
	JSONObject exportAsJSON();
	JSONObject exportAsJSON(boolean applyFilters);
	void exportAsCsv();
	void exportAsVot();
	String getFullId();	
	
	void setEmptyTable(String emptyTableText);
	
	void showStylePanel(int x, int y);
	
	void downloadSelected(DDRequestForm ddForm);
	
	void updateData();
	void openConfigurationPanel();
	
	Widget getWidget();
	void registerClosingObserver(ClosingObserver closingObserver);
	
	void registerFilterObserver(TableFilterObserver observer);
	Map<String, String> getTapFilters();
	String getFilterString();
	void clearFilters();
	
	String getVoTableString();
	
	void setPlaceholderText(String text);

	void insertHeader(GeneralJavaScriptObject data, String mode);
	void goToCoordinateOfFirstRow();
	
	boolean isMOCMode();
	void setMOCMode(boolean input);
	void notifyObservers();
	void disableFilters();
	void enableFilters();
	
	boolean isDataProductDatalink();
    int getNumberOfShownRows();
	void filterOnFoV(String raCol, String decCol);
	
	void setMaxHeight(int height);
	void setVisibleColumns(List<String> columns);

	int getVisibleColumnsWidth();

	void showColumn(String field);
	void hideColumn(String field);

	void blockRedraw();
	void restoreRedraw();
	void redrawAndReinitializeHozVDom();
	void addTapFilter(String label, String tapFilter);
}
