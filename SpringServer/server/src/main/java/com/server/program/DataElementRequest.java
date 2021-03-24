package com.server.program;

import com.sun.istack.NotNull;

public class DataElementRequest {
	//класс, определяющий объект, передающийся через HTTP запрос серверу
	
	@NotNull
	private Integer invoicesId;
	
	@NotNull
	private String cargo;
	
	@NotNull
	private String numberWagon;
	
	@NotNull
	private String pathImage;
	
	@NotNull
	private Integer numberOrder;
	
	@NotNull
	private String dateArrival;
	
	public int getInvoicesId() {
		return invoicesId;
	}
	
	public void setInvoicesId(Integer invoicesId) {
		this.invoicesId = invoicesId;
	}
	
	public String getCargo() {
		return cargo;
	}
	
	public void setCargo(String cargo) {
		this.cargo = cargo;
	}
	
	public String getNumberWagon() {
		return numberWagon;
	}
	
	public void setNumberWagon(String numberWagon) {
		this.numberWagon = numberWagon;
	}
	
	public String getPathImage() {
		return pathImage;
	}
	
	public void setPathImage(String pathImage) {
		this.pathImage = pathImage;
	}
	
	public int getNumberOrder() {
		return numberOrder;
	}
	
	public void setNumberOrder(Integer numberOrder) {
		this.numberOrder = numberOrder;
	}

	public String getDateArrival() {
		return dateArrival;
	}

	public void setDateArrival(String dateArrival) {
		this.dateArrival = dateArrival;
	}
}
