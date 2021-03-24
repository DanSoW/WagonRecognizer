package com.server.program;

//каждый экземпляр класса DataElement является единицей данных, которая необходима
//для взаимодействия с базой данных MySQL (запись/чтение)
public class DataElement {
	private int invoicesId;		//id накладной
	private String cargo;		//груз (Sd-%)
	private String numberWagon;	//номер вагона
	private String pathImage;	//путь к изображению
	private int numberOrder;	//порядковый номер вагона
	private String dateArrival;	//фактическая дата прибытия вагона
	
	public int getInvoicesId() {
		return invoicesId;
	}
	
	public void setInvoicesId(int invoicesId) {
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
	
	public void setNumberOrder(int numberOrder) {
		this.numberOrder = numberOrder;
	}

	public String getDateArrival() {
		return dateArrival;
	}

	public void setDateArrival(String dateArrival) {
		this.dateArrival = dateArrival;
	}

}
