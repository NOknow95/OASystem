package com.wjw.ems.security;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.stereotype.Component;

@Component
public class EmsBeanPostProcessor implements BeanPostProcessor {

	private FilterSecurityInterceptor filterSecurityInterceptor;
	private EmsDefaultFilterInvocationSecurityMetadataSource metadataSource = null;
	private boolean isSetter = false;

	@Override
	public Object postProcessAfterInitialization(Object arg0, String arg1) throws BeansException {
		// 1. 获取 FilterSecurityInterceptor 对象
		if (arg0 instanceof FilterSecurityInterceptor) {
			this.filterSecurityInterceptor = (FilterSecurityInterceptor) arg0;
		}
		// 2. 获取 MyDefaultFilterInvocationSecurityMetadataSource 对象
		if (arg0 instanceof EmsDefaultFilterInvocationSecurityMetadataSource) {
			this.metadataSource = (EmsDefaultFilterInvocationSecurityMetadataSource) arg0;
		}
		// 3. 若上述的两个对象都已经被初始化完成, 则完成属性的替换
		if (this.filterSecurityInterceptor != null && this.metadataSource != null && !isSetter) {
			try {
				this.filterSecurityInterceptor.setSecurityMetadataSource(metadataSource.getObject());
				isSetter = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// 4. 结束.
		return arg0;
	}

	@Override
	public Object postProcessBeforeInitialization(Object arg0, String arg1) throws BeansException {
		return arg0;
	}

}
