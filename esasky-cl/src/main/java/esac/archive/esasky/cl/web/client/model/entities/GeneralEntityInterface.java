/*
ESASky
Copyright (C) 2025 European Space Agency

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published
by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package esac.archive.esasky.cl.web.client.model.entities;

import com.google.gwt.user.client.ui.Image;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.AladinShape;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.view.resultspanel.ITablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu.StylePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorSettings;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;

import java.util.LinkedList;

public interface GeneralEntityInterface extends IShapeDrawer {

    SkyViewPosition getSkyViewPosition();

    void setSkyViewPosition(SkyViewPosition skyViewPosition);

    String getHistoLabel();

    void setHistoLabel(String histoLabel);

    String getId();

    String getIcon();

    void setId(String id);

    TapRowList getMetadata();

    void setMetadata(TapRowList metadata);

    String getTabLabel();

    Image getTypeLogo();

    Object getTAPDataByTAPName(TapRowList tapRowList, int rowIndex, String tapName);

    Double getDoubleByTAPName(TapRowList tapRowList, int rowIndex, String tapName, Double defaultValue);

    CountStatus getCountStatus();

    CommonTapDescriptor getDescriptor();

    void clearAll();

    String getColor();

    void setPrimaryColor(String color);

    void fetchData();

    void insertExternalData(GeneralJavaScriptObject data);

    void fetchData(String adql);

    void fetchDataWithoutMOC();

    void coneSearch(SkyViewPosition conePos);

    ITablePanel createTablePanel();

    ITablePanel getTablePanel();

    void setTablePanel(ITablePanel panel);

    StylePanel createStylePanel();

    void setStylePanelVisibility();

    void select();

    boolean isSampEnabled();

    boolean isRefreshable();

    void setRefreshable(boolean isRefreshable);

    void setCustomRefreshable(boolean customRefreshable);

    boolean isCustomRefreshable();

    boolean isCustomizable();

    void onShapeSelection(AladinShape shape);

    void onShapeDeselection(AladinShape shape);

    void onShapeHover(AladinShape shape);

    void onShapeUnhover(AladinShape shape);

    String getHelpText();
    String getHelpTitle();

    void registerColorChangeObserver(ColorChangeObserver colorChangeObserver);

    void onMultipleShapesSelection(LinkedList<AladinShape> shapes);

    void onMultipleShapesDeselection(LinkedList<AladinShape> linkedList);

    TabulatorSettings getTabulatorSettings();

    String getQuery();

    void setQuery(String query);

    void registerQueryChangedObserver(QueryChangeObserver queryChangeObserver);

    void unregisterQueryChangedObserver(QueryChangeObserver queryChangeObserver);
}
