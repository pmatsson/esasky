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

package esac.archive.esasky.cl.web.client.view.common;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.*;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import esac.archive.esasky.cl.web.client.model.Size;
import esac.archive.esasky.cl.web.client.utility.DeviceUtils;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.ClosingObserver;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MovablePanel extends FocusPanel {

    public interface OnKeyPress {void onEscapeKey();}

    private static final int MIN_DISTANCE_FROM_TOP = 30;
    private static final int SNAPPING_DISTANCE = 15;
    private static final int MIN_BOTTOM_DISTANCE = 2;

    private boolean isMouseDown = false;
	private boolean isBeingDragged = false;
	private final String googleEventCategory;
	private int positionOnPanelTop;
	private int positionOnPanelLeft;
	private boolean hasBeenMovedByUser = false;
	private int userSetPositionTop;
	private int userSetPositionLeft;

	private boolean isSuggestedPositionCenter;
	private boolean updateZIndex = true;
	private int suggestedPositionTop;
	private int suggestedPositionLeft;

	private boolean isSnappingEnabled = true;

	private Element moveInitiatorElement;
	private HandlerRegistration nativePreviewHandlerRegistration;
	protected FlowPanel movableContainer = new FlowPanel();
	protected List<ClosingObserver> observers = new LinkedList<>();

	private OnKeyPress onKeyPress = () -> {
	};

	private static int highestZIndex = 1000;

	public MovablePanel(String googleEventCategory, boolean isSuggestedPositionCenter, boolean updateZIndex) {
		this(googleEventCategory, isSuggestedPositionCenter);
		this.updateZIndex = updateZIndex;
	}

	public MovablePanel(String googleEventCategory, boolean isSuggestedPositionCenter) {
		super();
		this.googleEventCategory = googleEventCategory;
		this.isSuggestedPositionCenter = isSuggestedPositionCenter;
		DOM.sinkEvents(getElement(), Event.ONMOUSEDOWN | Event.ONMOUSEMOVE
				| Event.ONMOUSEUP | Event.ONMOUSEOVER | Event.ONTOUCHSTART | Event.ONTOUCHMOVE | Event.KEYEVENTS);
		DOM.sinkEvents(RootPanel.get().getElement(), Event.ONMOUSEUP | Event.ONTOUCHEND | Event.ONTOUCHCANCEL);

		MainLayoutPanel.addMainAreaResizeHandler(event -> {
			ensureDialogFitsInsideWindow();
			if(MovablePanel.this.isSuggestedPositionCenter) {
				setSuggestedPositionCenter();
			}
		});
		super.add(movableContainer);
        setFocus(true);
    }

	@Override
	public void add(Widget w) {
		movableContainer.add(w);
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		if(isSuggestedPositionCenter) {
			setSuggestedPositionCenter();
		}
		updateZIndex();
		updateHandlers();
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		for(ClosingObserver onClose : observers) {
			onClose.onClose();
		}
		if (nativePreviewHandlerRegistration != null) {
			nativePreviewHandlerRegistration.removeHandler();
			nativePreviewHandlerRegistration = null;
		}
	}

	public void addHideOnEscapeKeyBehavior(OnKeyPress onKeyPress) {
	    this.onKeyPress = onKeyPress;
	}

	@Override
	public void onBrowserEvent(Event event) {
		final int eventType = DOM.eventGetType(event);

		if (Event.ONKEYUP == eventType && event.getKeyCode() == KeyCodes.KEY_ESCAPE) {
		    onKeyPress.onEscapeKey();
		    return;
		}
		if (Event.ONMOUSEOVER == eventType) {
			if (elementCanStartDragOperation) {
				getElement().getStyle().setProperty("cursor", "move");
			} else {
				getElement().getStyle().setProperty("cursor", "auto");
			}
		}
		if (Event.ONMOUSEDOWN == eventType || Event.ONTOUCHSTART == eventType) {
			if (!isMouseDown && elementCanStartDragOperation) {
				isMouseDown = true;
				DOM.setCapture(getElement());
				positionOnPanelTop = eventType == Event.ONMOUSEDOWN ? event.getClientY() + MainLayoutPanel.getMainAreaAbsoluteTop() - getAbsoluteTop()
						: event.getTargetTouches().get(0).getClientY() + MainLayoutPanel.getMainAreaAbsoluteTop() - getAbsoluteTop();
				positionOnPanelLeft = eventType == Event.ONMOUSEDOWN ? event.getClientX() + MainLayoutPanel.getMainAreaAbsoluteLeft() - getAbsoluteLeft()
						: event.getTargetTouches().get(0).getClientX() + MainLayoutPanel.getMainAreaAbsoluteLeft() - getAbsoluteLeft();

				updateZIndex();
			}
		} else if (Event.ONMOUSEMOVE == eventType || Event.ONTOUCHMOVE == eventType) {
			if (isMouseDown) {
				isBeingDragged = true;
				hasBeenMovedByUser = true;
				getElement().getStyle().setProperty("cursor", "move");

				int positionY = eventType == Event.ONMOUSEMOVE ? event.getClientY() : event.getTargetTouches().get(0).getClientY();
				int top = positionY - positionOnPanelTop;
				userSetPositionTop = top;

				int positionX = eventType == Event.ONMOUSEMOVE ? event.getClientX() : event.getTargetTouches().get(0).getClientX();
				int left = positionX - positionOnPanelLeft;
				userSetPositionLeft = left;

				setPosition(left, top);
			}
		} else if (Event.ONMOUSEUP == eventType || Event.ONTOUCHEND == eventType || Event.ONTOUCHCANCEL== eventType) {
			DOM.releaseCapture(getElement());
			if (isBeingDragged) {
				GoogleAnalytics.sendEvent(googleEventCategory, GoogleAnalytics.ACT_MOVED, "");
			}
			isMouseDown = false;
		}
	}

	public void updateZIndex() {
		if (updateZIndex) {
			int currentZIndexNumber = 0;
			String currentZIndex = getElement().getStyle().getZIndex();
			if(currentZIndex != null && !currentZIndex.isEmpty()) {
				currentZIndexNumber = Integer.valueOf(currentZIndex);
			}
			if(currentZIndexNumber < highestZIndex) {
				highestZIndex++;
				currentZIndexNumber = highestZIndex;
				getElement().getStyle().setZIndex(currentZIndexNumber);
			} else {
				highestZIndex = currentZIndexNumber;
			}
		}
	}

	public void setSuggestedPosition(int left, int top) {
		isSuggestedPositionCenter = false;
		suggestedPositionLeft = left;
		suggestedPositionTop = top;
		if(!hasBeenMovedByUser()) {
			setPosition(suggestedPositionLeft, suggestedPositionTop);
		}
	}

	public void setSuggestedPositionCenter() {
		isSuggestedPositionCenter = true;
		Scheduler.get().scheduleFinally(this::setCenterPosition);
		setCenterPosition();
	}

	private void setCenterPosition() {
	    suggestedPositionLeft = MainLayoutPanel.getMainAreaWidth() / 2 - getOffsetWidth() / 2;
	    suggestedPositionTop = MainLayoutPanel.getMainAreaHeight() / 2 - getOffsetHeight() / 2;
	    if(!hasBeenMovedByUser()) {
	        setPosition(suggestedPositionLeft, suggestedPositionTop);
	    }
	}

	private void setPosition(int left, int top) {
		int snappingDistance = isSnappingEnabled ? SNAPPING_DISTANCE : MIN_BOTTOM_DISTANCE;
		
		if(top < MIN_DISTANCE_FROM_TOP) {
			top = MIN_DISTANCE_FROM_TOP;
		}
		if(top + snappingDistance > MainLayoutPanel.getMainAreaHeight() - getOffsetHeight()) {
			top = MainLayoutPanel.getMainAreaHeight() - getOffsetHeight();
			if(top < MIN_DISTANCE_FROM_TOP) {
				top = MIN_DISTANCE_FROM_TOP;
			}
		}
		setTop(top);
	
		if(left < 0) {
			left = 0;
		}
		if(left + snappingDistance > MainLayoutPanel.getMainAreaWidth() - getOffsetWidth()) {
			left = MainLayoutPanel.getMainAreaWidth() - getOffsetWidth();
		}
		setLeft(left);
	}

	private boolean hasBeenMovedByUser() {
		return hasBeenMovedByUser;
	}

	public void ensureDialogFitsInsideWindow() {
		setMaxSize();
		if(hasBeenMovedByUser()) {
			int left = userSetPositionLeft;
			if(userSetPositionLeft + getOffsetWidth() > MainLayoutPanel.getMainAreaWidth()) {
				left = MainLayoutPanel.getMainAreaWidth() - getOffsetWidth();
			}
			int top = userSetPositionTop;
			if(userSetPositionTop + getOffsetHeight() > MainLayoutPanel.getMainAreaHeight()) {
				top = MainLayoutPanel.getMainAreaHeight() - getOffsetHeight();
			}
			setPosition(left, top);
		} else {
			if(isSuggestedPositionCenter) {
				setSuggestedPositionCenter();
			}

			int left = suggestedPositionLeft;
			if(suggestedPositionLeft + getOffsetWidth() > MainLayoutPanel.getMainAreaWidth()) {
				left = MainLayoutPanel.getMainAreaWidth() - getOffsetWidth();
			}
			int top = suggestedPositionTop;
			if(suggestedPositionTop + getOffsetHeight() > MainLayoutPanel.getMainAreaHeight()) {
				top = MainLayoutPanel.getMainAreaHeight() - getOffsetHeight();
			}

			setPosition(left, top);
		}
	}

	public void setMaxSize() {
		getElement().getStyle().setPropertyPx("maxHeight", MainLayoutPanel.getMainAreaHeight() - 30);
		getElement().getStyle().setPropertyPx("maxWidth", MainLayoutPanel.getMainAreaWidth() - 2);
	}

	private void setLeft(int left) {
		getElement().getStyle().setLeft(left, Unit.PX);
	}

	private void setTop(int top) {
		getElement().getStyle().setTop(top, Unit.PX);
	}

	public void definePositionFromTopAndLeft() {
		if(hasBeenMovedByUser) {
			setPosition(userSetPositionLeft, userSetPositionTop);
		} else {
			setPosition(suggestedPositionLeft, suggestedPositionTop);
		}
	}

	protected Size getDefaultSize() {
		int width = 610;
		int height = MainLayoutPanel.getMainAreaHeight();

		if (MainLayoutPanel.getMainAreaWidth() < 1500) {
			width = 500;
		}
		if (MainLayoutPanel.getMainAreaWidth() < 1100) {
			width = 350;
		}
		if (MainLayoutPanel.getMainAreaWidth() < 450) {
			height = 300;
		}
		if (height > 400) {
			height = 400;
		}
		if (!DeviceUtils.isMobileOrTablet() && height > MainLayoutPanel.getMainAreaHeight() / 2) {
			height = MainLayoutPanel.getMainAreaHeight() / 2;
		}
		if (height > MainLayoutPanel.getMainAreaHeight() - 30 - 2) {
			height = MainLayoutPanel.getMainAreaHeight() - 30 - 2;
		}
		if (width > MainLayoutPanel.getMainAreaWidth()) {
			width = MainLayoutPanel.getMainAreaWidth() - 2;
		}

		return new Size(width, height);
	}

	public void setSnapping(boolean isSnappingEnabled) {
		this.isSnappingEnabled = isSnappingEnabled;
	}

/*
 * Below code implemented to add the possibility to mark certain
 * elements inside the MovablePanel as unable to initiate move operation
 * Original code adapted from source code of gwt PopupPanel
 * */
	protected void previewNativeEvent(NativePreviewEvent event) {
		// If the event has been canceled or consumed, ignore it
		if (event.isCanceled() || (event.isConsumed())) {
			// We need to ensure that we cancel the event even if its been consumed so
			// that popups lower on the stack do not auto hide
			return;
		}

		// If the event targets the popup or the partner, consume it
		Event nativeEvent = Event.as(event.getNativeEvent());
		boolean eventTargetsPopupOrPartner = eventTargetsPopup(nativeEvent)
				|| !eventTargetsPartner(nativeEvent);
		if (eventTargetsPopupOrPartner) {
			event.consume();
		}

		// Switch on the event type
		if (previewNativeEvent(nativeEvent)) {
			// Don't eat events if event capture is enabled, as this can
			// interfere with dialog dragging, for example.
			if (DOM.getCaptureElement() != null) {
				event.consume();
				return;
			}

			if (!eventTargetsPopupOrPartner) {
				return;
			}

			elementCanStartDragOperation = eventTargetsPopup(nativeEvent) && eventTargetsPartner(nativeEvent);
		}
	}

	// Event types to preview
	private Boolean previewNativeEvent(Event event) {
		int type = event.getTypeInt();
		switch (type) {
			case Event.ONMOUSEDOWN:
			case Event.ONTOUCHSTART:
			case Event.ONMOUSEOVER:
			case Event.ONMOUSEOUT:
			case Event.ONMOUSEMOVE:
				return true;
			default:
				return false;
		}
	}

	private boolean elementCanStartDragOperation = false;

	private boolean eventTargetsPopup(NativeEvent event) {
		EventTarget target = event.getEventTarget();
		if (Element.is(target)) {
			return getElement().isOrHasChild(Element.as(target));
		}
		return false;
	}
	private boolean eventTargetsPartner(NativeEvent event) {
		if (elementsNotInitiatingMoveOperations == null && moveInitiatorElement == null) {
			return true;
		}

		EventTarget target = event.getEventTarget();
		if (Element.is(target)) {
			if (moveInitiatorElement != null) {
				return moveInitiatorElement.isOrHasChild(Element.as(target));
			}

			for (Element elem : elementsNotInitiatingMoveOperations) {
				if (elem.isOrHasChild(Element.as(target))) {
					return false;
				}
			}
		}

		return true;
	}

	protected void updateHandlers() {
		removeHandlers();
		nativePreviewHandlerRegistration = Event.addNativePreviewHandler(this::previewNativeEvent);
	}

	protected void removeHandlers() {
		// Remove any existing handlers.
		if (nativePreviewHandlerRegistration != null) {
			nativePreviewHandlerRegistration.removeHandler();
			nativePreviewHandlerRegistration = null;
		}
	}

	private List<Element> elementsNotInitiatingMoveOperations;

	/**
	 * Mouse events that occur within an the partner element will not
	 * initiate a move operation
	 */
	public void addElementNotAbleToInitiateMoveOperation(Element partner) {
		assert partner != null : "partner cannot be null";
		if (elementsNotInitiatingMoveOperations == null) {
			elementsNotInitiatingMoveOperations = new ArrayList<Element>();
		}
		elementsNotInitiatingMoveOperations.add(partner);
	}
	public void addElementNotAbleToInitiateMoveOperation(String id) {
		addElementNotAbleToInitiateMoveOperation(Document.get().getElementById(id));
	}

	/**
	 * Mouse events that occur within the partner element will be the ONLY
	 * element that can initiate a move operation
	 */
	public void addSingleElementAbleToInitiateMoveOperation(Element partner) {
		if (partner == null) {
			throw new IllegalArgumentException("partner cannot be null");
		}

		this.moveInitiatorElement = partner;
	}
	
	public void registerCloseObserver(ClosingObserver onClose) {
		observers.add(onClose);
	}
	
	public void unregisterCloseObserver(ClosingObserver onClose) {
		observers.remove(onClose);
	}
}
