package com.server.database.requests;

import com.sun.istack.NotNull;

public class DataElementRequestDeleteInvoices {
	@NotNull
	private String numberInvoice;

	public String getNumberInvoice() {
		return numberInvoice;
	}

	public void setNumberInvoice(String numberInvoice) {
		this.numberInvoice = numberInvoice;
	}
}
