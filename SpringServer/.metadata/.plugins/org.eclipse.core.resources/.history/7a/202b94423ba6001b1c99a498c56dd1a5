package com.server.exceptions;

public class RegisterNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private String numberInvoice;
	private int numberWagon;
	
	public RegisterNotFoundException(String numberInvoice, int numberWagon) {
		this.numberInvoice = numberInvoice;
		this.numberWagon = numberWagon;
	}
	
	@Override
	public String getMessage() {
		return "The record of registration with number invoice " + this.numberInvoice + " and with number wagon "
				+ this.numberWagon + " not found!";
	}
}