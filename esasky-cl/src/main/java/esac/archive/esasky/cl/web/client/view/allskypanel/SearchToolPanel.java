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

package esac.archive.esasky.cl.web.client.view.allskypanel;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteConstants;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteCoordinateFrameChangedEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteSelectSearchAreaEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.AladinShape;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.CoordinatesObject;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.IsShowingCoordintesInDegreesChangeEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.DecPosition;
import esac.archive.esasky.cl.web.client.model.RaPosition;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.DisplayUtils;
import esac.archive.esasky.cl.web.client.view.animation.EsaSkyAnimation;
import esac.archive.esasky.cl.web.client.view.common.Toggler;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyToggleButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.HelpButton;
import esac.archive.esasky.cl.web.client.view.common.icons.Icons;
import esac.archive.esasky.ifcs.model.coordinatesutils.ClientRegexClass;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesFrame;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesParser;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

public class SearchToolPanel extends FlowPanel {

    protected AladinShape source;

    private FlowPanel shapeButtonContainer = new FlowPanel();
    private EsaSkyToggleButton boxButton;
    private EsaSkyToggleButton circleButton;
    private EsaSkyToggleButton polyButton;

    private boolean toolboxIsVisible = false;

    private EsaSkyAnimation toggleShapeButtonsHeightAnimation;
    private EsaSkyAnimation toggleShapeButtonsMoveAnimation;

    private Toggler searchAreaDetailsToggler;
    private FlowPanel searchAreaDetailPanel;
    private FlowPanel circleDetails;
    private FlowPanel polyDetails;
    private FlowPanel boxDetails;

    private static Resources resources = GWT.create(Resources.class);
    private CssResource style;

    private static final String CIRCLE_BUTTON_STRING = "circle";

    private static final String ERROR_STRING = "error";

    public static interface Resources extends ClientBundle {
        @Source("searchToolPanel.css")
        @CssResource.NotStrict
        CssResource style();

        @Source("up_arrow_outline.png")
        @ImageOptions(flipRtl = true)
        ImageResource arrowIcon();
    }

    public SearchToolPanel() {
        style = resources.style();
        style.ensureInjected();
        initView();
        setVisible(false);
        hideToolbox();
    }

    private void hideSearchAreaDetails() {
        if (searchAreaDetailsToggler != null) {
            searchAreaDetailsToggler.setToggleStatus(false);
        }
    }

    private void showSearchAreaDetails() {
        if (searchAreaDetailsToggler != null) {
            searchAreaDetailPanel.clear();
            if (boxButton.getToggleStatus()) {
                searchAreaDetailPanel.add(boxDetails);
            } else if (polyButton.getToggleStatus()) {
                searchAreaDetailPanel.add(polyDetails);
            } else {
                searchAreaDetailPanel.add(circleDetails);
            }

            searchAreaDetailsToggler.setToggleStatus(true);
        }
    }

    private void toggleOtherButtons(EsaSkyToggleButton buttonPressed) {
        if (buttonPressed != boxButton) {
            boxButton.setToggleStatus(false);
        }
        if (buttonPressed != circleButton) {
            circleButton.setToggleStatus(false);
        }
        if (buttonPressed != polyButton) {
            polyButton.setToggleStatus(false);
        }

        if (searchAreaDetailsToggler != null) {
            searchAreaDetailsToggler.setToggleStatus(false);
        }

        hideSearchAreaDetails();
    }

    public void deToggleAllButtons() {
        boxButton.setToggleStatus(false);
        circleButton.setToggleStatus(false);
        polyButton.setToggleStatus(false);
        hideSearchAreaDetails();
    }

    public void showWithPolyDetails(boolean startSearch) {
        deToggleAllButtons();
        polyButton.setToggleStatus(true);
        showToolbox(startSearch);
    }

    public void showWithConeDetails(boolean startSearch) {
        deToggleAllButtons();
        circleButton.setToggleStatus(true);
        showToolbox(startSearch);
    }

