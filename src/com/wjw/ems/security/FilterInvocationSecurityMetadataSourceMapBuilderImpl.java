package com.wjw.ems.security;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wjw.ems.entities.Authority;
import com.wjw.ems.entities.Resource;
import com.wjw.ems.service.ResourceService;
import com.wjw.ems.utils.ReflectionUtils;

@Component
public class FilterInvocationSecurityMetadataSourceMapBuilderImpl
		implements FilterInvocationSecurityMetadataSourceMapBuilder {

	@Autowired
	private ResourceService resourceService;

	@Override
	public LinkedHashMap<String, List<String>> buildSrcMap() {

		List<Resource> resources = resourceService.getAll(true);
		LinkedHashMap srcMap = new LinkedHashMap<>();

		for (Resource resource : resources) {
			String url = resource.getUrl();
			Set<Authority> authorities = resource.getAuthorities();
			List<String> names = ReflectionUtils.fetchElementPropertyToList(authorities, "name");

			srcMap.put(url, names);
		}

		return srcMap;
	}

}
