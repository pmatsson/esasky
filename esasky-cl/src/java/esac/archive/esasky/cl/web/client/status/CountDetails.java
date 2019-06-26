package esac.archive.esasky.cl.web.client.status;

import esac.archive.ammi.ifcs.model.coordinatesutils.SkyViewPosition;

public class CountDetails {

    private Integer count;
    private Long updateTime;
    private SkyViewPosition skyViewPosition;

    public CountDetails(Integer count) {
        super();
        this.count = count;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

	public SkyViewPosition getSkyViewPosition() {
		return skyViewPosition;
	}

	public void setSkyViewPosition(SkyViewPosition skyViewPosition) {
		this.skyViewPosition = skyViewPosition;
	}
}
