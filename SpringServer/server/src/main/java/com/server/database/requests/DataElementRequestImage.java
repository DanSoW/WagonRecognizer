package com.server.database.requests;

import com.sun.istack.NotNull;

public class DataElementRequestImage {
	@NotNull
	private String fileName;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}