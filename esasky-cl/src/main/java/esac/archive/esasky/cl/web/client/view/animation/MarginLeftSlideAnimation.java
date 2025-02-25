/*
ESASky
Copyright (C) 2025 Henrik Norman

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

package esac.archive.esasky.cl.web.client.view.animation;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;

public class MarginLeftSlideAnimation extends EsaSkyAnimation {

    private final Element element;
    
    public MarginLeftSlideAnimation(Element element)
    {
        this.element = element;
    }
 
    @Override
	protected Double getCurrentPosition() {
		String marginLeftString = element.getStyle().getMarginLeft();
		if (marginLeftString.equals("")){
			marginLeftString = "0px";
		}
		//remove suffix "px"
		marginLeftString = marginLeftString.substring(0, marginLeftString.length()-2);
		Double currentPosition = new Double(marginLeftString);
		return currentPosition;
	}
    
    @Override
	protected void setCurrentPosition(double newPosition){
        this.element.getStyle().setMarginLeft(newPosition, Style.Unit.PX);
	}
}
