package com.server.exceptions;

public class ErrorInformation {
	private String messageError;
	public ErrorInformation(String msg) {
		this.messageError = msg;
	}
	
	public String getMessage() {
		return this.messageError;
	}
	
	public void setMessage(String msg) {
		this.messageError = msg;
	}
}
