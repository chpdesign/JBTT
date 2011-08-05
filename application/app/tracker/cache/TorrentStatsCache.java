package tracker.cache;

import org.apache.jcs.access.exception.CacheException;
import tracker.torrents.TorrentStats;

import java.util.*;

public class TorrentStatsCache extends AbstractCache<TorrentStats> {
	public String getRegion() { return "torrentstats"; }

	public TorrentStats getByTorrentId(Long id) throws Throwable {
		TorrentStats value;
		if (this.isEnabled()) {
			try {
				value = (TorrentStats)this.getCacheAccess().get("torrentId-" + id);
			} catch (ClassCastException classCastException) {
				value = null;
			}
			if (value == null) {
				value = TorrentStats.byTorrentId(id);
				if (value != null) {
					this.put(value);
				}
			}
		} else {
			value = TorrentStats.byTorrentId(id);
		}
		return value;
	}

	public List<TorrentStats> getByMultipleIds(List<Long> itemIds) throws Throwable {
		List<TorrentStats> values;

		if (this.isEnabled()) {
			Map<Integer, TorrentStats> valuesMap = new TreeMap<Integer, TorrentStats>();

			Map<Long, Integer> requestItemIds = new HashMap<Long, Integer>();

			for (Integer pos = 0; pos < itemIds.size(); pos++) {
				Long itemId = itemIds.get(pos);

				TorrentStats value;

				try {
					value = (TorrentStats)this.getCacheAccess().get("torrentId-" + itemId);
				} catch (ClassCastException classCastException) {
					value = null;
				}

				if (value != null) {
					valuesMap.put(pos, value);
				} else {
					requestItemIds.put(itemId, pos);
				}
			}

			List<TorrentStats> requestedValues = TorrentStats.byMultipleTorrentIds(
					new ArrayList<Long>(requestItemIds.keySet())
			);

			for (TorrentStats requestedValue : requestedValues) {
				this.put(requestedValue);

				Integer pos = requestItemIds.get(requestedValue.getTorrentId());
				valuesMap.put(pos, requestedValue);
			}

			values = new ArrayList<TorrentStats>(valuesMap.values());
		} else {
			values = TorrentStats.byMultipleTorrentIds(itemIds);
		}

		return values;
	}

	private static volatile TorrentStatsCache instance;

	public static TorrentStatsCache getInstance() throws CacheException {
		if (instance == null) {
			synchronized (TorrentStatsCache.class) {
				if (instance == null) {
					instance = new TorrentStatsCache();
				}
			}
		}
		return instance;
	}

	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
}
