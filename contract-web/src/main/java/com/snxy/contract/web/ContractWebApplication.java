package com.snxy.contract.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableEurekaClient
@ComponentScan({"com.snxy.contract.web","com.snxy.contract.business","com.snxy.contract.dao"})
public class ContractWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContractWebApplication.class, args);
	}

}

