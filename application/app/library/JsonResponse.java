package library;

public class JsonResponse {
	public String error = null;

	public void setError(String error) {
		this.error = error;
	}

	public void send() {
		throw new JsonIframeResponse(this);
	}
}
