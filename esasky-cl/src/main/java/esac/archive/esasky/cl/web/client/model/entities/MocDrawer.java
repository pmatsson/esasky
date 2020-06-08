package esac.archive.esasky.cl.web.client.model.entities;

import java.util.List;

import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

public class MocDrawer implements IShapeDrawer{

	public static final int DEFAULT_LINEWIDTH = 2;
    public static final int DEFAULT_SOURCE_SIZE = 8;
	public static final int MAX_LINEWIDTH = 12;

	private double ratio = DEFAULT_LINEWIDTH / MAX_LINEWIDTH;
	GeneralJavaScriptObject moc;


	public MocDrawer(String color) {
		String options = "{\"opacity\":0.2, \"color\":\"" + color + "\"}";
		moc = (GeneralJavaScriptObject) AladinLiteWrapper.getAladinLite().createMOC(options);
	}

	@Override
	public void setPrimaryColor(String color) {
		AladinLiteWrapper.getAladinLite().setOverlayColor(moc, color);
	}

	@Override
	public void setSizeRatio(double size) {
		ratio = size;
		AladinLiteWrapper.getAladinLite().setMOCOpacity(moc, Math.min(1, ratio));
	}
	
	@Override
    public double getSize() {
		return ratio;
    }

	@Override
	public void removeAllShapes() {
		if(moc != null) {
			AladinLiteWrapper.getAladinLite().clearMOC(moc);
			AladinLiteWrapper.getAladinLite().removeMOC(moc);
		}
	}

	@Override
	public void addShapes(GeneralJavaScriptObject javaScriptObject) {
	}
	public void addShapes(TapRowList rowList) {
		removeAllShapes();
		
		int healpixOrderIndex = rowList.getColumnIndex(EsaSkyConstants.HEALPIX_ORDER);
		int healpixIndex = rowList.getColumnIndex(EsaSkyConstants.HEALPIX_IPIX);
		if(healpixIndex != -1) {
			String healpixOrder = "";
			boolean first = true;
			String mocJSON = "{";
			for(int i = 0; i < rowList.getData().size(); i++) {
				String currentOrder = rowList.getDataRow(i).get(healpixOrderIndex).toString();
				if(currentOrder != healpixOrder) {
					healpixOrder = currentOrder;
					if(!first) {
						mocJSON += "], ";
					}
					mocJSON += "\"" + healpixOrder + "\":[";
					first = false;
				}
				mocJSON += rowList.getDataRow(i).get(healpixIndex) + ",";
			}
			mocJSON = mocJSON.substring(0,mocJSON.length()-1) + "]}";
			AladinLiteWrapper.getAladinLite().addMOCData(moc, mocJSON);
			AladinLiteWrapper.getAladinLite().addMOC(moc);
		}
	}
	
	public GeneralJavaScriptObject getOverlay() {
		return moc;
	}
	
	@Override
	public void selectShapes(int shapeId) {
		//TODO
	}
	
	@Override
	public void deselectShapes(int shapeId) {
		//TODO
	}

	@Override
	public void deselectAllShapes() {
		//TODO
	}

    @Override
    public void hideShape(int shapeId) {
		//TODO
    }
    
    @Override
    public void hideShapes(List<Integer> shapeIds) {
		//TODO
    }

    @Override
    public void hideAllShapes() {
    	//TODO
    }
    
    @Override
    public void showShape(int shapeId) {
		//TODO
    }
    
    @Override
    public void showShapes(List<Integer> shapeIds) {
		//TODO
    }
    
    @Override
    public void showAndHideShapes(List<Integer> shapeIdsToShow, List<Integer> shapeIdsToHide) {
		//TODO
    }
    
    @Override
	public void hoverStart(int shapeId) {
		//TODO
	}
	
	@Override
	public void hoverStop(int shapeId) {
		//TODO
	}

	@Override
	public void setShapeBuilder(ShapeBuilder shapeBuilder) {
		//TODO
	}

    @Override
    public String getShapeType() {
        return null;
    }

    @Override
    public void setShapeType(String shapeType) {
    }

}
