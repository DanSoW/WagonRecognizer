package com.server.settings;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

//************************************************************
//Настройки подключения к базе данных
//************************************************************

@Component
@ConfigurationProperties(prefix = "spring.datasource")
public class ConnectionSettings {
	private static int DEFAULT_MAX_POOL_SIZE = 5;
	
	private String jdbcDriver;								//название jdbc драйвера для подключения
	private String jdbcUrl;									//адрес, для подключения к базе данных
	private String jdbcUser;								//название пользователя
	private String jdbcPassword;							//пароль для подключения
	private int jdbcMaxPoolSize = DEFAULT_MAX_POOL_SIZE;	//установка максимального пула (по-умолчанию 5)
	
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
