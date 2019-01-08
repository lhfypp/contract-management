package com.snxy.contract.business;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringCloudApplication
@ComponentScan({"com.snxy.contract.business", "com.snxy.contract.dao"})
public class ContractBizApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContractBizApplication.class, args);
    }

}

