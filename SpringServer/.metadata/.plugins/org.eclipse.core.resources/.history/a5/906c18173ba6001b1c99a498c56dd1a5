package com.server.exceptions;

public class InvoiceNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private String number;
	public InvoiceNotFoundException(String number) {
		this.number = number;
	}
	
	@Override
	public String getMessage() {
		return "The invoice with number " + this.number + " not found!";
	}
}