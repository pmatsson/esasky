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

package esac.archive.esasky.cl.web.client.view.ctrltoolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.callback.ICommand;
import esac.archive.esasky.cl.web.client.event.ImageListSelectedEvent;
import esac.archive.esasky.cl.web.client.event.OpenSeaDragonActiveEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.Size;
import esac.archive.esasky.cl.web.client.model.entities.ImageListEntity;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository;
import esac.archive.esasky.cl.web.client.repository.EntityRepository;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.ESASkySlider;
import esac.archive.esasky.cl.web.client.view.common.EsaSkySwitch;
import esac.archive.esasky.cl.web.client.view.common.MovableResizablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.ITablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.TableObserver;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;

import java.util.Objects;

public abstract class OutreachImagePanel extends MovableResizablePanel<OutreachImagePanel> {
    private static boolean startupMinimized = false;
    private CommonTapDescriptor outreachImageDescriptor;
    private ImageListEntity imageEntity;
    private boolean isHidingFootprints = false;
    private static String outreachImageIdToBeOpened;
    private static String missionToBeOpened;
    private static boolean defaultHideFootprints = true;
    private static String outreachImageNameToBeOpened;
    private FlowPanel opacityPanel;
    private EsaSkySwitch hideFootprintsSwitch;
    private final FlowPanel mainContainer = new FlowPanel();
    private final FlowPanel tableContainer = new FlowPanel();
    private PopupHeader<OutreachImagePanel> header;
    private final String mission;
    private final Resources resources;
    private CssResource style;

    public static interface Resources extends ClientBundle {
        @Source("outreachImagePanel.css")
        @CssResource.NotStrict
        CssResource style();
    }

    public OutreachImagePanel(String mission, String googleEventCategory) {
        super(googleEventCategory, false);
        this.resources = GWT.create(Resources.class);
        this.style = this.resources.style();
        this.style.ensureInjected();

        this.mission = mission;
        initView();
        setMaxSize();

        CommonEventBus.getEventBus().addHandler(OpenSeaDragonActiveEvent.TYPE, event -> opacityPanel.setVisible(event.isActive() && super.isShowing()));
        CommonEventBus.getEventBus().addHandler(ImageListSelectedEvent.TYPE, (entity -> {
            if (Objects.equals(entity.getSelectedEntity(), imageEntity)) {
                if (!isShowing()) {
                    show();
                }
            } else if (isShowing()) {
                hide();
            }
        }));
        MainLayoutPanel.addMainAreaResizeHandler(event -> setDefaultSize());
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        if ((outreachImageIdToBeOpened != null || outreachImageNameToBeOpened != null) && mission.equals(missionToBeOpened)) {
            show();
        }
        this.addSingleElementAbleToInitiateMoveOperation(header.getElement());
    }

    @Override
    protected void onResize() {
        setMaxHeight();
    }

    @Override
    protected Element getResizeElement() {
        return mainContainer.getElement();
    }

    private void setDefaultSize() {
        Size size = getDefaultSize();
        mainContainer.setWidth(size.width + "px");
        mainContainer.setHeight(size.height + "px");

        Style containerStyle = mainContainer.getElement().getStyle();
        containerStyle.setPropertyPx("minWidth", 150);
        containerStyle.setPropertyPx("minHeight", 100);
    }

    @Override
    protected Element getMovableElement() {
        return header.getElement();
    }

    private void getData() {
        if (outreachImageDescriptor == null) {
            if (DescriptorRepository.getInstance().hasDescriptors(EsaSkyWebConstants.CATEGORY_IMAGES)) {
                fetchData();
            } else {
                DescriptorRepository.getInstance()
                        .registerDescriptorLoadedObserver(EsaSkyWebConstants.CATEGORY_IMAGES, this::fetchData);
            }
        }
    }

    public void show() {
        super.show();
        getData();
        if (imageEntity != null) {
            imageEntity.setIsPanelClosed(false);
        }
    }


    public void close() {
        if (imageEntity != null) {
            imageEntity.setIsPanelClosed(true);
        }

        super.hide();
    }


    protected abstract CommonTapDescriptor getOutreachImageDescriptor();

    private void fetchData() {
        if (outreachImageDescriptor != null) {
            return;
        }

        outreachImageDescriptor = getOutreachImageDescriptor();

        if (outreachImageDescriptor == null) {
            return;
        }

        imageEntity = EntityRepository.getInstance().createImageListEntity(outreachImageDescriptor);
        if (mission.equals(missionToBeOpened)) {
            if (outreachImageIdToBeOpened != null) {
                imageEntity.setIdToBeOpened(outreachImageIdToBeOpened, startupMinimized);
            } else if (outreachImageNameToBeOpened != null) {
                imageEntity.setNameToBeOpened(outreachImageNameToBeOpened);
            }
        }

        tableContainer.add(imageEntity.createTablePanel().getWidget());
        imageEntity.fetchData();
        setMaxSize();
        hideFootprints(defaultHideFootprints);
    }


