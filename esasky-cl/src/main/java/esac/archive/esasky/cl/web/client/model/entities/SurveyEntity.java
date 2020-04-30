package esac.archive.esasky.cl.web.client.model.entities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.Image;

import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.CommonObservationDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.ObservationDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.AladinShape;
import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.model.Shape;
import esac.archive.esasky.cl.web.client.model.ShapeId;
import esac.archive.esasky.cl.web.client.model.SourceShape;
import esac.archive.esasky.cl.web.client.model.SourceShapeType;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.query.TAPSurveyService;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.SurveyConstant;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.cl.web.client.view.resultspanel.ITablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.SurveyTablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorTablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu.StylePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu.StylePanel.StylePanelCallback;;

public class SurveyEntity implements GeneralEntityInterface{

    private String shape;
    
    private ShapeBuilder shapeBuilder = new ShapeBuilder() {
    	
    	@Override
    	public Shape buildShape(int rowId, TapRowList rowList, GeneralJavaScriptObject row) {
    		SourceShape mySource = new SourceShape();
    		mySource.setShapeId(rowId);
    		Map<String, String> details = new HashMap<String, String>();
    		if(Modules.useTabulator) {
    			mySource.setRa(row.invokeFunction("getData").getStringProperty(EsaSkyConstants.OBS_TAP_RA));
    			mySource.setDec(row.invokeFunction("getData").getStringProperty(EsaSkyConstants.OBS_TAP_DEC));
    			details.put(SurveyConstant.SURVEY_NAME, row.invokeFunction("getData").getStringProperty(EsaSkyConstants.OBS_TAP_NAME));
			} else {
	            mySource.setDec((getTAPDataByTAPName(rowList, rowId, EsaSkyConstants.OBS_TAP_DEC))
	                    .toString());
	            mySource.setRa((getTAPDataByTAPName(rowList, rowId, EsaSkyConstants.OBS_TAP_RA))
	                    .toString());
//	            details.put(EsaSkyWebConstants.SOURCE_TYPE,
//	                    EsaSkyWebConstants.SourceType.SURVEY.toString());
	            details.put(SurveyConstant.SURVEY_NAME, (getTAPDataByTAPName(rowList, rowId,
	                    EsaSkyConstants.OBS_TAP_NAME)).toString());
			}
    		details.put(SurveyConstant.CATALOGE_NAME, getEsaSkyUniqId());
    		details.put(SurveyConstant.ID, Integer.toString(rowId));
    		
    		mySource.setJsObject(AladinLiteWrapper.getAladinLite().newApi_createSourceJSObj(
    				mySource.getRa(), mySource.getDec(), details, rowId));
    		
    		return mySource;
    	}
    };
    
    private final ObservationDescriptor descriptor;
    
    private final DefaultEntity defaultEntity;
    private JavaScriptObject overlay;
    
    public SurveyEntity(ObservationDescriptor obsDescriptor, CountStatus countStatus,
    		SkyViewPosition skyViewPosition, String esaSkyUniqId) {
    	this.descriptor = obsDescriptor;
		Map<String, String> catDetails = new HashMap<String, String>();
		catDetails.put("shape", SourceShapeType.CROSS.getName());
		overlay = AladinLiteWrapper.getAladinLite().createCatalogWithDetails(
				esaSkyUniqId, 20, descriptor.getPrimaryColor(), catDetails);
    	IShapeDrawer drawer = new CombinedSourceFootprintDrawer(overlay, AladinLiteWrapper.getAladinLite().createOverlay(esaSkyUniqId,
				descriptor.getPrimaryColor()), shapeBuilder);
        defaultEntity = new DefaultEntity(obsDescriptor, countStatus, skyViewPosition, esaSkyUniqId,
                drawer, TAPSurveyService.getInstance());
    }
    
    public String getShape() {
        return (this.shape != null) ? this.shape : SourceShapeType.CROSS.getName();
    }

    public void setShape(String shape) {
        this.shape = shape;
        AladinLiteWrapper.getAladinLite().setCatalogShape(overlay, shape);
    }
    
//    @Override
    public String getMetadataAdql() {
        return TAPSurveyService.getInstance().getMetadataAdql(getDescriptor());
    }

	@Override
	public CommonObservationDescriptor getDescriptor() {
		return descriptor;
	}

	@Override
	public ITablePanel createTablePanel() {
		if(Modules.useTabulator) {
			return new TabulatorTablePanel(getTabLabel(), getEsaSkyUniqId(), this);
		} else {
			return new SurveyTablePanel(getTabLabel(), getEsaSkyUniqId(), this);
		}
	}

	@Override
	public void setSizeRatio(double size) {
		defaultEntity.setSizeRatio(size);
	}

	@Override
	public double getSize() {
		return defaultEntity.getSize();
	}

	@Override
	public void removeAllShapes() {
		defaultEntity.removeAllShapes();
	}

	@Override
	public void addShapes(TapRowList rowList, GeneralJavaScriptObject javaScriptObject) {
		defaultEntity.addShapes(rowList, javaScriptObject);
	}

	@Override
	public void selectShapes(Set<ShapeId> rowsToSelect) {
		defaultEntity.selectShapes(rowsToSelect);
	}

	@Override
	public void deselectShapes(Set<ShapeId> rowsToDeselect) {
		defaultEntity.deselectShapes(rowsToDeselect);
	}

	@Override
	public void deselectAllShapes() {
		defaultEntity.deselectAllShapes();
	}

	@Override
	public void showShape(int rowId) {
		defaultEntity.showShape(rowId);
	}

