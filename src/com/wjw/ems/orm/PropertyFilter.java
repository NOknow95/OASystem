package com.wjw.ems.orm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class PropertyFilter {

	public static List<PropertyFilter> parseParamsToPropertyFilters(Map<String, Object> params) {
		List<PropertyFilter> filters = new ArrayList<>();

		for (Map.Entry<String, Object> param : params.entrySet()) {
			String key = param.getKey();
			Object propertyVal = param.getValue();
			if (propertyVal == null || propertyVal.toString().trim().equals("")) {
				continue;
			}

			String str = StringUtils.substringBefore(key, "_");
			String matchCode = str.substring(0, str.length() - 1);
			String typeCode = str.substring(str.length() - 1);

			String propertyName = StringUtils.substringAfter(key, "_");
			MatchType matchType = Enum.valueOf(MatchType.class, matchCode);
			Class propertyType = Enum.valueOf(PropertyType.class, typeCode).getPropertyType();

			PropertyFilter filter = new PropertyFilter(propertyName, propertyVal, matchType, propertyType);

			filters.add(filter);
		}
		return filters;
	}

	public static void main(String[] args) {
		MatchType valueOf = Enum.valueOf(MatchType.class, "EQ");
		System.out.println(valueOf);
	}

	public PropertyFilter(String propertyName, Object propertyVal, MatchType matchType, Class propertyType) {
		super();
		this.propertyName = propertyName;
		this.propertyVal = propertyVal;
		this.matchType = matchType;
		this.propertyType = propertyType;
	}

	private String propertyName;
	private Object propertyVal;

	private MatchType matchType;
	private Class propertyType;

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public Object getPropertyVal() {
		return propertyVal;
	}

	public void setPropertyVal(Object propertyVal) {
		this.propertyVal = propertyVal;
	}

	public MatchType getMatchType() {
		return matchType;
	}

	public void setMatchType(MatchType matchType) {
		this.matchType = matchType;
	}

	public Class getPropertyType() {
		return propertyType;
	}

	public void setPropertyType(Class propertyType) {
		this.propertyType = propertyType;
	}

	public enum MatchType {
		EQ, LT, LE, GT, GE, LIKE;
	}

	enum PropertyType {
		I(Integer.class), D(Date.class), F(Float.class), S(String.class), B(Boolean.class);

		private Class propertyType;

		private PropertyType(Class propertyType) {
			this.propertyType = propertyType;
		}

		private Class getPropertyType() {
			return propertyType;
		}

	}

}
