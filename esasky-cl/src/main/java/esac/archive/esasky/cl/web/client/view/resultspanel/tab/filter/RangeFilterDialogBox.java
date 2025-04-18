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

package esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter;


import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.dom.client.Element;

import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.FilterObserver;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.SignButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.SignButton.SignType;

public class RangeFilterDialogBox extends FilterDialogBox {
    
    private final Resources resources = GWT.create(Resources.class);
    private CssResource style;
    
    private FilterObserver filterObserver;
    private JavaScriptObject slider;
    private boolean hasSliderBeenAddedToDialogBox = false;
    private boolean reRenderingWouldTakeSignificantTime;
    
    private double minValue = Double.NEGATIVE_INFINITY;
    private double maxValue = Double.POSITIVE_INFINITY;
    private double currentLow = minValue;
    private double currentHigh = maxValue;
    private double range;
    private double precision;
    private double stepSize;
    
    private final int SLIDER_MAX = 10000;
    private double currentSliderFromFraction = 0;
    private double currentSliderToFraction = 1.0 * SLIDER_MAX;
    
    private TextBox fromTextBox = new TextBox();
    private TextBox toTextBox = new TextBox();
    
    private final String sliderSelectorContainerId;
    private final String rangeFilterContainerId;
    
    protected FilterTimer filterTimer = new FilterTimer();
	private ValueFormatter valueFormatter;
    
    public interface Resources extends ClientBundle {
        @Source("rangeFilterDialogBox.css")
        @CssResource.NotStrict
        CssResource style();
        
		@Source("reset.png")
		ImageResource resetIcon();
    }
    
