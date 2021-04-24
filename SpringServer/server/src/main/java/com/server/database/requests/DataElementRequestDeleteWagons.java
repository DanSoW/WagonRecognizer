package com.server.database.requests;

import com.sun.istack.NotNull;

public class DataElementRequestDeleteWagons {
	@NotNull
	private Integer numberWagon;

	public Integer getNumberWagon() {
		return numberWagon;
	}

	public void setNumberWagon(Integer numberWagon) {
		this.numberWagon = numberWagon;
	}
}
