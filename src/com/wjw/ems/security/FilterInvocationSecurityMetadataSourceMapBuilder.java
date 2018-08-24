package com.wjw.ems.security;

import java.util.LinkedHashMap;
import java.util.List;

public interface FilterInvocationSecurityMetadataSourceMapBuilder {

	LinkedHashMap<String, List<String>> buildSrcMap();

}
