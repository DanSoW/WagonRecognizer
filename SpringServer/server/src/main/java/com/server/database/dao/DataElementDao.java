package com.server.database.dao;

import java.util.List;
import java.util.Optional;

import com.server.database.elements.DataElementInvoices;
import com.server.database.elements.DataElementRegister;
import com.server.database.elements.DataElementWagons;

public interface DataElementDao {
	//****************************************
	//Возвращение информации о конкретном полувагоне по его идентификационному номеру
	Optional<DataElementWagons> getDataElementWagonsByNumber(int numberWagon);
	
	//Возвращение информации о всех полувагонах, содержащихся в таблице Wagons
	List<Optional<DataElementWagons>> getDataElementWagonsAll();
	
	//Добавление информации о полувагоне в таблицу Wagons
	void insertDataElementWagons(int numberWagon, String arrivalDate, String imagePath, double levelCorrectRecognize);
	
	//Обновление одной конкретной записи о полувагоне в таблице Wagons
	void updateDataElementWagons(int numberWagon, String arrivalDate, String imagePath, double levelCorrectRecognize);
	
	//Удаление одной конкретной записи о полувагоне, по его идентификационному номеру
	void deleteDataElementWagons(int numberWagon);
	
	//****************************************
	//Возвращение информации о конкретной накладной по её идентификационному номеру
	Optional<DataElementInvoices> getDataElementInvoicesByNumber(String numberInvoices);
	
	//Возвращение информации о всех накладных, содержащихся в таблице Invoices
	List<Optional<DataElementInvoices>> getDataElementInvoicesAll();
	
	//Добавление информации о накладной в таблицу Invoices
	void insertDataElementInvoices(String numberInvoice, String nameSupplier, short totalWagons, 
			String arrivalTrainDate, String departureTrainDate);
	
	//Обновление одной конкретной записи о накладной в таблице Invoices
	void updateDataElementInvoices(String numberInvoice, String nameSupplier, short totalWagons, 
			String arrivalTrainDate, String departureTrainDate);
	
	//Удаление одной конкретной записи о накладной, по её идентификационному номеру
	void deleteDataElementInvoices(String numberInvoice);
	
	//****************************************
	//Возвращение информации об одной записи в таблице соответствия полувагонов накладным
	Optional<DataElementRegister> getDataElementRegisterById(String fkNumberInvoice, int numberWagon);
		
	//Возвращение информации о всех записях в таблице соответствия полувагонов накладным
	List<Optional<DataElementRegister>> getDataElementRegisterAll();
		
	//Добавление информации о накладной и полувагоне
	void insertDataElementRegister(String fkNumberInvoice, int numberWagon, short serialNumber, float sD);
		
	//Обновление информации о накладной и полувагоне
	void updateDataElementRegister(String fkNumberInvoice, int numberWagon, short serialNumber, float sD);
		
	//Удаление записи о накладной и полувагоне
	void deleteDataElementRegister(String fkNumberInvoice, int numberWagon);
	
	//Обновление значения актуального порядкового номера (вычисляется по прибытии полувагона)
	void updateDataElementRegisterActualNumber(String fkNumberInvoice, int numberWagon, short actualSerialNumber);
}
