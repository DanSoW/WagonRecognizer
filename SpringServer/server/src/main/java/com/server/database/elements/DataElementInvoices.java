package com.server.database.elements;

public class DataElementInvoices {
	private String numberInvoice;
	private String nameSupplier;
	private short totalWagons;
	private String arrivalTrainDate;
	private String departureTrainDate;
	
	public DataElementInvoices(String numberInvoice, String nameSupplier, 
			short totalWagons, String arrivalTrainDate, String departureTrainDate) {
		this.numberInvoice = numberInvoice;
		this.nameSupplier = nameSupplier;
		this.totalWagons = totalWagons;
		this.arrivalTrainDate = arrivalTrainDate;
		this.departureTrainDate = departureTrainDate;
	}
	
	public String getNumberInvoice() {
		return numberInvoice;
	}
	public void setNumberInvoice(String numberInvoice) {
		this.numberInvoice = numberInvoice;
	}
	public String getNameSupplier() {
		return nameSupplier;
	}
	public void setNameSupplier(String nameSupplier) {
		this.nameSupplier = nameSupplier;
	}
	public short getTotalWagons() {
		return totalWagons;
	}
	public void setTotalWagons(short totalWagons) {
		this.totalWagons = totalWagons;
	}
	public String getArrivalTrainDate() {
		return arrivalTrainDate;
	}
	public void setArrivalTrainDate(String arrivalTrainDate) {
		this.arrivalTrainDate = arrivalTrainDate;
	}
	public String getDepartureTrainDate() {
		return departureTrainDate;
	}
	public void setDepartureTrainDate(String departureTrainDate) {
		this.departureTrainDate = departureTrainDate;
	}
	
}