    public void showToolbox(boolean startSearch) {
        toolboxIsVisible = true;
        setVisible(true);
        toggleShapeButtonsHeightAnimation.animateTo(400, 500);
        toggleShapeButtonsMoveAnimation.animateTo(30, 250);

        if (startSearch) {
            if (boxButton.getToggleStatus()) {
                startSearchMode(boxButton, "box");
            } else if (polyButton.getToggleStatus()) {
                startSearchMode(polyButton, "polygon");
            } else if (circleButton.getToggleStatus()) {
                startSearchMode(circleButton, CIRCLE_BUTTON_STRING);
            } else {
                circleButton.toggle();
                startSearchMode(circleButton, CIRCLE_BUTTON_STRING);
            }
        }
    }

    public void hideToolbox() {
        toolboxIsVisible = false;
        toggleShapeButtonsHeightAnimation.animateTo(0, 400);
        toggleShapeButtonsMoveAnimation.animateTo(0, 400);

        if (AladinLiteWrapper.getAladinLite().isAttached()) {
            AladinLiteWrapper.getAladinLite().endSelectionMode();
        }
    }

    public void toggleToolbox() {
        if (toolboxIsVisible) {
            hideToolbox();
            AladinLiteWrapper.getAladinLite().clearSearchArea();
        } else {
            showToolbox(true);
        }
    }

    @SuppressWarnings("java:S3776")
    private FlowPanel initConeDetails() {

        FlowPanel detailContainer = new FlowPanel();
        detailContainer.setStyleName("searchToolbox__detailsPanelCircle");
        Label headerLabel = new Label();
        headerLabel.setText(TextMgr.getInstance().getText("searchToolbox_circle"));
        headerLabel.setStyleName("searchToolbox__detailsPanelHeaderLabel");
        detailContainer.add(headerLabel);

        FlowPanel radiusContainer = new FlowPanel();
        Label radiusLabel = new Label();
        radiusLabel.setText(TextMgr.getInstance().getText("searchToolbox_radius"));
        TextBox radiusText = new TextBox();
        radiusContainer.add(radiusLabel);
        radiusContainer.add(radiusText);

        FlowPanel raContainer = new FlowPanel();
        Label raLabel = new Label();
        raLabel.setText("RA");
        TextBox raText = new TextBox();
        raContainer.add(raLabel);
        raContainer.add(raText);

        FlowPanel decContainer = new FlowPanel();
        Label decLabel = new Label();
        decLabel.setText("Dec");
        TextBox decText = new TextBox();
        decContainer.add(decLabel);
        decContainer.add(decText);

        EsaSkyButton btn = new EsaSkyButton(TextMgr.getInstance().getText("searchToolbox_submitButton"));
        btn.addStyleName("searchToolbox__detailsPanelSubmitButton");

        detailContainer.add(radiusContainer);
        detailContainer.add(raContainer);
        detailContainer.add(decContainer);
        detailContainer.add(btn);

        String inputErrorClassName = "input__error";
        detailContainer.addDomHandler(event -> {
            if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                btn.click();
            }
        }, KeyUpEvent.getType());

        btn.addClickHandler(event -> {
            String raStr = raText.getText();
            String decStr = decText.getText().trim();
            String radiusStr = radiusText.getText();

            detailContainer.removeStyleName(inputErrorClassName);
            boolean success = createConicalSearchArea(raStr, decStr, radiusStr);

            if (!success) {
                detailContainer.addStyleName(inputErrorClassName);
            }
        });

        CommonEventBus.getEventBus().addHandler(IsShowingCoordintesInDegreesChangeEvent.TYPE, () -> {
            try {
                String[] coordStr = updateCoordinateFormat(new RaPosition(raText.getText()), new DecPosition(decText.getText(), true));
                raText.setText(coordStr[0]);
                decText.setText(coordStr[1]);
            } catch (Exception ex) {
                Log.debug(ex.getMessage(), ex);
            }
        });

