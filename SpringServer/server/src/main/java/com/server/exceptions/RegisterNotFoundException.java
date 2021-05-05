package com.server.exceptions;

//************************************************************
//Класс для обработки исключения, возникающее
//тогда, когда по данному идентификационному номеру накладной
//и полувагона не было найдено ни одной записи в таблице
//соответствия полувагона конкретной накладной
//************************************************************

public class RegisterNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private String numberInvoice;
	private int numberWagon;
	
	public RegisterNotFoundException(String numberInvoice, int numberWagon) {
		this.numberInvoice = numberInvoice;
		this.numberWagon = numberWagon;
	}
	
	@Override
	public String getMessage() {
		return "Записи с номером накладной " + this.numberInvoice + " и номером полувагона "
				+ this.numberWagon + " не найдено!";
	}
}