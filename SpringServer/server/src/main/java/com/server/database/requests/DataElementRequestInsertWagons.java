package com.server.database.requests;

import com.sun.istack.NotNull;

public class DataElementRequestInsertWagons{
	
	@NotNull
	private Integer numberWagon;

	@NotNull
	private String arrivalDate;
	
	@NotNull
	private String imagePath;
	
	@NotNull
	private double levelCorrectRecognize;

	public Integer getNumberWagon() {
		return numberWagon;
	}

	public void setNumberWagon(Integer numberWagon) {
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
