package com.server.program;

import javax.servlet.MultipartConfigElement;

import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@Configuration
@SpringBootApplication(scanBasePackages = "com.server")
public class ServerApplication {

	@Bean
	public MultipartConfigElement multipartConfigElement() {
		//установка конфигурации для загрузки картинки на сервер
		MultipartConfigFactory factory = new MultipartConfigFactory();
		factory.setMaxFileSize(DataSize.ofMegabytes(256L));				//максимальный размер файла
        factory.setMaxRequestSize(DataSize.ofMegabytes(256L));			//максимальный размер возвращаемого файла
        return factory.createMultipartConfig();							//возвращает созданную конфигурацию
	}
	
	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

}
