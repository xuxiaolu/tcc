package com.xuxl.tcctransaction.redpacket.config;

import java.io.IOException;

import javax.sql.DataSource;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import com.alibaba.druid.pool.DruidDataSource;
import com.xuxl.tcctransaction.spring.repository.SpringJdbcTransactionRepository;

@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = {"com.xuxl.tcctransaction.redpacket.infrastructure.dao","com.xuxl.tcctransaction.spring.mapper"})
public class DataSourceConfig {
	
	@Bean
	@Primary
	public DataSource redPacketDataSource() {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setUrl("jdbc:mysql://192.168.17.25/tcc_red?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true");
		dataSource.setUsername("ulife");
		dataSource.setPassword("ulife");
		dataSource.setMaxActive(8);
		return dataSource;
	}
	
	@Bean
	public SqlSessionFactoryBean sqlSessionFactoryBean() throws IOException {
		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		factoryBean.setDataSource(redPacketDataSource());
		factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:sqlmap/main/*.xml"));
		factoryBean.setTypeAliasesPackage("com.xuxl.tcctransaction.redpacket.domain.entity");
		return factoryBean;
	}
	
	
	@Bean
	public PlatformTransactionManager transactionManager() {
		DataSourceTransactionManager manager = new DataSourceTransactionManager(redPacketDataSource());
		return manager;
	}
	
	@Bean
	public TransactionTemplate transactionTemplate() {
		TransactionTemplate template = new TransactionTemplate(transactionManager());
		return template;
	}
	
	@Bean
	public SpringJdbcTransactionRepository transactionRepository() {
		SpringJdbcTransactionRepository transactionRepository = new SpringJdbcTransactionRepository();
		transactionRepository.setDomain("REDPACKET");
		return transactionRepository;
	}

}
