package com.wjw.ems.orm;

import java.util.List;

public class Page<T> {
	private int pageNo = 1;
	private int pageSize = 5;

	private int totalElements;
	private List<T> content;

	public void setPageNo(String pageNoStr) {

		int pageNo = 1;
		try {
			pageNo = Integer.parseInt(pageNoStr);
		} catch (NumberFormatException e) {
		}

		if (pageNo < 1) {
			pageNo = 1;
		}
		this.pageNo = pageNo;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setTotalElements(int totalElements) {
		this.totalElements = totalElements;
		if (this.pageNo > this.getTotalPages()) {
			this.pageNo = this.getTotalPages();
		}
	}

	public int getTotalElements() {
		return totalElements;
	}

	public void setContent(List<T> content) {
		this.content = content;
	}

	public List<T> getContent() {
		return content;
	}

	public int getTotalPages() {
		int totalPages = this.totalElements / this.pageSize;
		if (this.totalElements % this.pageSize != 0) {
			totalPages++;
		}
		return totalPages;
	}

	public boolean isHasPrev() {
		return this.getPageNo() > 1;
	}

	public boolean isHasNext() {
		return this.getPageNo() < this.getTotalPages();
	}

	public int getPrev() {
		if (isHasPrev()) {
			return this.pageNo - 1;
		}
		return this.pageNo;
	}

	public int getNext() {
		if (isHasNext()) {
			return this.pageNo + 1;
		}
		return this.pageNo;
	}

	@Override
	public String toString() {
		return "Page [pageNo=" + pageNo + ", pageSize=" + pageSize + ", totalElements=" + totalElements + ", content="
				+ content + "]";
	}

}
