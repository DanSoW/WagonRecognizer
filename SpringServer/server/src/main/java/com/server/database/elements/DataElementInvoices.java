package com.server.database.elements;

//***************************************
//Единица данных для таблицы Invoices
//***************************************

public class DataElementInvoices {
	private String numberInvoice;			//Идентификационный номер накладной
	private String nameSupplier;			//Название поставщика
	private short totalWagons;				//Общее число полувагонов
	private String arrivalTrainDate;		//Дата прибытия состава полувагонов
	private String departureTrainDate;		//Дата отправки состава полувагонов
	
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
