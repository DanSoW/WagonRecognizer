package com.server.configs;

import javax.activation.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.server.settings.ConnectionSettings;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

//***************************************************
//Configuration for the database
//***************************************************

@Configuration
public class DatabaseConfig {
	private final ConnectionSettings connect;
	
	@Autowired
	public DatabaseConfig(ConnectionSettings csett) {
		this.connect = csett;
	}
	
	@Bean
	public DataSource dataSource() {
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setDriverClassName(connect.getJdbcDriver());
		hikariConfig.setJdbcUrl(connect.getJdbcUrl());
		hikariConfig.setUsername(connect.getJdbcUser());
		hikariConfig.setPassword(connect.getJdbcPassword());
		hikariConfig.setMaximumPoolSize(connect.getJdbcMaxPoolSize());
		hikariConfig.setPoolName("main");
		return (DataSource) new HikariDataSource(hikariConfig);
	}
}
