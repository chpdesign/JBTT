package tracker.cache;

import tracker.pagination.Page;

public abstract class AbstractPageSet<PageType extends Page> extends AbstractCache<PageType> {
	protected Integer itemsPerPage;
	protected String firstPageUrl;
	protected String baseUrl;

	protected Integer itemsCount;
	protected Integer pagesCount;

	protected AbstractPageSet() { }

	public String getRegion() { return "pages"; }

	public synchronized void reset() throws Throwable {
		this.getCacheAccess().clear();
		this.itemsCount = null;
		this.pagesCount = null;
	}

	public Integer getItemsPerPage() {
		return this.itemsPerPage;
	}

	public String getFirstPageUrl() {
		return this.firstPageUrl;
	}

	public String getBaseUrl() {
		return this.baseUrl;
	}

	public Integer getItemsCount() throws Throwable {
		if (this.itemsCount == null) {
			this.itemsCount = this.requestItemsCount();
		}
		return this.itemsCount;
	}

	public Integer getPagesCount() throws Throwable {
		if (this.pagesCount == null) {
			int pagesCount = this.getItemsCount() / this.getItemsPerPage();
			if (this.getItemsCount() % this.getItemsPerPage() > 0) {
				pagesCount++;
			}
			this.pagesCount = pagesCount;
		}
		return this.pagesCount;
	}

	protected int getPageOffset(int number) {
		return (number - 1) * this.getItemsPerPage();
	}

	protected abstract int requestItemsCount() throws Throwable;
}
