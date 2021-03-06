package com.server.controllers;

import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.server.exceptions.ErrorInformation;

import ch.qos.logback.classic.Logger;

//**********************************************************************
//Контроллер для логгирования ошибок внутри серверной части приложения
//**********************************************************************

@ControllerAdvice
public class LogErrorController {
	private static final Logger logger =
			(Logger) LoggerFactory.getLogger(LogErrorController.class);
	
	@ExceptionHandler(Exception.class)
	@ResponseBody
	public ErrorInformation processException(Exception e) {
		logger.error("Internal server error", e);
		return new ErrorInformation(e.getMessage());
	}
}
