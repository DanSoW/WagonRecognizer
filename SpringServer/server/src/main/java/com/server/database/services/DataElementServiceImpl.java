package com.server.database.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.server.database.dao.DataElementDao;
import com.server.database.elements.DataElementInvoices;
import com.server.database.elements.DataElementRegister;
import com.server.database.elements.DataElementWagons;
import com.server.exceptions.InvoiceNotFoundException;
import com.server.exceptions.RegisterNotFoundException;
import com.server.exceptions.WagonNotFoundException;

import java.util.Optional;

@Primary
@Service
public class DataElementServiceImpl implements DataElementService {

	private final DataElementDao dataDao;
	
	@Autowired
	public DataElementServiceImpl(DataElementDao dDao) {
		this.dataDao = dDao;
	}
	
	@Override
	public List<DataElementWagons> getDataElementWagonsAll() {
		List<Optional<DataElementWagons>> wagons = this.dataDao.getDataElementWagonsAll();
		List<DataElementWagons> data = new ArrayList<DataElementWagons>();
		for(Optional<DataElementWagons> optional : wagons) {
			if(optional.isPresent())
				data.add(optional.get());
		}
		
		return data;
	}
	
	@Override
	public List<DataElementInvoices> getDataElementInvoicesAll() {
		List<Optional<DataElementInvoices>> invoices = this.dataDao.getDataElementInvoicesAll();
		List<DataElementInvoices> data = new ArrayList<DataElementInvoices>();
		for(Optional<DataElementInvoices> optional : invoices) {
			if(optional.isPresent())
				data.add(optional.get());
		}
		
		return data;
	}
	
	@Override
	public List<DataElementRegister> getDataElementRegisterAll() {
		List<Optional<DataElementRegister>> register = this.dataDao.getDataElementRegisterAll();
		List<DataElementRegister> data = new ArrayList<DataElementRegister>();
		for(Optional<DataElementRegister> optional : register) {
			if(optional.isPresent())
				data.add(optional.get());
		}
		
		return data;
	}
	
	@Override
	public DataElementWagons getDataElementWagons(int numberWagon){
		return this.dataDao.getDataElementWagonsByNumber(numberWagon)
				.orElseThrow(() -> new WagonNotFoundException(numberWagon));
	}

	@Override
	public DataElementInvoices getDataElementInvoices(String numberInvoices) {
		return this.dataDao.getDataElementInvoicesByNumber(numberInvoices)
				.orElseThrow(() -> new InvoiceNotFoundException(numberInvoices));
	}
	
	@Override
	public DataElementRegister getDataElementRegister(String fkNumberInvoice, int numberWagon) {
		return this.dataDao.getDataElementRegisterById(fkNumberInvoice, numberWagon)
				.orElseThrow(() -> new RegisterNotFoundException(fkNumberInvoice, numberWagon));
	}
	
	@Override
	public void insertDataElementWagons(int numberWagon, String arrivalDate, String imagePath, double levelCorrectRecognize) {
		this.dataDao.insertDataElementWagons(numberWagon, arrivalDate, imagePath, levelCorrectRecognize);
	}

	@Override
	public void insertDataElementInvoices(String numberInvoice, String nameSupplier, short totalWagons,
			String arrivalTrainDate, String departureTrainDate) {
		this.dataDao.insertDataElementInvoices(numberInvoice, nameSupplier, totalWagons, arrivalTrainDate, departureTrainDate);
	}
	
	@Override
	public void insertDataElementRegister(String fkNumberInvoice, int numberWagon, short serialNumber, float sD) {
		this.dataDao.insertDataElementRegister(fkNumberInvoice, numberWagon, serialNumber, sD);
	}

	@Override
	public void updateDataElementWagons(int numberWagon, String arrivalDate, String imagePath,
			double levelCorrectRecognize) {
		this.dataDao.updateDataElementWagons(numberWagon, arrivalDate, imagePath, levelCorrectRecognize);
	}

	@Override
	public void updateDataElementInvoices(String numberInvoice, String nameSupplier, short totalWagons,
			String arrivalTrainDate, String departureTrainDate) {
		this.dataDao.updateDataElementInvoices(numberInvoice, nameSupplier, totalWagons, arrivalTrainDate, departureTrainDate);
		
	}
	
	@Override
	public void updateDataElementRegister(String fkNumberInvoice, int numberWagon, short serialNumber, float sD) {
		this.dataDao.updateDataElementRegister(fkNumberInvoice, numberWagon, serialNumber, sD);
	}

	@Override
	public void deleteDataElementWagons(int numberWagon) {
		this.dataDao.deleteDataElementWagons(numberWagon);
	}

	@Override
	public void deleteDataElementInvoices(String numberInvoice) {
		this.dataDao.deleteDataElementInvoices(numberInvoice);
	}

	@Override
	public void deleteDataElementRegister(String fkNumberInvoice, int numberWagon) {
		this.dataDao.deleteDataElementRegister(fkNumberInvoice, numberWagon);	
	}

	@Override
	public void updateDataElementRegisterActualNumber(String fkNumberInvoice, int numberWagon,
			short actualSerialNumber) {
		this.dataDao.updateDataElementRegisterActualNumber(fkNumberInvoice, numberWagon, actualSerialNumber);
	}
}
