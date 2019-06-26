package esac.archive.esasky.cl.web.client.callback;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Response;

import esac.archive.ammi.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.ammi.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.cl.web.client.model.DynamicCountObject;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;
import esac.archive.esasky.cl.web.client.utility.JSONUtils;

public class CountRequestCallback extends JsonRequestCallback {
    
    private IDescriptor descriptor;
    private CountStatus countStatus;
    private long timecall;

    public CountRequestCallback(IDescriptor descriptor, CountStatus countStatus,
            ICountRequestHandler countRequestHandler, String url) {
        super(countRequestHandler.getProgressIndicatorMessage(), url);
        this.descriptor = descriptor;
        this.countStatus = countStatus;
        timecall = System.currentTimeMillis();
    }

    @Override
    protected void onSuccess(final Response response) {
        if(countStatus.getUpdateTime(descriptor.getMission()) != null 
        		&& countStatus.getUpdateTime(descriptor.getMission()) > timecall) {
        	Log.warn(this.getClass().getSimpleName() + " discarded server answer with timecall="
        			+ timecall + " , dif:" + (countStatus.getUpdateTime(descriptor.getMission()) - timecall));
        	return;
        }
        
        countStatus.setUpdateTime(descriptor.getMission(), timecall);
        Log.debug(this.getClass().getSimpleName() + " RESPONSE for " + descriptor.getMission()
                + " : " + response.getText());
        JsArray<JavaScriptObject> array = JSONUtils.evalJsonGetData("(" + response.getText() + ")");

        Log.debug(this.getClass().getSimpleName() + " [" + array.length() + "] results found");

        // / PARSE DYNAMIC COUNT RESPONSE
        DynamicCountObject count = JsonUtils.<DynamicCountObject> safeEval(array.get(0).toString());

        int value = count.getCount();
		SkyViewPosition skyViewPosition = CoordinateUtils.getCenterCoordinateInJ2000();

        countStatus.setCountDetails(descriptor.getMission(), value, System.currentTimeMillis(),
        		skyViewPosition);
        Log.debug(this.getClass().getSimpleName() + " " + descriptor.getMission() + ": [" + value
                + "] results found");
        countStatus.updateCount();
    }
}