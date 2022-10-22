package com.fidelity.warehouseservice;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;

/**
 * The Spring Boot application that launches the WarehouseController 
 * which is a RESTful web service.
 * 
 * @author ROI Instructor Team
 *
 */
@SpringBootApplication
// tell Spring Boot where to scan for annotated components
@ComponentScan(basePackages={"com.fidelity.integration", "com.fidelity.restservices", "com.fidelity.business.service"})
// tell MyBatis where to scan for mapping interface files
@MapperScan(basePackages="com.fidelity.integration.mapper")  
public class WarehouseServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(WarehouseServiceApplication.class, args);
	}

	/**
	 * This method creates a Logger that can be autowired in other classes:{@code
	 *    @Autowired 
	 *    private Logger logger;
	 }*/
	@Bean
	@Scope("prototype")
	public Logger createLogger(InjectionPoint ip) {
	    Class<?> classThatWantsALogger = ip.getField().getDeclaringClass();
	    return LoggerFactory.getLogger(classThatWantsALogger);
	}
}
