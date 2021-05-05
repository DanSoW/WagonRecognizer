package com.server.exceptions;

//*****************************************************
//Класс для обработки исключения, возникающее
//тогда, когда накладной по данному идентификационному
//номеру не найдено
//*****************************************************

public class InvoiceNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private String number;
	public InvoiceNotFoundException(String number) {
		this.number = number;
	}
	
	@Override
	public String getMessage() {
		return "Записи с номером накладной " + this.number + " не найдено!";
	}
}