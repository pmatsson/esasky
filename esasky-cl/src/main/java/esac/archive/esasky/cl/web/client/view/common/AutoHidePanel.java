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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;

public class AutoHidePanel extends FlowPanel{
	private boolean isShowing;
	
	public AutoHidePanel() {
		this.getElement().getStyle().setPosition(Position.ABSOLUTE);
	}
	
	public void show() {
		MainLayoutPanel.addElementToMainArea(this);
		isShowing = true;
		updateHandlersEsaSky();
	}

	public void hide() {
		MainLayoutPanel.removeElementFromMainArea(this);
		isShowing = false;
		updateHandlersEsaSky();
	}
	
	public boolean isShowing() {
		return isShowing;
	}
	
	protected void setWidget(Widget widget) {
		this.add(widget);
	}
	
	public void setPosition(int left, int top) {
		getElement().getStyle().setTop(top, Unit.PX);
		getElement().getStyle().setLeft(left, Unit.PX);
	}
	
	
	/*Auto hide functionality adapted from source code of
	 * GWT PopupPanel
	 * */
	private void previewNativeEventEsasky(NativePreviewEvent event) {
		// If the event has been canceled or consumed, ignore it
		if (event.isCanceled() || (event.isConsumed())) {
			// We need to ensure that we cancel the event even if its been consumed so
			// that popups lower on the stack do not auto hide
			return;
		}

		// If the event targets the popup or the partner, consume it
		Event nativeEvent = Event.as(event.getNativeEvent());
		boolean eventTargetsPopupOrPartner = eventTargetsPopupEsaSky(nativeEvent)
				|| eventTargetsPartnerEsaSky(nativeEvent);

		// Switch on the event type
		int type = nativeEvent.getTypeInt();
		switch (type) {
		case Event.ONMOUSEDOWN:
		case Event.ONTOUCHSTART:
			// Don't eat events if event capture is enabled, as this can
			// interfere with dialog dragging, for example.
			if (DOM.getCaptureElement() != null) {
				event.consume();
				return;
			}

			if (!eventTargetsPopupOrPartner) {
				hide();
				return;
			}
			break;
		}
	}
	private boolean eventTargetsPopupEsaSky(NativeEvent event) {
		EventTarget target = event.getEventTarget();
		if (Element.is(target)) {
			return getElement().isOrHasChild(Element.as(target));
		}
		return false;
	}
	private boolean eventTargetsPartnerEsaSky(NativeEvent event) {
		if (autoHidePartners == null) {
			return false;
		}

		EventTarget target = event.getEventTarget();
		if (Element.is(target)) {
			for (Element elem : autoHidePartners) {
				if (elem.isOrHasChild(Element.as(target))) {
					return true;
				}
			}
		}
		return false;
	}
	private HandlerRegistration nativePreviewHandlerRegistration;

	private void updateHandlersEsaSky() {
		// Remove any existing handlers.
		if (nativePreviewHandlerRegistration != null) {
			nativePreviewHandlerRegistration.removeHandler();
			nativePreviewHandlerRegistration = null;
		}

		// Create handlers if showing.
		if (isShowing) {
			nativePreviewHandlerRegistration = Event.addNativePreviewHandler(new NativePreviewHandler() {
				public void onPreviewNativeEvent(NativePreviewEvent event) {
					previewNativeEventEsasky(event);
				}
			});
		}
	}

	private List<Element> autoHidePartners;
	/**
	 * Mouse events that occur within an autoHide partner will not hide a panel
	 * set to autoHide.
	 *
	 * @param partner the auto hide partner to add
	 */
	public void addAutoHidePartner(Element partner) {
		assert partner != null : "partner cannot be null";
		if (autoHidePartners == null) {
			autoHidePartners = new ArrayList<Element>();
		}
		autoHidePartners.add(partner);
	}
	
}
