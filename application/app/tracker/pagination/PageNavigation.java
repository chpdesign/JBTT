package tracker.pagination;

import tracker.cache.AbstractPageSet;

public class PageNavigation {
	protected AbstractPageSet pageSet;
	protected String[] parameters;
	protected Integer pageNumber;

	public PageNavigation(AbstractPageSet pageSet, Integer pageNumber) {
		this(pageSet, pageNumber, new String[] { });
	}

	public PageNavigation(AbstractPageSet pageSet, Integer pageNumber, String[] parameters) {
		this.pageSet = pageSet;
		this.parameters = parameters;
		this.pageNumber = pageNumber;
	}

	public boolean hasPreviousPage() {
		return this.getPageNumber() > 1;
	}

	public boolean hasNextPage() throws Throwable {
		return this.getPageNumber() < this.getPageSet().getPagesCount();
	}

	public AbstractPageSet getPageSet() {
		return this.pageSet;
	}

	public int getPageNumber() {
		return this.pageNumber;
	}

	public String getPageUrl(Integer pageNumber) {
		String url;
		if (pageNumber > 1) {
			url = this.getPageSet().getBaseUrl();
		} else {
			url = this.getPageSet().getFirstPageUrl();
		}

		for (int i = 0; i < this.parameters.length; i++) {
			url = url.replace(String.format("{%d}", i), this.parameters[i]);
		}

		url = url.replace("{page}", pageNumber.toString());

		return url;
	}

	public String getPageUrlExt(String[] params) {
		String template = "/torrent/%s?commentsPageNumber=%s";

		return String.format(template, params);
	}

	public String getHtml() throws Throwable {
		String html = "<ul class=\"pagination\">";

		if (this.hasPreviousPage()) {
			html += String.format("<li><a href=\"%s\">&lsaquo;&nbsp;</a></li>", this.getPageUrl(this.getPageNumber() - 1));
		}

		if (this.getPageSet().getPagesCount() < 13) { /* « 1 2 3 4 5 6 7 8 9 10 11 12 » */
			for (int pageNumber = 1; pageNumber <= this.getPageSet().getPagesCount(); pageNumber++) {
				if (this.getPageNumber() == pageNumber) {
					html += "<li class=\"current\">" + pageNumber + "</li>";
				} else {
					html += "<li><a href=\"" + this.getPageUrl(pageNumber) + "\">" + pageNumber + "</a></li>";
				}
			}
		} else if(this.getPageNumber() < 9) { /* « 1 2 3 4 5 6 7 8 9 10 … 25 26 » */
			for (int pageNumber = 1; pageNumber <= 10; pageNumber++) {
				if (this.getPageNumber() == pageNumber) {
					html += "<li class=\"current\">" + pageNumber + "</li>";
				} else {
					html += "<li><a href=\"" + this.getPageUrl(pageNumber) + "\">" + pageNumber + "</a></li>";
				}
			}

			html += "<li>&hellip;</li>";
			html += "<li><a href=\"" + this.getPageUrl(this.getPageSet().getPagesCount() - 1) + "\">" + (this.getPageSet().getPagesCount() - 1) + "</a></li>";
			html += "<li><a href=\"" + this.getPageUrl(this.getPageSet().getPagesCount()) + "\">" + this.getPageSet().getPagesCount() + "</a></li>";
		} else if(this.getPageNumber() > this.getPageSet().getPagesCount() - 8) { /* « 1 2 … 17 18 19 20 21 22 23 24 25 26 » */
			html += "<li><a href=\"" + this.getPageUrl(1) + "\">1</a></li>";
			html += "<li><a href=\"" + this.getPageUrl(2) + "\">1</a></li>";
			html += "<li>&hellip;</li>";

			for (int pageNumber = this.getPageSet().getPagesCount() - 9; pageNumber <= this.getPageSet().getPagesCount(); pageNumber++) {
				if (this.getPageNumber() == pageNumber) {
					html += "<li class=\"current\">" + pageNumber + "</li>";
				} else {
					html += "<li><a href=\"" + this.getPageUrl(pageNumber) + "\">" + pageNumber + "</a></li>";
				}
			}
		} else { /* « 1 2 … 5 6 7 8 9 10 11 12 13 14 … 25 26 » */
			html += "<li><a href=\"" + this.getPageUrl(1) + "\">1</a></li>";
			html += "<li><a href=\"" + this.getPageUrl(2) + "\">1</a></li>";
			html += "<li>&hellip;</li>";

			for (int pageNumber = this.getPageNumber() - 5; pageNumber <= this.getPageNumber() + 5; pageNumber++) {
				if (this.getPageNumber() == pageNumber) {
					html += "<li class=\"current\">" + pageNumber + "</li>";
				} else {
					html += "<li><a href=\"" + this.getPageUrl(pageNumber) + "\">" + pageNumber + "</a></li>";
				}
			}

			html += "<li>&hellip;</li>";
			html += "<li><a href=\"" + this.getPageUrl(this.getPageSet().getPagesCount() - 1) + "\">" + (this.getPageSet().getPagesCount() - 1) + "</a></li>";
			html += "<li><a href=\"" + this.getPageUrl(this.getPageSet().getPagesCount()) + "\">" + this.getPageSet().getPagesCount() + "</a></li>";
		}

		if (this.hasNextPage()) {
            html += String.format("<li><a href=\"%s\">&nbsp;&rsaquo;</a></li>", this.getPageUrl(this.getPageNumber() + 1));
        }

		html += "</ul>";

		return html;
	}
}
