package tracker.announcer;

public enum Event {
	EMPTY(),
	STARTED("started"),
	STOPPED("stopped"),
	COMPLETED("completed");

	private final String key;

	Event() {
		this(null);
	}

	Event(String key) {
		this.key = key;
	}

	public String getKey() {
		return this.key;
	}

	public static Event byKey(String key) {
		if (key == null) {
			return EMPTY;
		} else if(key.equalsIgnoreCase("started")) {
			return STARTED;
		} else if(key.equalsIgnoreCase("stopped")) {
			return STOPPED;
		} else if(key.equalsIgnoreCase("completed")) {
			return COMPLETED;
		} else {
			return null;
		}
	}
}