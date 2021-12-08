package com.cvovo.gamemanager.util;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Paging {
	public static final String ORDER_DIRECTION_ASC = "ASC";
	public static final String ORDER_DIRECTION_DESC = "DESC";

	private static final int DEFAULT_PAGE_SIZE = 10;

	@JsonProperty("pageNum")
	private int page = 1;
	@JsonProperty("pageSize")
	private int rows = DEFAULT_PAGE_SIZE;
	private String sortName;
	private String sortOrder;
	public String searchText;

	private int totalPages;
	@JsonProperty("total")
	private long totalItems;
	@JsonProperty("rows")
	private List<?> items;

	public Paging() {
	}

	public Paging(List<?> items) {
		this.items = items;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page > 0 ? page : 1;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows > 0 ? rows : DEFAULT_PAGE_SIZE;
	}

	public String getSortName() {
		return sortName;
	}

	public void setSortName(String sortName) {
		this.sortName = sortName;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public long getTotalItems() {
		return totalItems;
	}

	public void setTotalItems(long totalItems) {
		this.totalItems = totalItems;
	}

	public List<?> getItems() {
		return items;
	}

	public void setItems(List<?> items) {
		this.items = items;
	}

	public Pageable createPageable() {
		Sort sort = null;
		if (!StringUtils.isEmpty(getSortName()))
			if (Paging.ORDER_DIRECTION_DESC.equalsIgnoreCase(getSortOrder()))
				sort = new Sort(Direction.DESC, getSortName());
			else
				sort = new Sort(Direction.ASC, getSortName());

		return new PageRequest(getPage() - 1, getRows(), sort);
	}

	public void record(Page<?> page) {
		setTotalItems(page.getTotalElements());
		setTotalPages(page.getTotalPages());
		setItems(page.getContent());
	}
}
