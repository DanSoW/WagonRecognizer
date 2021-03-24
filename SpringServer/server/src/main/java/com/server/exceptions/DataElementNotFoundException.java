package com.server.exceptions;

public class DataElementNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private int id;
	public DataElementNotFoundException(int id) {
		this.id = id;
	}
	
	@Override
	public String getMessage() {
		return "Profile with id = " + id
				+ " not found";
	}
}
