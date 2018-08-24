package com.wjw.ems.action;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

import org.apache.struts2.interceptor.ParameterAware;
import org.apache.struts2.interceptor.RequestAware;
import org.apache.struts2.interceptor.SessionAware;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;
import com.wjw.ems.orm.Page;
import com.wjw.ems.service.BaseService;

public class BaseAction<T> extends ActionSupport
		implements RequestAware, SessionAware, ModelDriven<T>, Preparable, ParameterAware {

	@Autowired
	protected BaseService<T> baseService;

	protected Page<T> page;

	public void setPage(Page<T> page) {
		this.page = page;
	}

	public Page<T> getPage() {
		return page;
	}

	protected T model;

	@Override
	public T getModel() {
		return model;
	}

	protected File file;

	public void setFile(File file) {
		this.file = file;
	}

	protected Integer id;

	public void setId(Integer id) {
		this.id = id;
	}

	protected String contentType;
	protected long contentLength;
	protected String filename;
	protected InputStream inputStream;

	public String getContentType() {
		return contentType;
	}

	public long getContentLength() {
		return contentLength;
	}

	public String getContentDisposition() {
		return "attachment;filename=" + filename;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	protected Map<String, Object> sessionMap = null;

	@Override
	public void setSession(Map<String, Object> arg0) {
		this.sessionMap = arg0;
	}

	protected Map<String, Object> requsetMap = null;

	@Override
	public void setRequest(Map<String, Object> arg0) {
		this.requsetMap = arg0;
	}

	protected Map<String, String[]> parameterMap;

	@Override
	public void setParameters(Map<String, String[]> arg0) {
		this.parameterMap = arg0;
	}

	@Override
	public void prepare() throws Exception {

	}
}
