package com.server.database.requests;

import com.sun.istack.NotNull;

public class DataElementRequestInsertRegister {
	@NotNull
	private String fkNumberInvoice;
	
	@NotNull
	private Integer numberWagon;

	@NotNull
	private Short serialNumber;

	@NotNull
	private Float sD;

	public String getFkNumberInvoice() {
		return fkNumberInvoice;
	}

	public void setFkNumberInvoice(String fkNumberInvoice) {
		this.fkNumberInvoice = fkNumberInvoice;
	}

	public Short getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(Short serialNumber) {
		this.serialNumber = serialNumber;
	}

	public Float getsD() {
		return sD;
	}

	public void setsD(Float sD) {
		this.sD = sD;
	}
	
	public Integer getNumberWagon() {
		return numberWagon;
	}

	public void setNumberWagon(Integer numberWagon) {
		this.numberWagon = numberWagon;
	}
}
