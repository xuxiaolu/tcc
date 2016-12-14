package com.xuxl.tcctransaction.capital;

import java.util.concurrent.CountDownLatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.xuxl.tcctransaction.dubbo.config.EnableDubboAnnotationBeanRegister;

@SpringBootApplication
@ComponentScan("com.xuxl")
@EnableDubboAnnotationBeanRegister(basePackages = "com.xuxl",isProvider = true)
public class CapitalApplication {
	
	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(CapitalApplication.class, args);
		CountDownLatch latch = new CountDownLatch(1);
		latch.await();
	}

}
	