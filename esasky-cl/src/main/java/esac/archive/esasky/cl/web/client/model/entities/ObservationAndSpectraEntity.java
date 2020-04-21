package esac.archive.esasky.cl.web.client.model.entities;

import com.allen_sauer.gwt.log.client.Log;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.CommonObservationDescriptor;
import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.model.PolygonShape;
import esac.archive.esasky.cl.web.client.model.Shape;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.query.TAPObservationService;
import esac.archive.esasky.cl.web.client.status.CountObserver;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.DeviceUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.view.resultspanel.CommonObservationsTablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.GeneralJavaScriptObject;
import esac.archive.esasky.cl.web.client.view.resultspanel.ITablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorTablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu.StylePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu.StylePanel.StylePanelCallback;;

public abstract class ObservationAndSpectraEntity extends CommonObservationEntity {

    private CommonObservationDescriptor descriptor;
    private MOCEntity mocEntity;

	
	public class MocBuilder implements ShapeBuilder{

		@Override
		public Shape buildShape(int rowId, TapRowList rowList, GeneralJavaScriptObject row) {
			PolygonShape shape = new PolygonShape();
			if(Modules.useTabulator) {
				shape.setStcs(row.invokeFunction("getData").getStringProperty(getDescriptor().getMocSTCSColumn()));
			} else {
		    	shape.setStcs((String)getTAPDataByTAPName(rowList, rowId, descriptor.getMocSTCSColumn()));
			}
			shape.setJsObject(AladinLiteWrapper.getAladinLite().createFootprintFromSTCS(shape.getStcs()));
			return shape;
		}
	}
    
    public ObservationAndSpectraEntity(CommonObservationDescriptor obsDescriptor,
            CountStatus countStatus, SkyViewPosition skyViewPosition, String esaSkyUniqObsId) {
        super(obsDescriptor, countStatus, skyViewPosition, esaSkyUniqObsId);
        this.descriptor = obsDescriptor;
        this.mocEntity = new MOCEntity(obsDescriptor, countStatus, this, defaultEntity);
    }

    public CommonObservationDescriptor getDescriptor() {
    	return descriptor;
    }

    @Override
    public String getMetadataAdql() {
        return TAPObservationService.getInstance().getMetadataAdql(getDescriptor());
    }
    
    @Override
    public void fetchData(final ITablePanel tablePanel) {
        
    	if(!getCountStatus().hasMoved(descriptor.getMission())) {
        	fetchData2(tablePanel);
        	
        } else {
        	
	        getCountStatus().registerObserver(new CountObserver() {
				@Override
				public void onCountUpdate(int newCount) {
					fetchData2(tablePanel);				
					getCountStatus().unregisterObserver(this);
				}
			});
        }
    }
    
    private void fetchData2(ITablePanel tablePanel) {
    	int mocLimit = descriptor.getShapeLimit();
    	int count = getCountStatus().getCount(descriptor.getMission());
    	
    	if (DeviceUtils.isMobile()){
    		mocLimit = EsaSkyWebConstants.MAX_SHAPES_FOR_MOBILE;
    	}
    	
    	if (mocLimit > 0 && count > mocLimit) {
    		//defaultEntity.setShapeBuilder(new MocBuilder());
    		//getMocMetadata(tablePanel);
    		//defaultEntity.fetchHeaders(tablePanel);
    		mocEntity.setTablePanel(tablePanel);
    		mocEntity.refreshMOC();
    	}else {
    		defaultEntity.setShapeBuilder(shapeBuilder);
    		defaultEntity.fetchData(tablePanel);
    	}
    }
    

    
    private void getMocMetadata(final ITablePanel tablePanel) {
        Log.debug("[getMocMetadata][" + descriptor.toString() + "]");

//        tablePanel.clearTable();
//        String adql = .getInstance().getMetadataAdql(getDescriptor());
//        
//        String url = TAPUtils.getTAPQuery(URL.encodeQueryString(adql), EsaSkyConstants.JSON);
//
//        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
//        try {
//            builder.sendRequest(null,
//                new MetadataCallback(tablePanel, adql, TextMgr.getInstance().getText("JsonRequestCallback_retrievingMOC"), new OnComplete() {
//                	
//                	@Override
//                	public void onComplete() {
//                		tablePanel.setEmptyTable(TextMgr.getInstance().getText("commonObservationTablePanel_showingGlobalSkyCoverage"));
//                	}
//                }));
//        } catch (RequestException e) {
//            Log.error(e.getMessage());
//            Log.error("[getMocMetadata] Error fetching JSON data from server");
//        }

        tablePanel.setADQLQueryUrl("");
    }

	@Override
	public ITablePanel createTablePanel() {
		if(Modules.useTabulator) {
//			return new CommonObservationsTabulatorTablePanel(getTabLabel(), getEsaSkyUniqId(), this);
			return new TabulatorTablePanel(getTabLabel(), getEsaSkyUniqId(), this);
		} else {
			return new CommonObservationsTablePanel(getTabLabel(), getEsaSkyUniqId(), this);
		}
	}
	
	@Override
	public StylePanel createStylePanel() {
		return new StylePanel(getEsaSkyUniqId(), getTabLabel(), getColor(), getSize(), 
				null, null, null, null, null, 
				new StylePanelCallback() {
					
					@Override
					public void onShapeSizeChanged(double value) {
						setSizeRatio(value);
					}
					
					@Override
					public void onShapeColorChanged(String color) {
						getDescriptor().setPrimaryColor(color);
					}
					
					@Override
					public void onShapeChanged(String shape) {
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
}