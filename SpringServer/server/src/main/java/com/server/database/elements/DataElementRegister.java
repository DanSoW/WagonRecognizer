package com.server.database.elements;

//***************************************
//Единица данных для таблицы Register
//***************************************

public class DataElementRegister {
	private String fkNumberInvoice;			//Идентификационный номер накладной
	private int numberWagon;				//Идентификационный номер полувагона
	private boolean arrivalMark;			//Метка о прибытии
	private short serialNumber;				//Порядковый номер полувагона в составе по накладной
	private short actualSerialNumber;		//Вычисленный порядковый номер полувагона в составе (по прибытию)
	private float sD;						//Значение Sd%
	
	public DataElementRegister(String fkNumberInvoice, int numberWagon,
			boolean arrivalMark, short serialNumber, short actualSerialNumber, float sD){
		this.fkNumberInvoice = fkNumberInvoice;
		this.numberWagon = numberWagon;
		this.arrivalMark = arrivalMark;
		this.serialNumber = serialNumber;
		this.setActualSerialNumber(actualSerialNumber);
		this.sD = sD;
	}
	
	public String getFkNumberInvoice() {
		return fkNumberInvoice;
	}
	
	public void setFkNumberInvoice(String fkNumberInvoice) {
		this.fkNumberInvoice = fkNumberInvoice;
	}
	
	public int getNumberWagon() {
		return numberWagon;
	}
	
	public void setNumberWagon(int numberWagon) {
		this.numberWagon = numberWagon;
	}
	
	public boolean isArrivalMark() {
		return arrivalMark;
	}
	public void setArrivalMark(boolean arrivalMark) {
		this.arrivalMark = arrivalMark;
	}
	
	public short getSerialNumber() {
		return serialNumber;
	}
	
	public void setSerialNumber(short serialNumber) {
		this.serialNumber = serialNumber;
	}
	
	public float getsD() {
		return sD;
	}
	
	public void setsD(float sD) {
		this.sD = sD;
	}

	public short getActualSerialNumber() {
		return actualSerialNumber;
	}

	public void setActualSerialNumber(short actualSerialNumber) {
		this.actualSerialNumber = actualSerialNumber;
	}
}
