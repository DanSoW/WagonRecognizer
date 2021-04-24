package com.server.database;

public class InvoiceNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private String number;
	public InvoiceNotFoundException(String number) {
		this.number = number;
	}
	
	@Override
	public String getMessage() {
		return "Number invoice with number " + this.number + " not found!";
	}
}