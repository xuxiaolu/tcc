package com.xuxl.tcctransaction.redpacket;

import java.util.concurrent.CountDownLatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.xuxl.tcctransaction.dubbo.config.EnableDubboAnnotationBeanRegister;

@SpringBootApplication
@ComponentScan("com.xuxl")
@EnableDubboAnnotationBeanRegister(basePackages = "com.xuxl",isProvider = true)
public class RedPacketApplication {
	
	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(RedPacketApplication.class, args);
		CountDownLatch latch = new CountDownLatch(1);
		latch.await();
	}

}
