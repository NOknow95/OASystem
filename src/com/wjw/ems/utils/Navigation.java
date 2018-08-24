package com.wjw.ems.utils;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class Navigation {

	private Integer id;
	private String text;
	private String state;
	private String url;
	private Set<Navigation> children = new LinkedHashSet<>();

	public Navigation() {
	}

	public Navigation(Integer id, String text, String state, String url) {
		super();
		this.id = id;
		this.text = text;
		this.state = state;
		this.url = url;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Set<Navigation> getChildren() {
		return children;
	}

	public void setChildren(Set<Navigation> children) {
		this.children = children;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Navigation other = (Navigation) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