    private void initView() {
        this.getElement().addClassName("outreachImagePanel");

        header = new PopupHeader<>(this, TextMgr.getInstance().getText(getLabelPrefix() + "_header"),
                TextMgr.getInstance().getText(getLabelPrefix() + "_helpText"),
                TextMgr.getInstance().getText(getLabelPrefix() + "_helpTitle"),
                event -> close(), "Close panel");


        ESASkySlider opacitySlider = new ESASkySlider(0, 1.0, 250);
        opacitySlider.registerValueChangeObserver(value -> imageEntity.setOpacity(value));

        Label opacityLabel = new Label();
        opacityLabel.setText(TextMgr.getInstance().getText("targetlist_opacity"));
        opacityLabel.setStyleName("outreachImagePanel__opacityLabel");
        opacityPanel = new FlowPanel();
        opacityPanel.addStyleName("outreachImagePanel__opacityControl");
        opacityPanel.add(opacityLabel);
        opacityPanel.add(opacitySlider);
        opacityPanel.setVisible(false);
        MainLayoutPanel.addElementToMainArea(opacityPanel);
        hideFootprintsSwitch = new EsaSkySwitch("outreachImagePanel__hideFootprintsSwitch_" + mission, true, TextMgr.getInstance().getText("outreachImage_hideFootprints"), "");
        hideFootprintsSwitch.addStyleName("outreachImagePanel__footprintSwitch");
        hideFootprintsSwitch.addClickHandler(event -> hideFootprints(!isHidingFootprints));
        header.addActionWidget(hideFootprintsSwitch);
        mainContainer.add(header);
        mainContainer.add(tableContainer);
        mainContainer.getElement().setId("outreachImagePanelContainer_" + this.mission);
        this.add(mainContainer);
    }

    private String getLabelPrefix() {
        return "outreachImagePanel_" + mission;
    }

    @Override
    public void setMaxSize() {
        Style elementStyle = mainContainer.getElement().getStyle();
        int maxWidth = MainLayoutPanel.getMainAreaWidth() + MainLayoutPanel.getMainAreaAbsoluteLeft() - getAbsoluteLeft() - 15;
        elementStyle.setPropertyPx("maxWidth", maxWidth);
        elementStyle.setPropertyPx("maxHeight", MainLayoutPanel.getMainAreaHeight() - getAbsoluteTop());
        setMaxHeight();
    }

    private void setMaxHeight() {
        int headerSize = header.getOffsetHeight();
        int height = mainContainer.getOffsetHeight() - headerSize - 5;

        if (height > MainLayoutPanel.getMainAreaHeight()) {
            height = MainLayoutPanel.getMainAreaHeight() - headerSize - 5;
        }

        tableContainer.getElement().getStyle().setPropertyPx("height", height);
    }

    public static void setStartupId(String id, String mission) {
        outreachImageIdToBeOpened = id;
        missionToBeOpened = mission;
    }

    public static void setStartupId(String id, String mission, boolean hideFootprints) {
        if (id != null) {
            outreachImageIdToBeOpened = id;
            defaultHideFootprints = hideFootprints;
            missionToBeOpened = mission;
        }
    }

    public static void setStartupId(String id, String mission, boolean hideFootprints, boolean minimized) {
        outreachImageIdToBeOpened = id;
        defaultHideFootprints = hideFootprints;
        startupMinimized = minimized;
        missionToBeOpened = mission;
    }

    public static void setStartupName(String name, String mission) {
        outreachImageNameToBeOpened = name;
        missionToBeOpened = mission;
    }

    public JSONArray getAllImageIds(ICommand command) {
        return getAllImageAttribute(command, true);
    }


    public JSONArray getAllImageNames(ICommand command) {
        return getAllImageAttribute(command, false);
    }

    public JSONArray getAllImageAttribute(ICommand command, boolean id) {
        if (imageEntity == null) {
            getData();
            imageEntity.getTablePanel().registerObserver(new TableObserver() {
                @Override
                public void numberOfShownRowsChanged(int numberOfShownRows) {
                    // Not needed here
                }

                @Override
                public void onSelection(ITablePanel selectedTablePanel) {
                    // Not needed here
                }

                @Override
                public void onUpdateStyle(ITablePanel panel) {
                    // Not needed here
                }

                @Override
                public void onDataLoaded(int numberOfRows) {
                    if (numberOfRows > 0) {
                        command.onResult(id ? imageEntity.getIds() : imageEntity.getNames());
                        imageEntity.setIsPanelClosed(true);
                        imageEntity.getTablePanel().unregisterObserver(this);
                    }
                }

                @Override
                public void onRowSelected(GeneralJavaScriptObject row) {
                    // Not needed here
                }

                @Override
                public void onRowDeselected(GeneralJavaScriptObject row) {
                    // Not needed here
                }
            });

        } else {
            return id ? imageEntity.getIds() : imageEntity.getNames();
        }

        return null;
    }

    public void selectShapeByName(String name) {
        if (imageEntity != null) {
            imageEntity.selectShape(imageEntity.getIdFromName(name));
        } else {
            OutreachImagePanel.setStartupName(name, this.mission);
        }

        if (!isShowing()) {
            show();
        }
    }

    public void selectShape(String id) {
        if (imageEntity != null) {
            imageEntity.selectShape(id);
        } else {
            OutreachImagePanel.setStartupId(id, this.mission);
        }

        if (!isShowing()) {
            show();
        }
    }

    private void hideFootprints(boolean hide) {
        if (hide != isHidingFootprints) {
            isHidingFootprints = hide;
            hideFootprintsSwitch.setChecked(hide);
            imageEntity.setIsHidingShapes(hide);
        }
    }

    public void selectShapeMinimized(String id) {
        if (imageEntity != null) {
            imageEntity.showImage(id);
        } else {
            setStartupId(id, this.mission, true, true);
            getData();
        }
    }

}
