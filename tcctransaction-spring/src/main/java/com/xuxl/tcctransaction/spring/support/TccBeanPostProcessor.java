package com.xuxl.tcctransaction.spring.support;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.xuxl.tcctransaction.support.BeanFactory;
import com.xuxl.tcctransaction.support.BeanFactoryAdapter;

@Component
public class TccBeanPostProcessor implements ApplicationListener<ContextRefreshedEvent> {

	public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
		ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();
		if (applicationContext.getParent() == null) {
			BeanFactoryAdapter.setBeanFactory(applicationContext.getBean(BeanFactory.class));
		}
	}
}
