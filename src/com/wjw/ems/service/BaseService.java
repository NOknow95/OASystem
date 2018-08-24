package com.wjw.ems.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.wjw.ems.dao.BaseDao;
import com.wjw.ems.orm.Page;
import com.wjw.ems.orm.PropertyFilter;

public class BaseService<T> {

	@Autowired
	protected BaseDao<T> dao;

	@Transactional(readOnly = true)
	public List<T> getByIsNull(String propertyName) {
		return dao.getByIsNull(propertyName);
	}

	@Transactional(readOnly = true)
	public List<T> getByIsNotNull(String propertyName) {
		return dao.getByIsNotNull(propertyName);
	}

	@Transactional(readOnly = true)
	public List<T> getAll(boolean... cachable) {
		return this.dao.getAll(cachable);
	}

	@Transactional(readOnly = true)
	public T get(Integer id) {
		return dao.get(id);
	}

	@Transactional(readOnly = true)
	public Page<T> getPae(Page<T> page) {
		return dao.getPage(page);
	}

	@Transactional(readOnly = true)
	public T getBy(String propertyName, String propertyVal) {
		return dao.getBy(propertyName, propertyVal);
	}

	public Page<T> getPae(Page<T> page, Map<String, Object> params) {
		List<PropertyFilter> filters = PropertyFilter.parseParamsToPropertyFilters(params);
		return dao.getPage(page, filters);
	}
}
