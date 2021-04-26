package com.server.settings;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

//************************************************************
//Settings for connecting to the database
//************************************************************

@Component
@ConfigurationProperties(prefix = "spring.datasource")
public class ConnectionSettings {
	private static int DEFAULT_MAX_POOL_SIZE = 5;
	
	private String jdbcDriver;
	private String jdbcUrl;
	private String jdbcUser;
	private String jdbcPassword;
	private int jdbcMaxPoolSize = DEFAULT_MAX_POOL_SIZE;
	
	public String getJdbcDriver() {
		return jdbcDriver;
	}
	
	public void setJdbcDriver(String jdbcDriver) {
		this.jdbcDriver = jdbcDriver;
	}
	
	public String getJdbcUrl() {
		return jdbcUrl;
	}
	
	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}
	
	public String getJdbcPassword() {
		return jdbcPassword;
	}
	
	public void setJdbcPassword(String jdbcPassword) {
		this.jdbcPassword = jdbcPassword;
	}
	
	public String getJdbcUser() {
		return jdbcUser;
	}
	
	public void setJdbcUser(String jdbcUser) {
		this.jdbcUser = jdbcUser;
	}
	
	public int getJdbcMaxPoolSize() {
		return jdbcMaxPoolSize;
	}
	
	public void setJdbcMaxPoolSize(int jdbcMaxPoolSize) {
		this.jdbcMaxPoolSize = jdbcMaxPoolSize;
	}
}
