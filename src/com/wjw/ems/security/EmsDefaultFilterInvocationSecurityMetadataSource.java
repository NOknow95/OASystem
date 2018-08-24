package com.wjw.ems.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.access.intercept.DefaultFilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.AntPathRequestMatcher;
import org.springframework.security.web.util.RequestMatcher;
import org.springframework.stereotype.Component;

@Component
public class EmsDefaultFilterInvocationSecurityMetadataSource
		implements FactoryBean<DefaultFilterInvocationSecurityMetadataSource> {

	@Autowired
	private FilterInvocationSecurityMetadataSourceMapBuilder filterInvocationSecurityMetadataSourceMapBuilder;

	public void setFilterInvocationSecurityMetadataSourceMapBuilder(
			FilterInvocationSecurityMetadataSourceMapBuilder filterInvocationSecurityMetadataSourceMapBuilder) {
		this.filterInvocationSecurityMetadataSourceMapBuilder = filterInvocationSecurityMetadataSourceMapBuilder;
	}

	@Override
	public DefaultFilterInvocationSecurityMetadataSource getObject() throws Exception {

		LinkedHashMap<String, List<String>> srcMap = filterInvocationSecurityMetadataSourceMapBuilder.buildSrcMap();

		LinkedHashMap<RequestMatcher, Collection<ConfigAttribute>> requestMap = new LinkedHashMap<>();

		// 初始化requestMap
		AntPathRequestMatcher matcher = null;
		List<ConfigAttribute> attris = null;

		// 以下信息需要查询数据库得到
		for (Map.Entry<String, List<String>> entry : srcMap.entrySet()) {
			String url = entry.getKey();
			List<String> roles = entry.getValue();
			matcher = new AntPathRequestMatcher(url);

			attris = new ArrayList<>();
			for (String role : roles) {
				attris.add(new SecurityConfig(role));
			}

			requestMap.put(matcher, attris);
		}

		// matcher = new AntPathRequestMatcher("/user.jsp");
		// attris = new ArrayList<>();
		// attris.add(new SecurityConfig("ROLE_USER"));
		// requestMap.put(matcher, attris);

		DefaultFilterInvocationSecurityMetadataSource metadataSource = new DefaultFilterInvocationSecurityMetadataSource(
				requestMap);
		return metadataSource;
	}

	@Override
	public Class<?> getObjectType() {
		return null;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
