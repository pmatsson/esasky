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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;

import esac.archive.esasky.cl.web.client.model.LineStyle;
import esac.archive.esasky.cl.web.client.model.Shape;
import esac.archive.esasky.cl.web.client.model.SourceShape;
import esac.archive.esasky.cl.web.client.model.SourceShapeType;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;

public class CombinedSourceFootprintDrawer implements IShapeDrawer{
    
	public static final int DEFAULT_LINEWIDTH = 2;
    public static final int DEFAULT_SOURCE_SIZE = 8;
    private static final int MAX_SOURCE_SIZE = 40;
	public static final int MAX_LINEWIDTH = 12;
	private static final double MAX_SECONDARY_SCALE = 10.0;

	private double ratio = DEFAULT_LINEWIDTH / (double) MAX_LINEWIDTH;

    private double secondaryScale = 1.0;
    private boolean showAvgPM = false;
    private boolean useMedianOnAvgPM;
    private JavaScriptObject polyline;
	private JavaScriptObject polylineOverlay;
	private JavaScriptObject sourceOverlay;
	private JavaScriptObject footPrintOverlay;
	private ArrayList<Shape> sourceShapes = new ArrayList<>();
	private ArrayList<Shape> footPrintshapes = new ArrayList<>();
	private ArrayList<Integer[]> allShapesIndexes = new ArrayList<>();
	private ShapeBuilder shapeBuilder;
	private Object shapeType;
	private String lineStyle;
	private String primaryColor;
	private String secondaryColor;


	public CombinedSourceFootprintDrawer(JavaScriptObject sourceOverlay, JavaScriptObject footPrintOverlay , ShapeBuilder shapeBuilder) {
	    this(sourceOverlay, footPrintOverlay, shapeBuilder, SourceShapeType.SQUARE.getName());
	}
	
	public CombinedSourceFootprintDrawer(JavaScriptObject sourceOverlay, JavaScriptObject footPrintOverlay , ShapeBuilder shapeBuilder, Object shapeType) {
		this.sourceOverlay = sourceOverlay;
		this.footPrintOverlay = footPrintOverlay;
		this.shapeBuilder = shapeBuilder;
		this.shapeType = shapeType;
	}

	@Override
	public void setPrimaryColor(String color) {
		this.primaryColor = color;
		AladinLiteWrapper.getAladinLite().setOverlayColor(sourceOverlay, color);
		AladinLiteWrapper.getAladinLite().setOverlayColor(footPrintOverlay, color);
	}

	@Override
	public String getPrimaryColor() {
		return this.primaryColor;
	}

	@Override
	public void setSizeRatio(double size) {
		ratio = size;
		AladinLiteWrapper.getAladinLite().setCatalogSourceSize(sourceOverlay, (int) Math.max(1, ratio * MAX_SOURCE_SIZE));
		AladinLiteWrapper.getAladinLite().setOverlayLineWidth(footPrintOverlay, (int) Math.max(1, ratio * MAX_LINEWIDTH));
	}
	
	@Override
    public double getSize() {
		return ratio;
    }

	@Override
	public void removeAllShapes() {
	    removeAllSourcesAndFootprints();
        AladinLiteWrapper.getAladinLite().removeAllFootprintsFromOverlay(polylineOverlay);
	}
	
	private void removeAllSourcesAndFootprints() {
	    AladinLiteWrapper.getAladinLite().removeAllSourcesFromCatalog(sourceOverlay);
	    AladinLiteWrapper.getAladinLite().removeAllFootprintsFromOverlay(footPrintOverlay);
	    sourceShapes.clear();
	    footPrintshapes.clear();
	    allShapesIndexes.clear();
	}

	@Override
	public void addShapes(GeneralJavaScriptObject rows, GeneralJavaScriptObject metadata) {
	    removeAllSourcesAndFootprints();
		
		GeneralJavaScriptObject [] rowDataArray = GeneralJavaScriptObject.convertToArray(rows);
		for(int i = 0; i < rowDataArray.length; i++) {
			int id = i;
			if(rowDataArray[i].getProperty("id") != null) {
				id = Integer.parseInt(rowDataArray[i].getProperty("id").toString());
			}
			storeShape(shapeBuilder.buildShape(id, null, rowDataArray[i], metadata));
		}
		Log.debug("Added " + rowDataArray.length + " rows and shapes");
		
		if(!sourceShapes.isEmpty()) {
			AladinLiteWrapper.getAladinLite().addCatalogToAladin(sourceOverlay);
		}
		
		for (Shape shape : sourceShapes) {
			AladinLiteWrapper.getAladinLite().newApi_addSourceToCatalogue(sourceOverlay, shape.getJsObject());
		}
		for (Shape shape : footPrintshapes) {
			AladinLiteWrapper.getAladinLite().addFootprintToOverlay(footPrintOverlay, shape.getJsObject());
		}
	}

