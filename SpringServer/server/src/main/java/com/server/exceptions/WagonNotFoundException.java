package com.server.exceptions;

//*****************************************************
//Класс для обработки исключения, возникающее тогда,
//когда не было найдено ни одной записи с данным
//идентификационным номером полувагона
//*****************************************************

public class WagonNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private int id;
	public WagonNotFoundException(int id) {
		this.id = id;
	}
	
	@Override
	public String getMessage() {
		return "Записи с номером полувагона " + id
				+ " не найдено!";
	}
}
