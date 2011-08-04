package library;

import com.google.gson.Gson;
import play.exceptions.UnexpectedException;
import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.mvc.results.Result;

/**
 * 200 OK with text/plain
 */
public class JsonIframeResponse extends Result {
    protected String json = null;

    public JsonIframeResponse(Object jsonObject) {
		this.json = new Gson().toJson(jsonObject);
    }

    public void apply(Request request, Response response) {
        try {
            // this.setContentTypeIfNotSet(response, "application/json; charset=utf-8");
			this.setContentTypeIfNotSet(response, "text/plain; charset=utf-8"); // TODO: temporary fix for iframe requests
            response.out.write(json.getBytes("utf-8"));
        } catch (Exception e) {
            throw new UnexpectedException(e);
        }
    }
}
