package com.server.wagons;

public class WagonNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private int id;
	public WagonNotFoundException(int id) {
		this.id = id;
	}
	
	@Override
	public String getMessage() {
		return "Number wagon = " + id
				+ " not found";
	}
}