    private void storeShape(Shape shape) {
        if(shape instanceof SourceShape) {
        	sourceShapes.add(shape);
        	allShapesIndexes.add(new Integer[] {sourceShapes.size()-1, -1});
        } else {
        	footPrintshapes.add(shape);
        	allShapesIndexes.add(new Integer[] {-1, footPrintshapes.size()-1});
        }
    }
    
    public Shape getShape(int shapeId) {
		Integer[] index = allShapesIndexes.get(shapeId);
		
		if(index[0] != -1) {
			return sourceShapes.get(index[0]);
		}else {
			return footPrintshapes.get(index[1]);
		}
	}

    @Override
	public LinkedList<Integer> selectShapes(String shapeName) {
		if(shapeName == null) {
			return null;
		}
		
		LinkedList<Integer> shapeIds = new LinkedList<>();
		
		for(Shape shape : sourceShapes) {
			if(shapeName.equals(shape.getShapeName())){
				AladinLiteWrapper.getAladinLite().selectShape(shape.getJsObject());
				shapeIds.add(shape.getShapeId());
			}
		}
		for(Shape shape : footPrintshapes) {
			if(shapeName.equals(shape.getShapeName())){
				AladinLiteWrapper.getAladinLite().selectShape(shape.getJsObject());
				shapeIds.add(shape.getShapeId());
			}
		}
		return shapeIds;
	}

	@Override
	public LinkedList<Integer>  deselectShapes(String shapeName) {
		if(shapeName == null) {
			return null;
		}
		
		LinkedList<Integer> shapeIds = new LinkedList<>();
		
		for(Shape shape : sourceShapes) {
			if(shapeName.equals(shape.getShapeName())){
				AladinLiteWrapper.getAladinLite().deselectShape(shape.getJsObject());
				shapeIds.add(shape.getShapeId());
			}
		}
		for(Shape shape : footPrintshapes) {
			if(shapeName.equals(shape.getShapeName())){
				AladinLiteWrapper.getAladinLite().deselectShape(shape.getJsObject());
				shapeIds.add(shape.getShapeId());
			}
		}
		return shapeIds;
	}
	
	@Override
	public void selectShapes(int shapeId) {
		if (shapeId < allShapesIndexes.size()) {
			Integer[] index = allShapesIndexes.get(shapeId);

			if (index[0] != -1) {
				AladinLiteWrapper.getAladinLite().selectShape(sourceShapes.get(index[0]).getJsObject());
			} else {
				AladinLiteWrapper.getAladinLite().selectShape(footPrintshapes.get(index[1]).getJsObject());
			}
		}
	}
	
	@Override
	public void deselectShapes(int shapeId) {
		if (shapeId < allShapesIndexes.size()) {
			Integer[] index = allShapesIndexes.get(shapeId);

			if (index[0] != -1) {
				AladinLiteWrapper.getAladinLite().deselectShape(sourceShapes.get(index[0]).getJsObject());
			} else {
				AladinLiteWrapper.getAladinLite().deselectShape(footPrintshapes.get(index[1]).getJsObject());
			}
		}
	}

	@Override
	public void deselectAllShapes() {
		AladinLiteWrapper.getAladinLite().cleanSelectionOnCatalogue(sourceOverlay);
		
		for (Shape currentFootPol : footPrintshapes) {
			AladinLiteWrapper.getAladinLite().deselectShape(currentFootPol.getJsObject());
		}
	}
	

    @Override
    public void hideShape(int shapeId) {
		if (shapeId < allShapesIndexes.size()) {
			Integer[] index = allShapesIndexes.get(shapeId);

			if (index[0] != -1) {
				AladinLiteWrapper.getAladinLite().hideShape(sourceShapes.get(index[0]).getJsObject());
			} else {
				AladinLiteWrapper.getAladinLite().hideShape(footPrintshapes.get(index[1]).getJsObject());
			}
		}
    }
    
    @Override
    public void hideShapes(List<Integer> shapeIds) {
        for(int id : shapeIds) {
        	Integer[] index = allShapesIndexes.get(id);
    		
    		if(index[0] != -1) {
    			AladinLiteWrapper.getAladinLite().hideShape(sourceShapes.get(index[0]).getJsObject());
    		}else {
    			AladinLiteWrapper.getAladinLite().hideShape(footPrintshapes.get(index[1]).getJsObject());
    		}
        }
    }
    
    @Override
    public void hideAllShapes() {
    	for(Integer[] index : allShapesIndexes) {
    		if(index[0] != -1) {
    			AladinLiteWrapper.getAladinLite().hideShape(sourceShapes.get(index[0]).getJsObject());
    		}else {
    			AladinLiteWrapper.getAladinLite().hideShape(footPrintshapes.get(index[1]).getJsObject());
    		}
    	}
    }
    
    @Override
    public void showShape(int shapeId) {
		if (shapeId < allShapesIndexes.size()) {
			Integer[] index = allShapesIndexes.get(shapeId);

			if (index[0] != -1) {
				AladinLiteWrapper.getAladinLite().showShape(sourceShapes.get(index[0]).getJsObject());
			} else {
				AladinLiteWrapper.getAladinLite().showShape(footPrintshapes.get(index[1]).getJsObject());
			}
		}
    }
    
