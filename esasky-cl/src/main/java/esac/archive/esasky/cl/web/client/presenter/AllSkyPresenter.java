package esac.archive.esasky.cl.web.client.presenter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.VerticalPanel;

import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteShapeSelectedEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteShapeSelectedEventHandler;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.AladinShape;
import esac.archive.esasky.ifcs.model.client.HiPS;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesConversion;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesFrame;
import esac.archive.esasky.ifcs.model.shared.ColorPalette;
import esac.archive.esasky.ifcs.model.shared.ESASkySSOSearchResult.ESASkySSOObjType;
import esac.archive.esasky.cl.wcstransform.module.footprintbuilder.STCSGeneratorFactory;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.AddShapeTooltipEvent;
import esac.archive.esasky.cl.web.client.event.AddShapeTooltipEventHandler;
import esac.archive.esasky.cl.web.client.event.UrlChangedEvent;
import esac.archive.esasky.cl.web.client.event.hips.HipsChangeEvent;
import esac.archive.esasky.cl.web.client.event.hips.HipsChangeEventHandler;
import esac.archive.esasky.cl.web.client.event.planning.FutureFootprintClearEvent;
import esac.archive.esasky.cl.web.client.event.planning.FutureFootprintClearEventHandler;
import esac.archive.esasky.cl.web.client.event.planning.FutureFootprintEvent;
import esac.archive.esasky.cl.web.client.event.planning.FutureFootprintEventHandler;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.PlanningConstant;
import esac.archive.esasky.cl.web.client.view.allskypanel.MultiTargetTooltip;
import esac.archive.esasky.cl.web.client.view.allskypanel.PlanningDetectorCenterTooltip;
import esac.archive.esasky.cl.web.client.view.allskypanel.Tooltip;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.planningmenu.FutureFootprintRow;
import esac.archive.esasky.cl.web.client.view.searchpanel.targetlist.MultiTargetSourceConstants;

