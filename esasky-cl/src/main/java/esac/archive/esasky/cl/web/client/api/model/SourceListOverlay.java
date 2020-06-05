package esac.archive.esasky.cl.web.client.api.model;

import java.util.LinkedList;
import java.util.List;

import esac.archive.esasky.cl.web.client.api.DefaultValues;

public class SourceListOverlay implements IOverlay {

	String catalogueName;
	String cooframe;
	String color;
	Integer lineWidth;
	List<GeneralSkyObject> skyObjectList = new LinkedList<GeneralSkyObject>();

	@Override
	public String getOverlayName() {
		return catalogueName;
	}

	@Override
	public void setOverlayName(String catalogueName) {
		this.catalogueName = catalogueName;
		if ("".equals(catalogueName) || null == catalogueName) {
			this.catalogueName = DefaultValues.CATALOGUE_DEFAULT_NAME;
		}
	}

	@Override
	public String getCooframe() {
		return cooframe;
	}

	@Override
	public void setCooframe(String cooframe) {
		this.cooframe = cooframe;
	}

	@Override
	public String getColor() {
		return color;
	}

	@Override
	public void setColor(String color) {
		this.color = color;
		if ("".equals(color) || null == color) {
			this.color = DefaultValues.CATALOGUE_DEFAULT_COLOR;
		}
	}

	@Override
	public List<GeneralSkyObject> getSkyObjectList() {
		return skyObjectList;
	}

	@Override
	public void setSkyObjectList(List<GeneralSkyObject> skyObjects) {
		this.skyObjectList = skyObjects;
	}

	@Override
	public int getLineWidth() {
		return lineWidth;
	}

	@Override
	public void setLineWidth(Integer lineWidth) {
		this.lineWidth = lineWidth;
		if (null == lineWidth) {
			this.lineWidth = DefaultValues.CATALOGUE_DEFAULT_LINEWIDTH;
		}
	}

}