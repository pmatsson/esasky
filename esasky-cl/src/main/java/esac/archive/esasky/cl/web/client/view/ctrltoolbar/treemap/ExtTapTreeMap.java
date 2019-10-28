package esac.archive.esasky.cl.web.client.view.ctrltoolbar.treemap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.moxieapps.gwt.highcharts.client.Credits;
import org.moxieapps.gwt.highcharts.client.Point;
import org.moxieapps.gwt.highcharts.client.ToolTip;
import org.moxieapps.gwt.highcharts.client.ToolTipData;
import org.moxieapps.gwt.highcharts.client.ToolTipFormatter;

import esac.archive.esasky.cl.web.client.model.entities.EntityContext;
import esac.archive.esasky.ifcs.model.descriptor.ColorChangeObserver;
import esac.archive.esasky.ifcs.model.descriptor.ExtTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

public class ExtTapTreeMap extends TreeMap {

	private EntityContext context;
    private HashMap<String, Point> allPointMap = new HashMap<>();

	
	public ExtTapTreeMap(final EntityContext context) {
		super(context);
		this.context = context;
	}
	
	@Override
	protected void setTapCredits() {
    	setCredits(new Credits().setEnabled(false))
        .setToolTip(new ToolTip().setFormatter(new ToolTipFormatter() {

            @Override
            public String format(ToolTipData toolTipData) {
                Point point = toolTipData.getPoint();

                if (point.getText() != null && allPoints.containsKey(point.getText())) {
                    PointInformation pointInformation = allPoints.get(point.getText());
                    String tooltipText = pointInformation.longName;
                   
                    if(pointInformation.credits != null && !pointInformation.credits.isEmpty()) {
                    	 tooltipText += " [" + pointInformation.credits.replace("U+00F8", "\u00F8") + "]";
                    }
                    return "<div style=\"font-size: 12px !important;\">" + tooltipText + "</div>";
                }

                return point.getName();
            }

        })).setChartTitle(null).setBackgroundColor("rgba(0, 0, 0, 0.65)").setMargin(1, 0, 0, 1);
    }
	
    private void cleanChildren(ExtTapDescriptor parent) {
    		for(PointInformation pointInformation : allPoints.values()) {
    			
    			ExtTapDescriptor childDesc = (ExtTapDescriptor) pointInformation.descriptor;
				
    			if( childDesc.getParent() == parent) {
    					Point child = getPoint(childDesc);
    					child.update(0,false);
    					cleanChildren(childDesc);
				}
    		}
    }
    
    @Override
    public void addData(final List<IDescriptor> descriptors, List<Integer> counts) {
        
        if ((descriptors.size() > 0)
                && (descriptors.size() == counts.size())) {
            
            List<Integer> zeroCountList = new ArrayList<Integer>();
            
            for (int i = 0; i < descriptors.size(); i ++) {
            	
            	cleanChildren((ExtTapDescriptor) descriptors.get(i));            	
                if (counts.get(i) > 0) {
                    //Only add descriptors with non zero count
                	final IDescriptor descriptor = descriptors.get(i);
            		addPoints(descriptor, counts.get(i), false);
                } else {
                    //Store this index for later removing
                    zeroCountList.add(i);
                }
            }
            
            for (int i : zeroCountList) {
                addPoints(descriptors.get(i), counts.get(i), false);
            }
            
            update();
        }
    }
    
    private Point getPoint2(IDescriptor descriptor) {
    	if(allPointMap.containsKey(descriptor.getGuiLongName())){
    		return allPointMap.get(descriptor.getGuiLongName());
    	}
    	return null;
    }
    
    private void addPoints(IDescriptor descriptor, int count, boolean updateView) {
        String pointId = null;

        PointInformation pointInformation = new PointInformation(descriptor.getGuiLongName(),
                descriptor.getMission(), descriptor.getCreditedInstitutions(), count, descriptor, context);
        boolean found = false;
        if (isRendered()) {
            Point point = getPoint(descriptor);
            if(point != null) {
            	pointId = point.getText();
            	if (count == 0) {
            		removePoint(point, updateView);
            	} else {
            		point.update(logCount(count), updateView);
            	}
            	found = true;
            }
            
            if (!found) {
                
                pointId = descriptor.generateId();
                final Point newPoint = getNewPoint (pointId, descriptor, descriptor.getHistoColor(), pointInformation, logCount(count));

            	ExtTapDescriptor desc = (ExtTapDescriptor) descriptor;
            	if(desc.getTreeMapType() == EsaSkyConstants.TREEMAP_TYPE_SUBCOLLECTION || desc.getTreeMapType() == EsaSkyConstants.TREEMAP_TYPE_DATAPRODUCT) {
                    Point parentPoint = getPoint2(desc.getParent());
                    newPoint.setParent(parentPoint);
            	}
                
                series.addPoint(newPoint, false, false, false);
                allPointMap.put(descriptor.getGuiLongName(), newPoint);
                
                if (updateView) {
                    update();
                }
                
        		descriptor.registerColorChangeObservers(new ColorChangeObserver() {
					@Override
					public void onColorChange(IDescriptor descriptor, String newColor) {
						setPointColor(descriptor, newColor);
					}
				});
            }
            
        } else {
            for (Point point : pointsToAdd) {
                if (isMatch(descriptor, point)) {
                    pointId = point.getText();
                    pointsToAdd.remove(point);
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                pointId = descriptor.generateId();
                
        		descriptor.registerColorChangeObservers(new ColorChangeObserver() {
					
					@Override
					public void onColorChange(IDescriptor descriptor, String newColor) {
						setPointColor(descriptor, newColor);
					}
				});
            }
            
            final Point newPoint = getNewPoint (pointId, descriptor, descriptor.getHistoColor(), pointInformation, logCount(count));
            
            pointsToAdd.add(newPoint);
            addPointsOnNextRender = true;
        }

        allPoints.put(pointId, pointInformation);

        if (count > 0) {
            removeGhostPoint();
        } else {
            makeSureGhostPointIsInGraph(pointId);
        }
    }
    
    private void update() {
    	String id = getIdOfSelectedLevel(series.getNativeSeries());
        series.update(this.series,isRendered());
        zoomToPoint(series.getNativeSeries(), id);
    }
    
}