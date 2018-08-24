package com.wjw.ems.interceptor;

import java.io.File;
import java.util.Map;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

public class DownloadFileCleanerInterceptor implements Interceptor {

	@Override
	public void destroy() {
	}

	@Override
	public void init() {
	}

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {

		String result = invocation.invoke();

		// 删除临时文件
		// 1. 需要确定文件的文件名
		Map<String, Object> requestMap = (Map<String, Object>) invocation.getInvocationContext().get("request");
		String fileDir = (String) requestMap.get("fileDir");

		if (fileDir == null) {
			return result;
		}
		
		Thread.sleep(5000);
		
		// 2. 删除文件
		File file = new File(fileDir);
		if (file != null && file.exists()) {
			file.delete();
		}

		return result;
	}

}
