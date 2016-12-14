package com.xuxl.tcctransaction.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import com.xuxl.tcctransaction.interceptor.CompensableTransactionInterceptor;
import com.xuxl.tcctransaction.interceptor.ResourceCoordinatorInterceptor;
import com.xuxl.tcctransaction.recover.TransactionRecovery;
import com.xuxl.tcctransaction.spring.TccCompensableAspect;
import com.xuxl.tcctransaction.spring.TccTransactionContextAspect;
import com.xuxl.tcctransaction.spring.recover.RecoverScheduledJob;
import com.xuxl.tcctransaction.spring.support.TccTransactionConfigurator;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableScheduling
public class TccTransactionConfig {
	
	@Bean
	public TccTransactionConfigurator tccTransactionConfigurator() {
		return new TccTransactionConfigurator();
	}
	
	@Bean
	public TransactionRecovery transactionRecovery() {
		TransactionRecovery recovery = new TransactionRecovery();
		recovery.setTransactionConfigurator(tccTransactionConfigurator());
		return recovery;
	}
	
	@Bean
	public SchedulerFactoryBean schedulerFactoryBean() {
		SchedulerFactoryBean factoryBean = new SchedulerFactoryBean();
		return factoryBean;
	}
	
	@Bean(initMethod = "init")
	public RecoverScheduledJob scheduleJob() {
		RecoverScheduledJob job = new RecoverScheduledJob();
		job.setScheduler(schedulerFactoryBean().getObject());
		job.setTransactionRecovery(transactionRecovery());
		job.setTransactionConfigurator(tccTransactionConfigurator());
		return job;
	}
	
	@Bean
	public CompensableTransactionInterceptor compensableTransactionInterceptor() {
		CompensableTransactionInterceptor interceptor = new CompensableTransactionInterceptor();
		interceptor.setTransactionConfigurator(tccTransactionConfigurator());
		return interceptor;
	}
	
	@Bean
	public ResourceCoordinatorInterceptor resourceCoordinatorInterceptor() {
		ResourceCoordinatorInterceptor interceptor = new ResourceCoordinatorInterceptor();
		interceptor.setTransactionConfigurator(tccTransactionConfigurator());
		return interceptor;
	}
	
	@Bean
	public TccCompensableAspect tccCompensableAspect() {
		TccCompensableAspect aspect = new TccCompensableAspect();
		aspect.setCompensableTransactionInterceptor(compensableTransactionInterceptor());
		return aspect;
	}

	@Bean
	public TccTransactionContextAspect tccTransactionContextAspect() {
		TccTransactionContextAspect aspect = new TccTransactionContextAspect();
		aspect.setResourceCoordinatorInterceptor(resourceCoordinatorInterceptor());
		return aspect;
	}
}
