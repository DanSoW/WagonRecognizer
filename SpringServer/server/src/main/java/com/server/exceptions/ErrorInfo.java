package com.server.exceptions;

public class ErrorInfo {
	private String messageError;
	public ErrorInfo(String msg) {
		this.messageError = msg;
	}
	
	public String getMessage() {
		return this.messageError;
	}
	
	public void setMessage(String msg) {
		this.messageError = msg;
	}
}
