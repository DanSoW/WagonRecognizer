package com.server.controllers;

import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.server.exceptions.ErrorInfo;

import ch.qos.logback.classic.Logger;

@ControllerAdvice
public class ErrorDataElementController {
	private static final Logger logger =
			(Logger) LoggerFactory.getLogger(ErrorDataElementController.class);
	
	@ExceptionHandler(Exception.class)
	@ResponseBody
	public ErrorInfo processException(Exception e) {
		logger.error("Unexcepted error", e);
		return new ErrorInfo(e.getMessage());
	}
}
