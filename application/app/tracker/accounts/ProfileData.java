package tracker.accounts;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class ProfileData {
	protected final String json;
	protected Boolean dataParsed = false;
	protected Boolean dataChanged = false;

	protected String icq = null;
	protected String jabber = null;
	protected String skype = null;

	protected Integer gender = 0;

	public ProfileData() { this(""); }

	public ProfileData(String json) { this.json = json; }

	protected synchronized void parseJson() {
		if (this.dataParsed || this.json == null || this.json.isEmpty()) {
			return;
		}

		this.dataParsed = true;

		JsonParser parser = new JsonParser();
		JsonObject entries = (JsonObject)parser.parse(this.json);

		if (entries.has("icq")) {
			this.icq = entries.get("icq").getAsString();
		}

		if (entries.has("jabber")) {
			this.jabber = entries.get("jabber").getAsString();
		}

		if (entries.has("skype")) {
			this.skype = entries.get("skype").getAsString();
		}

		if (entries.has("gender")) {
			this.gender = Integer.parseInt(entries.get("gender").getAsString());
		}
	}

	public String toString() {
		if (!this.dataChanged) {
			return this.json;
		}

		JsonObject fields = new JsonObject();

		if (this.icq != null) {
			fields.add("icq", new JsonPrimitive(this.icq));
		}

		if (this.jabber != null) {
			fields.add("jabber", new JsonPrimitive(this.jabber));
		}

		if (this.skype != null) {
			fields.add("skype", new JsonPrimitive(this.skype));
		}

		if (this.gender != null) {
			fields.add("gender", new JsonPrimitive(this.gender.toString()));
		}

		return fields.toString();
	}

	public String getIcq() { this.parseJson(); return this.icq; }
	public void setIcq(String icq) { this.parseJson(); this.dataChanged = true; this.icq = icq; }

	public String getJabber() { this.parseJson(); return this.jabber; }
	public void setJabber(String jabber) { this.parseJson(); this.dataChanged = true; this.jabber = jabber; }

	public String getSkype() { this.parseJson(); return this.skype; }
	public void setSkype(String skype) { this.parseJson(); this.dataChanged = true; this.skype = skype; }

	public Integer getGender() { this.parseJson(); return this.gender; }
	public void setGender(Integer gender) { this.parseJson(); this.dataChanged = true; this.gender = gender; }
}
