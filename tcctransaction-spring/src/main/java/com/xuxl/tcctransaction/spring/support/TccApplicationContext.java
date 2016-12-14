package com.xuxl.tcctransaction.spring.support;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.xuxl.tcctransaction.support.BeanFactory;

@Component
public class TccApplicationContext implements BeanFactory, ApplicationContextAware {

	private ApplicationContext applicationContext;

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public Object getBean(Class<?> aClass) {
		return this.applicationContext.getBean(aClass);
	}
}