        CommonEventBus.getEventBus().addHandler(AladinLiteCoordinateFrameChangedEvent.TYPE, cooFrameEvent -> {
            try {
                CoordinatesFrame oldCooFrame;
                CoordinatesFrame newCooFrame;
                if (cooFrameEvent.getCoordinateFrame() == AladinLiteConstants.CoordinateFrame.GALACTIC) {
                    raLabel.setText("GLon");
                    decLabel.setText("GLat");
                    oldCooFrame = CoordinatesFrame.J2000;
                    newCooFrame = CoordinatesFrame.GALACTIC;
                } else {
                    raLabel.setText("RA");
                    decLabel.setText("Dec");
                    oldCooFrame = CoordinatesFrame.GALACTIC;
                    newCooFrame = CoordinatesFrame.J2000;
                }

                String raStr = raText.getText();
                String decStr = decText.getText();
                if (!raStr.isEmpty() && !decStr.isEmpty()) {
                    double[] coords = convertCoordinatesToFrame(raStr, decStr, oldCooFrame, newCooFrame);
                    String[] coordStr = updateCoordinateFormat(new RaPosition(coords[0]), new DecPosition(coords[1]));
                    raText.setText(coordStr[0]);
                    decText.setText(coordStr[1]);
                }

            } catch (Exception ex) {
                Log.debug(ex.getMessage(), ex);
            }
        });

        CommonEventBus.getEventBus().addHandler(AladinLiteSelectSearchAreaEvent.TYPE, searchAreaEvent -> {
            if (searchAreaEvent != null && searchAreaEvent.getSearchArea() != null && searchAreaEvent.getSearchArea().isCircle()) {
                detailContainer.removeStyleName(inputErrorClassName);
                radiusText.setText(searchAreaEvent.getSearchArea().getRadius());

                CoordinatesObject coordObj = searchAreaEvent.getSearchArea().getJ2000Coordinates()[0];
                double[] coords = convertCoordinatesToFrame(Double.toString(coordObj.getRaDeg()), Double.toString(coordObj.getDecDeg()), CoordinatesFrame.J2000);

                String[] coordStr = updateCoordinateFormat(new RaPosition(coords[0]), new DecPosition(coords[1]));
                raText.setText(coordStr[0]);
                decText.setText(coordStr[1]);
                showSearchAreaDetails();
            }

        });

