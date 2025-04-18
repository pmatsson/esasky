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


import java.util.LinkedList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;


public class ESASkySlider extends FlowPanel {

    private Resources resources;
    private final CssResource style;
    private final String CSS_SLIDER_CONTAINER_ID= "esaSkySliderContainer";
    private final String CSS_SLIDER_ID = "esaSkySlider";
    private final int SLIDERMAX = 1000;
    
    private HTML slider;
    private double minValue;
    private double maxValue;
    private double currentValue;
    private double oldValue;
    private long lastSentGoogleAnalyticsTime = 0;

    /**
     * A ClientBundle that provides images for this widget.
     */
    public static interface Resources extends ClientBundle {

        @Source("esaSkySlider.css")
        @CssResource.NotStrict
        CssResource style();
    }

    public ESASkySlider(double min, double max, int width) {
        this.resources = GWT.create(Resources.class);
        this.style = this.resources.style();
        this.style.ensureInjected();
        this.addStyleName(CSS_SLIDER_CONTAINER_ID);

    	this.minValue = min;
    	this.maxValue = max;
    	this.currentValue = max;
    	initView(width);
    }

    private void initView(int width) {
    	String maxVal = Integer.toString(SLIDERMAX);
    	this.slider = new HTML(
				"<input type=\"range\" min=\"0\" max=\"" + maxVal + "\" "
						+ "value=\"" + maxVal + "\" class=\"" + CSS_SLIDER_ID
						+ " \" >");
    	
    	this.add(slider);
    	addSliderListener(this, slider.getElement());
    }
    
	private native void addSliderListener(ESASkySlider instance, Element slider) /*-{
		slider.oninput = function() {
			instance.@esac.archive.esasky.cl.web.client.view.common.ESASkySlider::fireSliderChangedEvent(D)(this.children[0].value);
		}
	}-*/;
	
	private void setSliderValue(double value) {
		setSliderValue(value, slider.getElement());
	}
	
	private native void setSliderValue(double value, Element slider) /*-{
		slider.children[0].value = value;
	}-*/;
	
	private void fireSliderChangedEvent(double newValue) {
		double scrollPercentage = ((double) newValue) / SLIDERMAX;
		if(scrollPercentage < 0.001 || scrollPercentage > 0.999) {
			scrollPercentage = Math.round(scrollPercentage);
		}
		changeValueFromFraction(scrollPercentage);
	}
    
    private LinkedList<EsaSkySliderObserver> observers = new LinkedList<EsaSkySliderObserver>();
    
    public void registerValueChangeObserver(EsaSkySliderObserver observer) {
 	   observers.add(observer);
    }
    
    private void notifyObservers() {
 	   for(EsaSkySliderObserver observer : observers) {
 		   observer.onValueChange(currentValue);
 	   }
    }
    
    private void changeValueFromFraction(double scrollFraction) {
		double value = (maxValue - minValue) * scrollFraction + minValue;
		setValue(value);
		
		if(System.currentTimeMillis() -lastSentGoogleAnalyticsTime > 1000) {
			GoogleAnalytics.sendEventWithURL(GoogleAnalytics.CAT_SLIDER, GoogleAnalytics.ACT_SLIDER_MOVED, Double.toString(value));
			lastSentGoogleAnalyticsTime = System.currentTimeMillis();
		}
    }

    public void setValue(double value) {
    	oldValue = currentValue;
    	if(value > maxValue) {
    		currentValue = maxValue;
		}else if(value < minValue) {
			currentValue = minValue;
		}else {
			currentValue = value;
		}
    	double newPos = currentValue / (maxValue - minValue) * SLIDERMAX;
    	setSliderValue(newPos);
    	notifyObservers();
    }

	public double getMinValue() {
		return minValue;
	}

	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}

	public double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}

	public double getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(double currentValue) {
		this.currentValue = currentValue;
	}

	public double getOldValue() {
		return oldValue;
	}

	public void setOldValue(double oldValue) {
		this.oldValue = oldValue;
	}

}
