package com.server.program;

import javax.servlet.MultipartConfigElement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

//***********************************************************
//Запуск серверной части приложения
//***********************************************************

@Configuration
@SpringBootApplication(scanBasePackages = "com.server")
public class ServerApplication {

	@Bean
	public MultipartConfigElement multipartConfigElement() {
		//Настройка конфигурации для загрузки изображения на сервер
		MultipartConfigFactory factory = new MultipartConfigFactory();
		factory.setMaxFileSize(DataSize.ofMegabytes(256L));				//максимальный размер файла
        factory.setMaxRequestSize(DataSize.ofMegabytes(256L));			//максимальный размер возвращаемого файла
        return factory.createMultipartConfig();							//возврат созданной конфигурации
	}
	
	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

}