    @Override
    public void showShapes(List<Integer> shapeIds) {
        if(allShapesIndexes.size() == 0) {return;}
        for(int id : shapeIds) {
        	Integer[] index = allShapesIndexes.get(id);
    		
    		if(index[0] != -1) {
    			AladinLiteWrapper.getAladinLite().showShape(sourceShapes.get(index[0]).getJsObject());
    		}else {
    			AladinLiteWrapper.getAladinLite().showShape(footPrintshapes.get(index[1]).getJsObject());
    		}
        }
    }
    
    @Override
    public void showAndHideShapes(List<Integer> shapeIdsToShow, List<Integer> shapeIdsToHide) {
        hideShapes(shapeIdsToHide);
        showShapes(shapeIdsToShow);
    }
    
    
    @Override
	public void hoverStart(int shapeId) {
		if (shapeId < allShapesIndexes.size()) {
			Integer[] index = allShapesIndexes.get(shapeId);
			if(index[0] != -1) {
				AladinLiteWrapper.getAladinLite().hoverStart(sourceShapes.get(index[0]).getJsObject());
			}else {
				AladinLiteWrapper.getAladinLite().hoverStart(footPrintshapes.get(index[1]).getJsObject());
			}
		}
	}
	
	@Override
	public void hoverStop(int shapeId) {
		if (shapeId < allShapesIndexes.size()) {
			Integer[] index = allShapesIndexes.get(shapeId);

			if (index[0] != -1) {
				AladinLiteWrapper.getAladinLite().hoverStop(sourceShapes.get(index[0]).getJsObject());
			} else {
				AladinLiteWrapper.getAladinLite().hoverStop(footPrintshapes.get(index[1]).getJsObject());
			}
		}
	}

	@Override
	public void setShapeBuilder(ShapeBuilder shapeBuilder) {
		this.shapeBuilder = shapeBuilder;
	}
	
	
	@Override
    public String getShapeType() {
        return (sourceShapes.size() != 0) ? this.shapeType.toString() : null;
    }

	@Override
    public void setShapeType(String shapeType) {
        this.shapeType = shapeType;
        AladinLiteWrapper.getAladinLite().setCatalogShape(sourceOverlay, shapeType);
    }

    public void setSecondaryColor(String color) {
    	this.secondaryColor = color;
        AladinLiteWrapper.getAladinLite().setCatalogArrowColor(sourceOverlay, color);
        AladinLiteWrapper.getAladinLite().setOverlayColor(polylineOverlay, color);
    }
    
	@Override
	public String getSecondaryColor() {
		return this.secondaryColor;
	}

    @Override
    public void setLineStyle(String lineStyle) {
    	this.lineStyle = lineStyle;
    	AladinLiteWrapper.getAladinLite().setOverlayLineStyle(footPrintOverlay, lineStyle);
    }
    
    @Override
    public String getLineStyle() {
    	 if(!footPrintshapes.isEmpty()) {
    		 if(lineStyle == null) {
    			 lineStyle = LineStyle.SOLID.getName();
    		 }
    		 return this.lineStyle;
    	 }
    	 
    	 return null;
    }

    // Must return a ratio between 0.01 and 1.0;
    public double getSecondaryScale() {
        return secondaryScale / MAX_SECONDARY_SCALE;
    }

    // Must receive a ratio between 0.01 and 1.0;
    public void setSecondaryScale(double scale) {
        secondaryScale = scale * MAX_SECONDARY_SCALE;
        AladinLiteWrapper.getAladinLite().setCatalogArrowScale(sourceOverlay, secondaryScale);
        AladinLiteWrapper.getAladinLite().setOverlayLineWidth(polylineOverlay, (int)  Math.max(1, secondaryScale));
    }
    
    public void setShowAvgProperMotion(boolean showAvgPM, boolean useMedianOnAvgPM) {
        this.showAvgPM = showAvgPM;
        this.useMedianOnAvgPM = useMedianOnAvgPM;
        AladinLiteWrapper.getAladinLite().setCatalogAvgProperMotion(sourceOverlay, this.showAvgPM, this.useMedianOnAvgPM, true);
    }
    
    public boolean getShowAvgProperMotion() {
        return this.showAvgPM;
    }
    
    public boolean getUseMedianOnAvgProperMotion() {
        return this.useMedianOnAvgPM;
    }
    
    @Override
    public int getNumberOfShapes() {
    	return sourceShapes.size() + footPrintshapes.size();
    }

    
    public void addPolylineOverlay(String esaskyUniqId, double[] polylinePoints, String color) {
        if(this.polylineOverlay == null) {
            this.polylineOverlay = AladinLiteWrapper.getAladinLite().createOverlay(esaskyUniqId, color);
        }
        polyline = AladinLiteWrapper.getInstance().createPolyline(polylinePoints);
        AladinLiteWrapper.getInstance().addPlolyline2SsoOverlay(polylineOverlay, polyline);
    }
}