	public RangeFilterDialogBox(String tapName, String columnName, ValueFormatter valueFormatter,
	        final String filterButtonId, final FilterObserver filterObserver) {
		super(tapName, filterButtonId);
        this.style = this.resources.style();
        this.style.ensureInjected();
        this.valueFormatter = valueFormatter;
        rangeFilterContainerId = filterButtonId.replaceAll("(\\(|\\)| )", "_") + "rangeColumn";
        sliderSelectorContainerId = "selectorId_WTIH_NO_TITLE_" + rangeFilterContainerId;
        this.filterObserver = filterObserver;
        
        HTML columnNameHTML = new HTML(columnName.replaceAll("_", " "));
        columnNameHTML.addStyleName("filterColumnName");
        
        SignButton minimizeButton = new SignButton(SignType.MINUS);
        minimizeButton.addStyleName("filterButton__minimize");
        minimizeButton.setSmallStyle();
        minimizeButton.addClickHandler(new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                hide();
            }
        });

        fromTextBox.addStyleName("sliderTextBox");
        fromTextBox.addBlurHandler(new BlurHandler() {
			
			@Override
			public void onBlur(BlurEvent event) {
				onChangeFromTextBox();
			}
		});
        
        fromTextBox.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					onChangeFromTextBox();
				}
			}
		});
        addElementNotAbleToInitiateMoveOperation(fromTextBox.getElement());
        toTextBox.addBlurHandler(new BlurHandler() {
        	
        	@Override
        	public void onBlur(BlurEvent event) {
        		onChangeFromTextBox();
        	}
        });
        toTextBox.addKeyUpHandler(new KeyUpHandler() {
        	
        	@Override
        	public void onKeyUp(KeyUpEvent event) {
        		if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
        			onChangeFromTextBox();
        		}
        	}
        });
        
        toTextBox.addStyleName("sliderTextBox");
        addElementNotAbleToInitiateMoveOperation(toTextBox.getElement());
        
        FlowPanel container = new FlowPanel();
        container.add(columnNameHTML);
        container.add(minimizeButton);
		container.add(fromTextBox);
        
		EsaSkyButton resetButton = new EsaSkyButton(this.resources.resetIcon());
		resetButton.setTitle(TextMgr.getInstance().getText("rangeFilter_resetFilter"));
		resetButton.addStyleName("resetRangeFilterButton");
		resetButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				currentSliderFromFraction = 0;
				currentSliderToFraction = SLIDER_MAX;
				setSliderFraction(slider, 0, SLIDER_MAX);
				setTextBoxValues(minValue, maxValue);
				filterTimer.setNewRange(minValue, maxValue);
			}
		});
		container.add(resetButton);
		container.add(toTextBox);
		
		container.getElement().setId(rangeFilterContainerId);
		setWidget(container);
		
		addStyleName("rangeFilterDialogBox");
		
	}
	
	@Override
	public void show() {
		super.show();
		if(!hasSliderBeenAddedToDialogBox) {
			hasSliderBeenAddedToDialogBox = true;
			stepSize = 1;
			slider = createSliderFilter(this, rangeFilterContainerId, sliderSelectorContainerId, 0, SLIDER_MAX, stepSize);
			setTextBoxValues(minValue, maxValue);
    		setSliderValues(currentLow, currentHigh);
			addElementNotAbleToInitiateMoveOperation("slider-" + sliderSelectorContainerId);
		}
	}

	@Override
	public boolean isFilterActive() {
		if(hasSliderBeenAddedToDialogBox) {
			return (currentSliderFromFraction > 0 || currentSliderToFraction < SLIDER_MAX)
					&& 	!(Double.isInfinite(minValue) || Double.isInfinite(maxValue));
		}
		 return currentLow > Double.NEGATIVE_INFINITY || currentHigh < Double.POSITIVE_INFINITY;
	}
	
    private native JavaScriptObject createSliderFilter(RangeFilterDialogBox instance, String containerId, String sliderSelectorId, int minValue, int maxValue, double fixedStep) /*-{
	    var sliderSelector = $wnd.createSliderSelector(sliderSelectorId,
	                                      "",
	                                      minValue,
	                                      maxValue,
	                                      $entry(function (selector) {
	                                      	instance.@esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter.RangeFilterDialogBox::fireRangeChangedEvent(DDI)(selector.fromValue, selector.toValue, selector.movedHandle);
	                                      	}),
	                                      20,
	                                      fixedStep);
		$wnd.$("#" + containerId).append(sliderSelector.$html);
		return sliderSelector;
    }-*/;

    private void fireRangeChangedEvent(double fromValue, double toValue, int movedHandle) {
    	currentSliderFromFraction = fromValue;
    	currentSliderToFraction = toValue;
    	
    	if(movedHandle == 0) {
    	    fromValue = minValue + fromValue * range / SLIDER_MAX;
    	    fromTextBox.setText(valueFormatter.formatValue(fromValue));
            toValue = valueFormatter.getValueFromFormat(toTextBox.getText());
    	} else {
    	    fromValue = valueFormatter.getValueFromFormat(fromTextBox.getText());
    	    toValue = minValue + toValue * range / SLIDER_MAX;
    	    toTextBox.setText(valueFormatter.formatValue(toValue));
    	}
    	
    	filterTimer.setNewRange(fromValue, toValue);
    }
    
    private void setTextBoxValues(double fromValue, double toValue) {
		if(Double.isNaN(fromValue) || Double.isNaN(toValue)) {
			return;
		}
    	fromTextBox.setText(valueFormatter.formatValue(fromValue));
    	toTextBox.setText(valueFormatter.formatValue(toValue));
    }
    
    public void changeFormatter(ValueFormatter formatter) {
        this.valueFormatter = formatter;
    }
    
    public void setRange(Double minValue, Double maxValue, int precision){
    	range = Math.abs(maxValue - minValue);
    	this.precision = precision;
    	
    	boolean filterWasActive = isFilterActive();
    	
    	if(!filterWasActive) {
    		if(currentLow <= Double.NEGATIVE_INFINITY) {
    			this.currentLow = minValue;
    		}
    		if(currentHigh >= Double.POSITIVE_INFINITY) {
    			this.currentHigh = maxValue;
    		}
    	}
    	this.minValue = minValue;
    	this.maxValue = maxValue;
    	if(hasSliderBeenAddedToDialogBox) {
    		if(currentLow < minValue) {
    			currentLow = minValue;
    		}
    		if(currentHigh > maxValue) {
    			currentHigh = maxValue;
    		}
    		if(filterWasActive) {
    			setSliderValues(currentLow, currentHigh);
    		}
    		setTextBoxValues(currentLow, currentHigh);
    	}else {
    		filterTimer.setNewRange(currentLow, currentHigh);
    	}
    	ensureCorrectFilterButtonStyle();
    };
    
    @Override
    public void setValuesFromString(String filterString) {
		String[] andSplit = filterString.split(" AND ");
		String lowerString = null;
		String upperString = null;
		if(andSplit.length > 1) {
			lowerString = andSplit[0];
			upperString = andSplit[1];
		}else if(andSplit[0].contains(">")) {
			lowerString = andSplit[0];
		}else {
			upperString = andSplit[0];
		}
		
		Double minValue = new Double(this.currentLow);
		Double maxValue = new Double(this.currentHigh);
		if(lowerString != null) {
			minValue = Double.parseDouble(lowerString.split(">=")[1]);
		}
		if(upperString != null) {
			maxValue = Double.parseDouble(upperString.split("<=")[1]);
		}
		
		setValues(minValue, maxValue);
    }
    
    public void setValues(Double minValue, Double maxValue) {
    	if(minValue != null) {
    		if(minValue > this.minValue) {
    			this.currentLow = minValue;
    		}
    	}
    	if(maxValue != null) {
    		if(maxValue < this.maxValue) {
    			this.currentHigh = maxValue;
    		}
    	}
    	if(hasSliderBeenAddedToDialogBox) {
    		setSliderValues(currentLow, currentHigh);
    		setTextBoxValues(currentLow, currentHigh);
    	}
    	filterTimer.setNewRange(currentLow, currentHigh);
    	
    }
    
    private void onChangeFromTextBox() {
    	try {
    		double fromValue = valueFormatter.getValueFromFormat(fromTextBox.getText());
    		double toValue = valueFormatter.getValueFromFormat(toTextBox.getText());
    		if(toValue < fromValue) {
    			double temp = fromValue;
    			fromValue = toValue;
    			toValue = temp;
    		}
    		
    		if(fromValue < minValue) {
    			fromValue = minValue;
    		}
    		if(toValue > maxValue) {
    			toValue = maxValue;
    		}
    		
    		setSliderValues(fromValue, toValue);
    		
    		setTextBoxValues(fromValue, toValue);
    		filterTimer.setNewRange(fromValue, toValue);
    		filterTimer.run();
    		
    	} catch (NumberFormatException exception) {
    		setTextBoxValues(currentLow, currentHigh);
    	}
    }
    
    public double getCurrentLow() {
    	return currentLow;
    }
    
    public double getCurrentHigh() {
    	return currentHigh;
    }
    
	private void setSliderValues(double currentLow, double currentHigh) {
		currentSliderFromFraction = (currentLow - minValue) / range * SLIDER_MAX;
		currentSliderToFraction = (currentHigh - minValue) / range * SLIDER_MAX;
		setSliderFraction(slider, currentSliderFromFraction, currentSliderToFraction);
    }
    
    private native void setSliderFraction(JavaScriptObject slider, double lowerHandleFraction, double higherHandleFraction) /*-{
    	slider.setValues(lowerHandleFraction, higherHandleFraction);
    }-*/;
    
    public void setReRenderingWouldTakeSignificantTime(boolean wouldTakeSignificantTime) {
    	reRenderingWouldTakeSignificantTime = wouldTakeSignificantTime;
    }
    
	protected class FilterTimer extends Timer{
		
		private double lastLow = currentLow;
		private double lastHigh = currentHigh;
		
		@Override
		public void run() {
			if(isUserStillDragging() && reRenderingWouldTakeSignificantTime) {
				schedule(200);
				return;
			}
			if(!(Math.abs(lastLow - currentLow) < range * 10e-6 && Math.abs(lastHigh - currentHigh) < range * 10e-6)) {
				lastLow = currentLow;
				lastHigh = currentHigh;
				if(isFilterActive()) {
                   String filter = "";
                    if(currentLow > minValue) {
                        filter += Double.toString(currentLow);
                    }
                    filter += ",";
					if(currentHigh < maxValue) {
						filter += Double.toString(currentHigh);
					}
					
					filterObserver.onNewFilter(filter);
				}else {
					filterObserver.onNewFilter("");
				}
			}
		}
		
		public void setNewRange(double low, double high) {
			currentLow = low;
			currentHigh = high;
			ensureCorrectFilterButtonStyle();
			schedule(500);
		}
		
		private boolean isUserStillDragging() {
			if(!hasSliderBeenAddedToDialogBox) {
				return false;
			}
			Element sliderSelector = Document.get().getElementById("slider-" + sliderSelectorContainerId);
			
			Element sliderSelectorChild = sliderSelector.getFirstChildElement();
			do {
				if(isActiveHandle(sliderSelectorChild.getClassName())) {
					return true;
				}
				sliderSelectorChild = sliderSelectorChild.getNextSiblingElement();
			} while(sliderSelectorChild != null);
			
			return false;
		}
		
		private boolean isActiveHandle(String className) {
			return className.contains("ui-slider-handle") && className.contains("ui-state-active");
		}
	}
}