	@Override
	public void showShapes(List<Integer> shapeIds) {
		defaultEntity.showShapes(shapeIds);
	}

	@Override
	public void showAndHideShapes(List<Integer> rowIdsToShow, List<Integer> rowIdsToHide) {
		defaultEntity.showAndHideShapes(rowIdsToShow, rowIdsToHide);
	}

	@Override
	public void hideShape(int rowId) {
		defaultEntity.hideShape(rowId);
	}

	@Override
	public void hideShapes(List<Integer> shapeIds) {
		defaultEntity.hideShapes(shapeIds);
	}
	
	@Override
	public void hideAllShapes() {
		defaultEntity.hideAllShapes();
	}

	@Override
	public void setShapeBuilder(ShapeBuilder shapeBuilder) {
		defaultEntity.setShapeBuilder(shapeBuilder);
	}

	@Override
	public void hoverStart(int hoveredRowId) {
		defaultEntity.hoverStart(hoveredRowId);
		
	}

	@Override
	public void hoverStop(int hoveredRowId) {
		defaultEntity.hoverStop(hoveredRowId);
	}

	@Override
	public SkyViewPosition getSkyViewPosition() {
		return defaultEntity.getSkyViewPosition();
	}

	@Override
	public void setSkyViewPosition(SkyViewPosition skyViewPosition) {
		defaultEntity.setSkyViewPosition(skyViewPosition);
	}

	@Override
	public String getHistoLabel() {
		return defaultEntity.getHistoLabel();
	}

	@Override
	public void setHistoLabel(String histoLabel) {
		defaultEntity.setHistoLabel(histoLabel);
	}

	@Override
	public String getEsaSkyUniqId() {
		return defaultEntity.getEsaSkyUniqId();
	}

	@Override
	public void setEsaSkyUniqId(String esaSkyUniqId) {
		defaultEntity.setEsaSkyUniqId(esaSkyUniqId);
	}

	@Override
	public TapRowList getMetadata() {
		return defaultEntity.getMetadata();
	}

	@Override
	public void setMetadata(TapRowList metadata) {
		defaultEntity.setMetadata(metadata);
	}

	@Override
	public Long getLastUpdate() {
		return defaultEntity.getLastUpdate();
	}

	@Override
	public void setLastUpdate(Long lastUpdate) {
		defaultEntity.setLastUpdate(lastUpdate);
	}

	@Override
	public String getTabLabel() {
		return defaultEntity.getTabLabel();
	}

	@Override
	public int getTabNumber() {
		return defaultEntity.getTabNumber();
	}

	@Override
	public void setTabNumber(int number) {
		defaultEntity.setTabNumber(number);
	}

	@Override
	public Image getTypeLogo() {
		return defaultEntity.getTypeLogo();
	}

	@Override
	public Object getTAPDataByTAPName(TapRowList tapRowList, int rowIndex, String tapName) {
		return defaultEntity.getTAPDataByTAPName(tapRowList, rowIndex, tapName);
	}

	@Override
	public Double getDoubleByTAPName(TapRowList tapRowList, int rowIndex, String tapName, Double defaultValue) {
		return defaultEntity.getDoubleByTAPName(tapRowList, rowIndex, tapName, defaultValue);
	}

	@Override
	public CountStatus getCountStatus() {
		return defaultEntity.getCountStatus();
	}

	@Override
	public EntityContext getContext() {
		return defaultEntity.getContext();
	}

	@Override
	public void clearAll() {
		defaultEntity.clearAll();
	}

	@Override
	public String getColor() {
		return defaultEntity.getColor();
	}

	@Override
	public void setPrimaryColor(String color) {
		defaultEntity.setPrimaryColor(color);
	}

	@Override
	public void fetchData() {
		defaultEntity.fetchData();
	}
	
	@Override
	public void fetchDataWithoutMOC() {
		fetchData();
		
	}

	@Override
	public boolean isSampEnabled() {
		return defaultEntity.isSampEnabled();
	}

	@Override
	public boolean isRefreshable() {
		return defaultEntity.isRefreshable();
	}

	@Override
	public boolean hasDownloadableDataProducts() {
		return false;
	}

	@Override
	public boolean isCustomizable() {
		return defaultEntity.isCustomizable();
	}

	@Override
	public void refreshData() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void coneSearch(SkyViewPosition conePos) {
		// TODO Auto-generated method stub		
	}

	@Override
	public StylePanel createStylePanel() {
		return new StylePanel(getEsaSkyUniqId(), getTabLabel(), getColor(), getSize(), getShape(), 
				null, null, null, null, 
				new StylePanelCallback() {
					
					@Override
					public void onShapeSizeChanged(double value) {
						setSizeRatio(value);
					}
					
					@Override
					public void onShapeColorChanged(String color) {
						descriptor.setPrimaryColor(color);
					}
					
					@Override
					public void onShapeChanged(String shape) {
						setShape(shape);
					}
					@Override
					public void onSecondaryShapeScaleChanged(double value) {
					}
					
					@Override
					public void onSecondaryColorChanged(String color) {
					}
					
					@Override
					public void onArrowAvgCheckChanged(boolean checkedOne, boolean checkedTwo) {
					}
				});
	}

    @Override
    public String getShapeType() {
        return defaultEntity.getShapeType();
    }

    @Override
    public void setShapeType(String shapeType) {
        defaultEntity.setShapeType(shapeType);
    }

    @Override
    public void onShapeSelection(AladinShape shape) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onShapeDeselection(AladinShape shape) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onShapeHover(AladinShape shape) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onShapeUnhover(AladinShape shape) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void select() {
        // TODO Auto-generated method stub
        
    }

}