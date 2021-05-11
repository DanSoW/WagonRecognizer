package com.server.database.requests;

import com.sun.istack.NotNull;

public class DataElementRequestSetting {
	@NotNull
	private Short size;
	
	public Short getSize() {
		return size;
	}

	public void setSize(Short size) {
		this.size = size;
	}
}
