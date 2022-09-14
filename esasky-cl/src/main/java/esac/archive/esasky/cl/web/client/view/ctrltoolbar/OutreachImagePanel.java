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
import esac.archive.esasky.cl.web.client.utility.DeviceUtils;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.*;
import esac.archive.esasky.cl.web.client.view.resultspanel.ITablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.TableObserver;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.descriptor.BaseDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.ImageDescriptor;

import java.util.Objects;
import java.util.Optional;

public class OutreachImagePanel extends MovableResizablePanel<OutreachImagePanel> {

	private BaseDescriptor outreachImageDescriptor;
	private ImageListEntity imageEntity;
	private boolean isHidingFootprints = false;
	private static String outreachImageIdToBeOpened; 
	
	private FlowPanel opacityPanel;
	private final EsaSkySwitch hideFootprintsSwitch = new EsaSkySwitch("outreachImagePanel__hideFootprintsSwitch", false, TextMgr.getInstance().getText("outreachImage_hideFootprints"), "");
	private final FlowPanel mainContainer = new FlowPanel();
	private final FlowPanel tableContainer = new FlowPanel();
	private PopupHeader<OutreachImagePanel> header;

	private final Resources resources;
	private CssResource style;

	public static interface Resources extends ClientBundle {
		@Source("outreachImagePanel.css")
		@CssResource.NotStrict
		CssResource style();
	}
	
	public OutreachImagePanel() {
		super(GoogleAnalytics.CAT_OUTREACHIMAGES, false);
		this.resources = GWT.create(Resources.class);
		this.style = this.resources.style();
		this.style.ensureInjected();

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
		}) );
		MainLayoutPanel.addMainAreaResizeHandler(event -> setDefaultSize());
	}
	
	@Override
	protected void onLoad() {
		super.onLoad();
		if(outreachImageIdToBeOpened != null) {
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
		if(outreachImageDescriptor == null) {
			if(DescriptorRepository.getInstance().getImageDescriptors() != null) {
				fetchData();
			} else {
				DescriptorRepository.getInstance().setOutreachImageCountObserver(newCount -> fetchData());
			}
		}
	}
	public void show() {
		super.show();
		getData();
		if(imageEntity != null && !DeviceUtils.isMobileOrTablet()) {
			imageEntity.setIsPanelClosed(false);
		}
	}


	public void close() {
		if(imageEntity != null && !DeviceUtils.isMobileOrTablet()) {
			imageEntity.setIsPanelClosed(true);
		}

		super.hide();
	}


	private void fetchData() {
		if(outreachImageDescriptor != null) {
			return;
		}

		Optional<ImageDescriptor> optionalImageDescriptor = DescriptorRepository.getInstance().getImageDescriptors()
				.getDescriptors().stream().filter(ImageDescriptor::isHst).findFirst();

		if (optionalImageDescriptor.isPresent()) {
			outreachImageDescriptor = optionalImageDescriptor.get();
		} else {
			return;
		}

		imageEntity = EntityRepository.getInstance().createImageListEntity(outreachImageDescriptor);
		if(outreachImageIdToBeOpened != null) {
			imageEntity.setIdToBeOpened(outreachImageIdToBeOpened);
		}
		tableContainer.add(imageEntity.createTablePanel().getWidget());
		imageEntity.fetchData();
		setMaxSize();
	}
	
	private void initView() {
		this.getElement().addClassName("outreachImagePanel");

		header = new PopupHeader<>(this, TextMgr.getInstance().getText("outreachImagePanel_header"),
				TextMgr.getInstance().getText("outreachImagePanel_helpText"),
				TextMgr.getInstance().getText("outreachImagePanel_helpTitle"),
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
		
        hideFootprintsSwitch.addStyleName("outreachImagePanel__footprintSwitch");
        hideFootprintsSwitch.addClickHandler(event ->
        {
        	isHidingFootprints = !isHidingFootprints;
        	hideFootprintsSwitch.setChecked(isHidingFootprints);
    		imageEntity.setIsHidingShapes(isHidingFootprints);
		});
		header.addActionWidget(hideFootprintsSwitch);
		mainContainer.add(header);
		mainContainer.add(tableContainer);
        mainContainer.getElement().setId("outreachImagePanelContainer");
		this.add(mainContainer);
	}
	
	@Override
	public void setMaxSize() {
		Style elementStyle = mainContainer.getElement().getStyle();
		int maxWidth = MainLayoutPanel.getMainAreaWidth() + MainLayoutPanel.getMainAreaAbsoluteLeft() - getAbsoluteLeft() - 15;
		elementStyle.setPropertyPx("maxWidth", maxWidth);
		elementStyle.setPropertyPx("maxHeight", MainLayoutPanel.getMainAreaHeight() + MainLayoutPanel.getMainAreaAbsoluteTop() - getAbsoluteTop() - 15);
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
    
    public static void setStartupId(String id) {
    	outreachImageIdToBeOpened = id;
    }


	public JSONArray getAllImageIds(ICommand command) {
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
						command.onResult(imageEntity.getIds());
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
			return imageEntity.getIds();
		}

		return null;
	}

	public void selectShape(String id) {
		if(imageEntity != null) {
			imageEntity.selectShape(id);
		} else {
			OutreachImagePanel.setStartupId(id);
		}

		if (!isShowing()) {
			show();
		}
	}


}
