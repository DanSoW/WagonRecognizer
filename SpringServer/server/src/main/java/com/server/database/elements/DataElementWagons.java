package com.server.database.elements;

//***************************************
//Единица данных для таблицы Wagons
//***************************************

public class DataElementWagons {
	private int numberWagon;				//Идентификационный номер полувагона
	private String arrivalDate;				//Дата прибытия полувагона
	private String imagePath;				//Путь к изображению распознанного полувагона в локальном хранилище сервера
	private double levelCorrectRecognize;	//Уровень корректного распознования (чем ниже значение, тем лучше)
	
	public DataElementWagons(int numberWagon, String arrivalDate, String imagePath, double levelCorrectRecognize) {
		this.numberWagon = numberWagon;
		this.arrivalDate = arrivalDate;
		this.imagePath = imagePath;
		this.levelCorrectRecognize = levelCorrectRecognize;
	}

	public int getNumberWagon() {
		return numberWagon;
	}

	public void setNumberWagon(int numberWagon) {
		this.numberWagon = numberWagon;
	}

	public String getArrivalDate() {
		return arrivalDate;
	}

	public void setArrivalDate(String arrivalDate) {
		this.arrivalDate = arrivalDate;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public double getLevelCorrectRecognize() {
		return levelCorrectRecognize;
	}

	public void setLevelCorrectRecognize(double levelCorrectRecognize) {
		this.levelCorrectRecognize = levelCorrectRecognize;
	}
	
}
