package com.server.database.elements;

//********************************************
//Единица данных для информации о пути
//к изображению в локальном хранилище сервера
//********************************************


public class DataElementImageFilePath {
	private String filePath;		//Абсолютный путь к изображению в локальном хранилище сервера

	public DataElementImageFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
}
