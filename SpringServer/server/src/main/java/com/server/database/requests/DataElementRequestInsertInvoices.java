package com.server.database.requests;

import com.sun.istack.NotNull;

public class DataElementRequestInsertInvoices {
	@NotNull
	private String numberInvoice;

	@NotNull
	private String nameSupplier;
	
	@NotNull
	private Short totalWagons;
	
	@NotNull
	private String arrivalTrainDate;
	
	@NotNull
	private String departureTrainDate;
	
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

	public Short getTotalWagons() {
		return totalWagons;
	}

	public void setTotalWagons(Short totalWagons) {
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
