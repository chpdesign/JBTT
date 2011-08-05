package tracker.cache;

import org.apache.jcs.access.exception.CacheException;
import tracker.torrents.Torrent;
import tracker.util.Utils;

import java.util.*;

public class TorrentsCache extends AbstractCache<Torrent> {
	public String getRegion() { return "torrents"; }

	public Torrent getById(Long id) throws Throwable {
		Torrent value;
		if (this.isEnabled()) {
			try {
				value = (Torrent)this.getCacheAccess().get("id-" + id);
			} catch (ClassCastException classCastException) {
				value = null;
			}
			if (value == null) {
				value = Torrent.byId(id);
				if (value != null) {
					this.put(value);
				}
			}
		} else {
			value = Torrent.byId(id);
		}
		return value;
	}

	public List<Torrent> getByMultipleIds(List<Long> itemIds) throws Throwable {
		List<Torrent> values;

		if (this.isEnabled()) {
			Map<Integer, Torrent> valuesMap = new TreeMap<Integer, Torrent>();

			Map<Long, Integer> requestItemIds = new HashMap<Long, Integer>();

			for (Integer pos = 0; pos < itemIds.size(); pos++) {
				Long itemId = itemIds.get(pos);

				Torrent value;

				try {
					value = (Torrent)this.getCacheAccess().get("id-" + itemId);
				} catch (ClassCastException classCastException) {
					value = null;
				}

				if (value != null) {
					valuesMap.put(pos, value);
				} else {
					requestItemIds.put(itemId, pos);
				}
			}

			List<Torrent> requestedValues = Torrent.byMultipleIds(
					new ArrayList<Long>(requestItemIds.keySet())
			);

			for (Torrent requestedValue : requestedValues) {
				this.put(requestedValue);

				Integer pos = requestItemIds.get(requestedValue.getId());
				valuesMap.put(pos, requestedValue);
			}

			values = new ArrayList<Torrent>(valuesMap.values());
		} else {
			values = Torrent.byMultipleIds(itemIds);
		}

		return values;
	}

	public Torrent getByInfoHash(byte[] infoHash) throws Throwable {
		Torrent value;
		if (this.isEnabled()) {
			try {
				value = (Torrent)this.getCacheAccess().get("infoHash-" + Utils.getHexString(infoHash));
			} catch (ClassCastException classCastException) {
				value = null;
			}
			if (value == null) {
				value = Torrent.byInfoHash(infoHash);
				if (value != null) {
					this.put(value);
				}
			}
		} else {
			value = Torrent.byInfoHash(infoHash);
		}
		return value;
	}

	private static volatile TorrentsCache instance;

	public static TorrentsCache getInstance() throws CacheException {
		if (instance == null) {
			synchronized (TorrentsCache.class) {
				if (instance == null) {
					instance = new TorrentsCache();
				}
			}
		}
		return instance;
	}

	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
}