/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class AllSkyPresenter {

    /** local instance of view. */
    private View view;

    private Map<FutureFootprintRow, Map<String, JavaScriptObject>> planningFootprintsPerInstrument = null;

    private FutureFootprintRow previuosPlanningFootprintRow = null;
    private FutureFootprintRow currentPlanningFootprintRow = null;

    private List<SSOOverlayAndPolyline> ssoPolyline = null;
    private HiPS currentHiPS = EsaSkyWebConstants.getInitialHiPS();
    
    private HiPS currentOverlay;
    private double currentOpacity = 0;
    
    /**
     * View interface.
     */
    public interface View {

        VerticalPanel getAllSkyContainerPanel();

        void showSourceTooltip(Tooltip tooltip);

        HasClickHandlers getZoomOutClickHandler();

        HasClickHandlers getZoomInClickHandler();

        void hideTooltip();

        void deToggleSelectionMode();

        void areaSelectionKeyboardShortcutStart();
    }

    /**
     * Class Constructor.
     * @param inputView Input View.
     */
    public AllSkyPresenter(final View inputView) {
        this.view = inputView;
        bind();
    }

    /**
     * Bind view with presenter.
     */
    private void bind() {

        CommonEventBus.getEventBus().addHandler(FutureFootprintClearEvent.TYPE,
                new FutureFootprintClearEventHandler() {

                    @Override
                    public void clearPlanningFootprint(FutureFootprintClearEvent event) {
                        AllSkyPresenter.this.removePlanningFootprint(event.getFutureFootprintRow());
                    }
                });

        CommonEventBus.getEventBus().addHandler(FutureFootprintEvent.TYPE,
                new FutureFootprintEventHandler() {

                    @Override
                    public void drawPlanningFootprint(FutureFootprintEvent event) {
                        AllSkyPresenter.this.drawPlanning(event.getFutureFootprintRow());

                    }
                });
        CommonEventBus.getEventBus().addHandler(HipsChangeEvent.TYPE, new HipsChangeEventHandler() {

            @Override
            public void onChangeEvent(final HipsChangeEvent changeEvent) {

            	changeHiPS(changeEvent.getHiPS(), changeEvent.getColorPalette(),
                		changeEvent.isBaseImage(), changeEvent.getOpacity());
                CommonEventBus.getEventBus().fireEvent(new UrlChangedEvent());
            }
        });
        
        /*
         * On mouse click on top of a source show a tooltip if not publications source
         */
        CommonEventBus.getEventBus().addHandler(AladinLiteShapeSelectedEvent.TYPE, new AladinLiteShapeSelectedEventHandler() {
			
			@Override
			public void onShapeSelectionEvent(AladinLiteShapeSelectedEvent selectEvent) {
				AladinShape obj = selectEvent.getShape();
				if (obj != null) {
				    if(obj.getDataDetailsByKey(MultiTargetSourceConstants.CATALOGUE_NAME) != null &&
				    		obj.getDataDetailsByKey(MultiTargetSourceConstants.CATALOGUE_NAME).equals(MultiTargetSourceConstants.OVERLAY_NAME)) {
				        AllSkyPresenter.this.view.showSourceTooltip(new MultiTargetTooltip(obj));
				    } else if(obj.getDataDetailsByKey(PlanningConstant.OVERLAY_PROPERTY) != null &&
				    		obj.getDataDetailsByKey(PlanningConstant.OVERLAY_PROPERTY).equals(PlanningConstant.OVERLAY_NAME)) {
				        AllSkyPresenter.this.view.showSourceTooltip(new PlanningDetectorCenterTooltip(obj));
				    }
				}
			}
        });
        
        CommonEventBus.getEventBus().addHandler(AddShapeTooltipEvent.TYPE, new AddShapeTooltipEventHandler() {
            
            @Override
            public void onEvent(AddShapeTooltipEvent event) {
              AllSkyPresenter.this.view.showSourceTooltip(event.getTooltip());
            }
        });
     
        // Click on + (ZoomIn) button
        this.view.getZoomInClickHandler().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(final ClickEvent event) {
                AladinLiteWrapper.getInstance().increaseZoom();
            }
        });

        // Click on - (ZoomOut) button
        this.view.getZoomOutClickHandler().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(final ClickEvent event) {
                AladinLiteWrapper.getInstance().decreaseZoom();
            }
        });

    }

    void removePlanningFootprint(FutureFootprintRow footprintRow) {
        planningFootprintsPerInstrument.remove(footprintRow);
        currentPlanningFootprintRow = previuosPlanningFootprintRow;
        drawPlanningPolygons();
    }

    public String drawPlanning(FutureFootprintRow futureFootprintRow) {

        previuosPlanningFootprintRow = currentPlanningFootprintRow;
        currentPlanningFootprintRow = futureFootprintRow;

        if (planningFootprintsPerInstrument == null) {
            planningFootprintsPerInstrument = new HashMap<FutureFootprintRow, Map<String, JavaScriptObject>>();
        }

        double raDeg = futureFootprintRow.getCenterRaDeg();
        double decDeg = futureFootprintRow.getCenterDecDeg();

        if (AladinLiteWrapper.getCoordinatesFrame() == CoordinatesFrame.GALACTIC) {
            double galacticCoordinates[] = CoordinatesConversion.convertPointGalacticToJ2000(raDeg,
                    decDeg);
            raDeg = galacticCoordinates[0];
            decDeg = galacticCoordinates[1];
        }

        Map<String, String> stcsPolygonInstrumentMap = STCSGeneratorFactory.getSTCSGenerator(
                futureFootprintRow.getInstrument().getMission().getMissionName()).doAll(
                futureFootprintRow.getInstrument().getInstrumentName(),
                futureFootprintRow.getAperture(),
                futureFootprintRow.getRotationDeg(), raDeg, decDeg);

        Map<String, JavaScriptObject> polygonJsInstrumentMap = new HashMap<String, JavaScriptObject>();

        String individualInstrumentPolygon = "";

        for (String inst : stcsPolygonInstrumentMap.keySet()) {

            JavaScriptObject polygon = AladinLiteWrapper.getAladinLite().createFootprintFromSTCS(
                    stcsPolygonInstrumentMap.get(inst));

            if (!futureFootprintRow.getIsAllInstrumentsSelected()) {
                if (inst.equals(futureFootprintRow.getInstrument().getInstrumentName())) {

                    polygonJsInstrumentMap.put(inst, polygon);
                    planningFootprintsPerInstrument.put(futureFootprintRow, polygonJsInstrumentMap);
                }
            } else {
                polygonJsInstrumentMap.put(inst, polygon);
                planningFootprintsPerInstrument.put(futureFootprintRow, polygonJsInstrumentMap);
            }

            if (inst.equals(futureFootprintRow.getInstrument().getInstrumentName())) {
                individualInstrumentPolygon = stcsPolygonInstrumentMap.get(inst);
            }
        }

        drawPlanningPolygons();

        return individualInstrumentPolygon;
    }

    private void drawPlanningPolygons() {

        JavaScriptObject planningOverlay = AladinLiteWrapper.getInstance().getPlanningOverlay();
        JavaScriptObject planningOverlaySelectedInstrument = AladinLiteWrapper.getInstance()
                .getPlanningOverlaySelectedInstrument();

        AladinLiteWrapper.getInstance().removeAllFootprintsFromOverlay(planningOverlay);
        AladinLiteWrapper.getInstance().removeAllFootprintsFromOverlay(
                planningOverlaySelectedInstrument);

        AladinLiteWrapper.getAladinLite().removeAllSourcesFromCatalog(
                AladinLiteWrapper.getInstance().getFutureCatalog());

        AladinLiteWrapper.getAladinLite().removeAllSourcesFromCatalog(
                AladinLiteWrapper.getInstance().getFutureSelectedDetectorCatalogue());

        for (Entry<FutureFootprintRow, Map<String, JavaScriptObject>> currEntry : planningFootprintsPerInstrument
                .entrySet()) {

            if (currentPlanningFootprintRow == currEntry.getKey()) {
                double raDeg = currentPlanningFootprintRow.getCenterRaDeg();
                double decDeg = currentPlanningFootprintRow.getCenterDecDeg();
                Map<String, Vector<double[]>> detectorCenters = STCSGeneratorFactory
                        .getSTCSGenerator(
                                currentPlanningFootprintRow.getInstrument().getMission()
                                .getMissionName()).getDetectorsSkyCoordsForInstrument(
                                        raDeg, decDeg, currentPlanningFootprintRow.getRotationDeg(),
                                        currentPlanningFootprintRow.getInstrument().getInstrumentName(),
                                        currentPlanningFootprintRow.getAperture());

                if (!currentPlanningFootprintRow.getIsAllInstrumentsSelected()) {

                    for (Entry<String, Vector<double[]>> currCenter : detectorCenters.entrySet()) {

                        Log.debug("[cc] Det name " + currCenter.getKey()
                                + " coords " + currCenter.getValue().get(0)[0] + " "
                                + currCenter.getValue().get(0)[1]);

                        Map<String, Object> details = new HashMap<String, Object>();

                        details.put(PlanningConstant.INSTRUMENT, currentPlanningFootprintRow
                                .getInstrument().getInstrumentName());
                        details.put(PlanningConstant.DETECTOR, currCenter.getKey());
                        details.put(PlanningConstant.REFERENCE_RA,
                                Double.toString(currCenter.getValue().get(0)[0]));
                        details.put(PlanningConstant.REFERENCE_DEC,
                                Double.toString(currCenter.getValue().get(0)[1]));
                        details.put(PlanningConstant.OVERLAY_PROPERTY, PlanningConstant.OVERLAY_NAME);
                        details.put(PlanningConstant.COO_FRAME, AladinLiteWrapper
                                .getCoordinatesFrame().name());

                        if (currCenter.getKey().equals(currentPlanningFootprintRow.getAperture())) {

                            AladinLiteWrapper.getInstance()
                            .addSourcesToFutureSelectedDetectorCatalogue(
                                    Double.toString(currCenter.getValue().get(0)[0]),
                                    Double.toString(currCenter.getValue().get(0)[1]),
                                            details);
                        } else {
                            AladinLiteWrapper.getInstance().addSourcesToFutureCatalog(
                                    Double.toString(currCenter.getValue().get(0)[0]),
                                    Double.toString(currCenter.getValue().get(0)[1]), details);
                        }
                    }

                } else {
                    Map<String, Vector<double[]>> currDetectorCenters = STCSGeneratorFactory
                            .getSTCSGenerator(
                                    currentPlanningFootprintRow.getInstrument().getMission()
                                    .getMissionName())
                            .getDetectorsSkyCoordsForFoV(
                                    raDeg,
                                            decDeg,
                                    currentPlanningFootprintRow.getRotationDeg(),
                                            currentPlanningFootprintRow.getInstrument().getInstrumentName(),
                                            currentPlanningFootprintRow.getAperture());

                    for (Entry<String, Vector<double[]>> currCenter : currDetectorCenters
                            .entrySet()) {

                        Log.debug("[cc] Det name " + currCenter.getKey()
                                + " coords " + currCenter.getValue().get(0)[0] + " "
                                + currCenter.getValue().get(0)[1]);
                        Map<String, Object> details = new HashMap<String, Object>();

                        details.put(PlanningConstant.INSTRUMENT, currentPlanningFootprintRow.getInstrument().getInstrumentName());
                        details.put(PlanningConstant.DETECTOR, currCenter.getKey());
                        details.put(PlanningConstant.REFERENCE_RA,
                                Double.toString(currCenter.getValue().get(0)[0]));
                        details.put(PlanningConstant.REFERENCE_DEC,
                                Double.toString(currCenter.getValue().get(0)[1]));
                        details.put(PlanningConstant.COO_FRAME, AladinLiteWrapper
                                .getCoordinatesFrame().name());

                        if (currCenter.getKey().equals(currentPlanningFootprintRow.getAperture())) {
                            AladinLiteWrapper.getInstance()
                            .addSourcesToFutureSelectedDetectorCatalogue(
                                    Double.toString(currCenter.getValue().get(0)[0]),
                                    Double.toString(currCenter.getValue().get(0)[1]),
                                    details);
                        } else {
                            AladinLiteWrapper.getInstance().addSourcesToFutureCatalog(
                                    Double.toString(currCenter.getValue().get(0)[0]),
                                    Double.toString(currCenter.getValue().get(0)[1]), details);
                        }
                    }
                }
            }

            FutureFootprintRow currentFutureFootprintRow = currEntry.getKey();
            Map<String, JavaScriptObject> instrumentJsPolygons = currEntry.getValue();

            for (Entry<String, JavaScriptObject> currInstrumentJsPolygons : instrumentJsPolygons
                    .entrySet()) {
                if (currInstrumentJsPolygons.getKey().equals(
                        currentFutureFootprintRow.getInstrument().getInstrumentName())) {
                    AladinLiteWrapper.getAladinLite().addFootprintToOverlay(
                            planningOverlaySelectedInstrument, currInstrumentJsPolygons.getValue());
                } else {
                    AladinLiteWrapper.getAladinLite().addFootprintToOverlay(planningOverlay,
                            currInstrumentJsPolygons.getValue());
                }
            }
        }
    }

    /**
     * changeHiPS().
     * @param hips Input HiPS object
     * @param colorPalette Input ColorPalette object
     */
    protected final void changeHiPS(final HiPS hips, final ColorPalette colorPalette, boolean isBaseImage, double opacity) {
    	currentOpacity = opacity;
    	if(isBaseImage) {
	    	if (currentHiPS != hips) {
	    		currentHiPS.setReversedColorMap(false);
	            currentHiPS = hips;
	            AladinLiteWrapper.getInstance().openHiPS(hips);
	            AladinLiteWrapper.getInstance().setColorPalette(colorPalette);
	            AladinLiteWrapper.getInstance().changeHiPSOpacity(Math.pow(opacity,0.25));
	            checkInverseForNewBaseHips(colorPalette);
				
	        } else {
	            AladinLiteWrapper.getInstance().setColorPalette(colorPalette);
	            AladinLiteWrapper.getInstance().changeHiPSOpacity(Math.pow(opacity,0.25));
	            checkInverseForBaseHips(hips, colorPalette);
	            
	        }
        }else {
			AladinLiteWrapper.getInstance().setOverlayImageLayerToNull();
			
			if(currentOverlay != null && hips == currentOverlay && isSameDouble(currentOpacity,opacity)) {
				AladinLiteWrapper.getInstance().createOverlayMap(hips, opacity, colorPalette);
			}else {
				AladinLiteWrapper.getInstance().createOverlayMap(hips, Math.pow(opacity,0.25), colorPalette);
			}
			
			currentOverlay = hips;
			
			checkInverseForOverlays(hips, colorPalette);
        }
        
    }
    
    private boolean isSameDouble(double v1, double v2) {
    	BigDecimal a = new BigDecimal(v1);
    	BigDecimal b = new BigDecimal(v2);
    	return a.equals(b);
    }
    
    private void checkInverseForNewBaseHips(final ColorPalette colorPalette) {
    	if(colorPalette.equals(ColorPalette.GREYSCALE_INV)) {
			AladinLiteWrapper.getInstance().getAladinLite().reverseColorMap();
			currentHiPS.setReversedColorMap(true);
		}
    }
    
    private void checkInverseForBaseHips(final HiPS hips, final ColorPalette colorPalette) {
    	if(!hips.isReversedColorMap() && colorPalette.equals(ColorPalette.GREYSCALE_INV)) {
			AladinLiteWrapper.getInstance().getAladinLite().reverseColorMap();
			hips.setReversedColorMap(true);
		}else if(hips.isReversedColorMap()  && !colorPalette.equals(ColorPalette.GREYSCALE_INV)) {
			AladinLiteWrapper.getInstance().getAladinLite().reverseColorMap();
			hips.setReversedColorMap(false);
		}
    }
    
    private void checkInverseForOverlays(final HiPS hips, final ColorPalette colorPalette) {
    	if(colorPalette.equals(ColorPalette.GREYSCALE_INV)) {
			AladinLiteWrapper.getInstance().getAladinLite().reverseOverlayColorMap();
			hips.setReversedColorMap(true);
		}else if(hips.isReversedColorMap()  && !colorPalette.equals(ColorPalette.GREYSCALE_INV)) {
			AladinLiteWrapper.getInstance().getAladinLite().reverseColorMap();
			hips.setReversedColorMap(false);
		}
    }
    
    public void areaSelectionFinished(){
        view.deToggleSelectionMode();
    }
    
    public void areaSelectionKeyboardShortcutStart(){
        view.areaSelectionKeyboardShortcutStart();
    }


    /** ########### */
    /** SSO SECTION */
    /** ########### */

    protected class SSOOverlayAndPolyline {

        private String name;
        private ESASkySSOObjType type;
        private JavaScriptObject jsPolyline;
        private JavaScriptObject jsOverlay;

        public SSOOverlayAndPolyline(String name, ESASkySSOObjType type,
                JavaScriptObject jsPolyline, JavaScriptObject jsOverlay) {
            super();
            this.name = name;
            this.type = type;
            this.jsPolyline = jsPolyline;
            this.jsOverlay = jsOverlay;
        }

        public String getName() {
            return name;
        }

        public ESASkySSOObjType getType() {
            return type;
        }

        public JavaScriptObject getJsPolyline() {
            return jsPolyline;
        }

        public JavaScriptObject getJsOverlay() {
            return jsOverlay;
        }

    }

    public List<SSOOverlayAndPolyline> getSsoPolyline() {
        if (ssoPolyline == null) {
            ssoPolyline = new LinkedList<SSOOverlayAndPolyline>();
        }
        return ssoPolyline;
    }

    public void hideTooltip() {
       this.view.hideTooltip();
    }
}