package com.server.database;

import java.util.List;

public interface DataElementService {
	DataElementWagons getDataElementWagons(int numberWagon);
	List<DataElementWagons> getDataElementWagonsAll();
	void insertDataElementWagons(int numberWagon, String arrivalDate, String imagePath, double levelCorrectRecognize);
	void updateDataElementWagons(int numberWagon, String arrivalDate, String imagePath, double levelCorrectRecognize);
	void deleteDataElementWagons(int numberWagon);
	
	DataElementInvoices getDataElementInvoices(String numberInvoices);
	List<DataElementInvoices> getDataElementInvoicesAll();
	void insertDataElementInvoices(String numberInvoice, String nameSupplier, 
			short totalWagons, String arrivalTrainDate, String departureTrainDate);
	void updateDataElementInvoices(String numberInvoice, String nameSupplier, 
			short totalWagons, String arrivalTrainDate, String departureTrainDate);
	void deleteDataElementInvoices(String numberInvoice);
	
	DataElementRegister getDataElementRegister(String fkNumberInvoice, int numberWagon);
	List<DataElementRegister> getDataElementRegisterAll();
	void insertDataElementRegister(String fkNumberInvoice, int numberWagon, short serialNumber, float sD);
	void updateDataElementRegister(String fkNumberInvoice, int numberWagon, short serialNumber, float sD);
	void deleteDataElementRegister(String fkNumberInvoice, int numberWagon);
}
