package com.xuxl.tcctransaction.spring.recover;

import org.quartz.Scheduler;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;

import com.xuxl.tcctransaction.SystemException;
import com.xuxl.tcctransaction.recover.TransactionRecovery;
import com.xuxl.tcctransaction.support.TransactionConfigurator;

public class RecoverScheduledJob {

    private TransactionRecovery transactionRecovery;

    private TransactionConfigurator transactionConfigurator;

    private Scheduler scheduler;

    public void init() {
        try {
            MethodInvokingJobDetailFactoryBean jobDetail = new MethodInvokingJobDetailFactoryBean();
            jobDetail.setTargetObject(transactionRecovery);
            jobDetail.setTargetMethod("startRecover");
            jobDetail.setName("transactionRecoveryJob");
            jobDetail.setConcurrent(false);
            jobDetail.afterPropertiesSet();
            CronTriggerFactoryBean cronTrigger = new CronTriggerFactoryBean();
            cronTrigger.setJobDetail(jobDetail.getObject());
            cronTrigger.setBeanName("transactionRecoveryCronTrigger");
            cronTrigger.setCronExpression(transactionConfigurator.getRecoverConfig().getCronExpression());
            cronTrigger.afterPropertiesSet();
            scheduler.scheduleJob(jobDetail.getObject(), cronTrigger.getObject());
            scheduler.start();

        } catch (Exception e) {
            throw new SystemException(e);
        }
    }

    public void setTransactionRecovery(TransactionRecovery transactionRecovery) {
        this.transactionRecovery = transactionRecovery;
    }

    public void setTransactionConfigurator(TransactionConfigurator transactionConfigurator) {
        this.transactionConfigurator = transactionConfigurator;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }
}
