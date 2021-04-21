package com.server.wagons;

//***************************************
//Database element for the Wagons table
//***************************************

public class DataElementWagons {
	private int numberWagon;				//Recognized wagon identification number
	private String arrivalDate;				//Arrival of the wagon
	private String imagePath;				//Path to the recognized image on the server
	private double levelCorrectRecognize;	//The level of correct recognition
	
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
