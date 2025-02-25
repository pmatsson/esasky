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

package esac.archive.esasky.cl.web.client.event.banner;

import com.google.gwt.event.shared.GwtEvent;

public class ToggleServerProblemBannerEvent extends GwtEvent<ToggleServerProblemBannerEventHandler> {

    public final static Type<ToggleServerProblemBannerEventHandler> TYPE = new Type<ToggleServerProblemBannerEventHandler>();

    public ToggleServerProblemBannerEvent() {
    }

    @Override
    public final Type<ToggleServerProblemBannerEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final ToggleServerProblemBannerEventHandler handler) {
        handler.onEvent(this);
    }

}
