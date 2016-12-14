package com.xuxl.tcctransaction.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.xuxl.tcctransaction.capital.api.CapitalTradeOrderService;
import com.xuxl.tcctransaction.dubbo.config.EnableDubboAnnotationBeanRegister;

@SpringBootApplication
@ComponentScan("com.xuxl")
@EnableDubboAnnotationBeanRegister(basePackages = "com.xuxl", isProvider = false)
//@ImportResource("classpath:appcontext-service-consumer.xml")
public class OrderApplication {
	
	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(OrderApplication.class, args);
		CapitalTradeOrderService service = context.getBean(CapitalTradeOrderService.class);
		System.out.println(service);
	}

}
