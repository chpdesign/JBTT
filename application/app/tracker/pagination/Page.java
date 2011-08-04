package tracker.pagination;

import tracker.cache.AbstractPageSet;
import tracker.cache.ICache;

import java.util.ArrayList;
import java.util.List;

public abstract class Page<ItemType> implements ICache {
	protected AbstractPageSet pageSet;
	protected Integer number;
	protected List<Long> itemIds;

//	protected List<ItemType> items = null;

	public Page(AbstractPageSet pageSet) {
		this.pageSet = pageSet;
		this.itemIds = new ArrayList<Long>();
	}

	public Page(AbstractPageSet pageSet, Integer number, List<Long> itemIds) {
		this.pageSet = pageSet;
		this.number = number;
		this.itemIds = itemIds;
	}

	public AbstractPageSet getPageSet() { return this.pageSet; }

	public Integer getNumber() { return this.number; }
	public void setNumber(Integer number) { this.number = number; }

	public List<Long> getItemIds() { return this.itemIds; }

	public abstract List<ItemType> getItems() throws Throwable;

//	public List<ItemType> getItems() throws Throwable {
//		if (this.items == null) {
//			synchronized (this) {
//				if (this.items == null) {
//					this.items = new ArrayList<ItemType>();
//					for (Long id : this.getItemIds()) {
//						ItemType item = this.loadItem(id);
//						if (item != null) {
//							this.items.add(item);
//						}
//					}
//				}
//			}
//		}
//		return this.items;
//	}

//	protected abstract ItemType loadItem(Long id) throws Throwable;
}
