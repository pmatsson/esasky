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

package esac.archive.esasky.cl.wcstransform.module.footprintbuilder;

import com.allen_sauer.gwt.log.client.Log;

import esac.archive.esasky.cl.wcstransform.module.utility.Constants.PlanningMission;

/**
 * @author Fabrizio Giordano Copyright (c) 2016 - European Space Agency
 */

public class STCSGeneratorFactory {

    public static STCSAbstractGenerator getSTCSGenerator(String mission) {
        Log.debug("FACTORY " + mission);
        if (PlanningMission.JWST.getMissionName().equals(mission)) {
            return new JWSTSiafToSTCSGenerator(mission);
        } else if (PlanningMission.XMM.getMissionName().equals(mission)) {
            return new DS9ToSTCSGenerator(mission);
        }
        return null;
    }
}