        return detailContainer;
    }

    public boolean createConicalSearchArea(String ra, String dec, String radius) {
        boolean success = true;

        try {
            CoordinatesFrame cooFrame = CoordinatesFrame.valueOf(AladinLiteWrapper.getAladinLite().getCooFrame().toUpperCase());
            double[] coords = convertCoordinatesToFrame(ra, dec, cooFrame, CoordinatesFrame.J2000);

            AladinLiteWrapper.getAladinLite().createSearchArea("CIRCLE ICRS " + coords[0] + " " + coords[1] + " " + radius);
            AladinLiteWrapper.getAladinLite().endSelectionMode();
        } catch (Exception ex) {
            Log.debug(ex.getMessage(), ex);
            DisplayUtils.showMessageDialogBox(TextMgr.getInstance().getText("searchPanel_wrongCoordsInput"), TextMgr.getInstance().getText(ERROR_STRING).toUpperCase(), UUID.randomUUID().toString(), TextMgr.getInstance().getText(ERROR_STRING));

            success = false;
        }

        return success;
    }

    private String[] updateCoordinateFormat(RaPosition raPos, DecPosition decPos) {

        if (GUISessionStatus.isShowingCoordinatesInDegrees()) {
            return new String[]{Double.toString(raPos.getRaDeg()), Double.toString(decPos.getDecDegFix())};
        } else {
            return new String[]{raPos.getSpacedHmsString(), decPos.getSpacedDmsStringFix()};
        }
    }

    private double[] convertCoordinatesToFrame(String raStr, String decStr, CoordinatesFrame oldCooFrame) {
        CoordinatesFrame cooFrame = CoordinatesFrame.valueOf(AladinLiteWrapper.getAladinLite().getCooFrame().toUpperCase());
        return convertCoordinatesToFrame(raStr, decStr, oldCooFrame, cooFrame);
    }

    private double[] convertCoordinatesToFrame(String raStr, String decStr, CoordinatesFrame oldCooFrame, CoordinatesFrame newCooFrame) {
        if (!decStr.matches("^([+\\-]).*")) {
            decStr = "+" + decStr;
        }
        return CoordinatesParser.convertCoordsToDegrees(new ClientRegexClass(), raStr + " " + decStr, oldCooFrame, newCooFrame);
    }

    private FlowPanel initPolygonDetails(String header) {
        FlowPanel detailContainer = new FlowPanel();
        detailContainer.setStyleName("searchToolbox__detailsPanelPoly");

        FlowPanel headerContainer = new FlowPanel();
        headerContainer.addStyleName("searchToolbox__detailsPanelHeaderContainer");
        Label headerLabel = new Label();
        headerLabel.setText(header);
        headerLabel.setStyleName("searchToolbox__detailsPanelHeaderLabel");
        headerContainer.add(headerLabel);

        TextBox stcsText = new TextBox();
        stcsText.setTitle(TextMgr.getInstance().getText("searchToolbox_title"));

        detailContainer.add(headerContainer);
        detailContainer.add(stcsText);

        EsaSkyButton btn = new EsaSkyButton(TextMgr.getInstance().getText("searchToolbox_submitButton"));
        btn.addStyleName("searchToolbox__detailsPanelSubmitButton");
        detailContainer.add(btn);

        detailContainer.addDomHandler(event -> {
            if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                btn.click();
            }
        }, KeyUpEvent.getType());

        String inputErrorClassName = "input__error";
        btn.addClickHandler(event -> {
            stcsText.removeStyleName(inputErrorClassName);
            boolean success = createPolygonSearchArea(stcsText.getText());

            if (!success) {
                stcsText.addStyleName(inputErrorClassName);
            }
        });

        CommonEventBus.getEventBus().addHandler(AladinLiteSelectSearchAreaEvent.TYPE, searchAreaEvent -> {
            if (searchAreaEvent != null && searchAreaEvent.getSearchArea() != null && !searchAreaEvent.getSearchArea().isCircle()) {
                stcsText.removeStyleName(inputErrorClassName);
                stcsText.setText(searchAreaEvent.getSearchArea().getAreaType() + " ICRS " + Arrays.stream(searchAreaEvent.getSearchArea().getJ2000Coordinates()).map(x -> x.getRaDeg() + " " + x.getDecDeg()).collect(Collectors.joining(" ")));
                showSearchAreaDetails();
            }

        });

        return detailContainer;
    }

    public boolean createPolygonSearchArea(String stcs) {
        boolean success = true;

        try {
            // Discard drawn shape if any
            AladinLiteWrapper.getAladinLite().setSelectionMode("discard");
            AladinLiteWrapper.getAladinLite().endSelectionMode();
            // Create new search area
            AladinLiteWrapper.getAladinLite().createSearchArea(stcs);
        } catch (Exception ex) {
            Log.debug(ex.getMessage(), ex);
            DisplayUtils.showMessageDialogBox(TextMgr.getInstance().getText("searchPanel_wrongCoordsInput"), TextMgr.getInstance().getText(ERROR_STRING).toUpperCase(), UUID.randomUUID().toString(), TextMgr.getInstance().getText(ERROR_STRING));
            success = false;
        }

        return success;
    }

    private void initView() {

        toggleShapeButtonsHeightAnimation = getShapeButtonsAnimationHeight();
        toggleShapeButtonsMoveAnimation = getShapeButtonsAnimationTop();
        shapeButtonContainer.addStyleName("searchToolbox__shapeButtonContainerHorizontal");
        searchAreaDetailPanel = new FlowPanel();

        circleDetails = initConeDetails();
        polyDetails = initPolygonDetails(TextMgr.getInstance().getText("searchToolbox_polygon"));
        boxDetails = initPolygonDetails(TextMgr.getInstance().getText("searchToolbox_box"));

        FlowPanel headerContainer = new FlowPanel();
        headerContainer.addStyleName("searchToolbox__searchHeaderContainer");
        Label headerLabel = new Label(TextMgr.getInstance().getText("searchToolbox_title"));
        headerLabel.addStyleName("searchToolbox__searchHeaderLabel");
        HelpButton helpButton = new HelpButton(TextMgr.getInstance().getText("searchToolbox_helpText"), TextMgr.getInstance().getText("searchToolbox_helpHeader"));
        helpButton.setStyleName("searchToolboxHelpButton");

        headerContainer.add(headerLabel);
        headerContainer.add(helpButton);

        add(headerContainer);

        searchAreaDetailsToggler = new Toggler(searchAreaDetailPanel);
        searchAreaDetailsToggler.addClickHandler(event -> {

            if (boxButton.getToggleStatus()) {
                searchAreaDetailPanel.clear();
                searchAreaDetailPanel.add(boxDetails);
            } else if (polyButton.getToggleStatus()) {
                searchAreaDetailPanel.clear();
                searchAreaDetailPanel.add(polyDetails);
            } else {
                searchAreaDetailPanel.clear();
                searchAreaDetailPanel.add(circleDetails);
            }
        });

        boxButton = new EsaSkyToggleButton(Icons.getDashedRectangleIcon());
        addButtonBehaviorAndStyle(boxButton, "box");
        circleButton = new EsaSkyToggleButton(Icons.getConeDashedIcon());
        addButtonBehaviorAndStyle(circleButton, CIRCLE_BUTTON_STRING);
        polyButton = new EsaSkyToggleButton(Icons.getDashedPolyIcon());
        addButtonBehaviorAndStyle(polyButton, "polygon");

        shapeButtonContainer.add(boxButton);
        shapeButtonContainer.add(circleButton);
        shapeButtonContainer.add(polyButton);
        add(shapeButtonContainer);

        if (searchAreaDetailsToggler != null && searchAreaDetailPanel != null) {
            add(searchAreaDetailsToggler);
            add(searchAreaDetailPanel);
        }

        this.getElement().setId("searchToolbox");

    }

    private void addButtonBehaviorAndStyle(EsaSkyToggleButton button, String mode) {
        addCommonButtonStyle(button, TextMgr.getInstance().getText("searchToolbox_" + mode + "ButtonTooltip"));
        button.addClickHandler(event -> {
            if (button.getToggleStatus()) {
                startSearchMode(button, mode);
            } else {
                endSearchMode();
            }
        });
    }

    private void startSearchMode(EsaSkyToggleButton button, String mode) {
        AladinLiteWrapper.getAladinLite().setSelectionMode(mode);
        AladinLiteWrapper.getAladinLite().setSelectionType("SEARCH");
        AladinLiteWrapper.getAladinLite().startSelectionMode();
        toggleOtherButtons(button);
    }

    private void endSearchMode() {
        searchAreaDetailsToggler.close();
        hideSearchAreaDetails();
        AladinLiteWrapper.getAladinLite().endSelectionMode();
        AladinLiteWrapper.getAladinLite().clearSearchArea();
    }

    private void addCommonButtonStyle(EsaSkyButton button, String tooltip) {
        button.setNonTransparentBackground();
        button.setBigStyle();
        button.addStyleName("searchToolboxButton");
        button.setTitle(tooltip);
    }

    protected EsaSkyAnimation getShapeButtonsAnimationHeight() {
        return new EsaSkyAnimation() {

            @Override
            protected void setCurrentPosition(double newPosition) {
                getElement().getStyle().setProperty("maxHeight", newPosition, Unit.PX);
                shapeButtonContainer.getElement().getStyle().setProperty("maxHeight", newPosition, Unit.PX);
            }

            @Override
            protected Double getCurrentPosition() {
                String heightString = shapeButtonContainer.getElement().getStyle().getProperty("maxHeight");
                if (heightString.equals("")) {
                    heightString = "0px";
                }
                // remove suffix "px"
                heightString = heightString.substring(0, heightString.length() - 2);
                return new Double(heightString);
            }

            @Override
            protected void onComplete() {
                super.onComplete();

                if (!toolboxIsVisible) {
                    setVisible(false);
                }
            }
        };
    }

    protected EsaSkyAnimation getShapeButtonsAnimationTop() {
        return new EsaSkyAnimation() {

            @Override
            protected void setCurrentPosition(double newPosition) {
                getElement().getStyle().setProperty("top", newPosition, Unit.PX);
                shapeButtonContainer.getElement().getStyle().setProperty("top", newPosition, Unit.PX);
            }

            @Override
            protected Double getCurrentPosition() {
                String topString = shapeButtonContainer.getElement().getStyle().getProperty("top");
                if (topString.equals("")) {
                    topString = "0px";
                }
                // remove suffix "px"
                topString = topString.substring(0, topString.length() - 2);
                return new Double(topString);
            }
        };
    }
}
