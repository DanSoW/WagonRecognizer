package com.server.controllers;

import java.util.List;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.server.database.elements.DataElementInvoices;
import com.server.database.elements.DataElementRegister;
import com.server.database.elements.DataElementWagons;
import com.server.database.requests.DataElementRequestDeleteInvoices;
import com.server.database.requests.DataElementRequestDeleteRegister;
import com.server.database.requests.DataElementRequestDeleteWagons;
import com.server.database.requests.DataElementRequestInsertInvoices;
import com.server.database.requests.DataElementRequestInsertRegister;
import com.server.database.requests.DataElementRequestInsertWagons;
import com.server.database.services.DataElementService;

//***************************************
//The main controller that implements communication between the server and the client
//***************************************

@RestController
@RequestMapping(value = "/database", produces = MediaType.APPLICATION_JSON_VALUE)
public class MainController{
	private final DataElementService dataService;
	
	@Autowired
	public MainController(DataElementService element) {
		this.dataService = element;
	}
	
	
	//Processing a GET request for a wagons table
	@GetMapping(value = "/wagons/get/")
	public DataElementWagons getDataElementWagons(@RequestParam int numberWagon) {
		return dataService.getDataElementWagons(numberWagon);
	}
	
	//Get all entries from the wagons table
	@GetMapping(value = "/wagons/get/all")
	public List<DataElementWagons> getDataElementWagonsAll() {
		return dataService.getDataElementWagonsAll();
	}
	
	//Processing a POST request for a wagons table
	@PostMapping(value = "/wagons/insert")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void insertDataElementWagons(@Valid @RequestBody DataElementRequestInsertWagons request) {
		dataService.insertDataElementWagons(
				request.getNumberWagon(),
				request.getArrivalDate(),
				request.getImagePath(),
				request.getLevelCorrectRecognize());
	}
	
	@PostMapping(value = "/wagons/update")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void updateDataElementWagons(@Valid @RequestBody DataElementRequestInsertWagons request) {
		dataService.updateDataElementWagons(
				request.getNumberWagon(),
				request.getArrivalDate(),
				request.getImagePath(),
				request.getLevelCorrectRecognize());
	}
	
	@PostMapping(value = "/wagons/delete")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void deleteDataElementWagons(@Valid @RequestBody DataElementRequestDeleteWagons request) {
		dataService.deleteDataElementWagons(request.getNumberWagon());
	}
	
	//**************************************************
	
	//Processing a GET request for a invoices table
	@GetMapping(value = "/invoices/get/")
	public DataElementInvoices getDataElementInvoices(@RequestParam String numberInvoice) {
		return dataService.getDataElementInvoices(numberInvoice);
	}
	
	//Get all entries from the wagons table
	@GetMapping(value = "/invoices/get/all")
	public List<DataElementInvoices> getDataElementInvoicesAll() {
		return dataService.getDataElementInvoicesAll();
	}
	
	//Processing a POST request for a invoices table
	@PostMapping(value = "/invoices/insert")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void insertDataElementInvoices(@Valid @RequestBody DataElementRequestInsertInvoices request) {
		dataService.insertDataElementInvoices(
				request.getNumberInvoice(),
				request.getNameSupplier(),
				request.getTotalWagons(),
				request.getArrivalTrainDate(),
				request.getDepartureTrainDate());
	}
	
	@PostMapping(value = "/invoices/update")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void updateDataElementInvoices(@Valid @RequestBody DataElementRequestInsertInvoices request) {
		dataService.updateDataElementInvoices(
				request.getNumberInvoice(),
				request.getNameSupplier(),
				request.getTotalWagons(),
				request.getArrivalTrainDate(),
				request.getDepartureTrainDate());
	}
	
	@PostMapping(value = "/invoices/delete")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void deleteDataElementInvoices(@Valid @RequestBody DataElementRequestDeleteInvoices request) {
		dataService.deleteDataElementInvoices(request.getNumberInvoice());
	}
	
	//**************************************************
	//Processing a GET request for a register table
	@GetMapping(value = "/register/get/")
	public DataElementRegister getDataElementRegister(@RequestParam String fkNumberInvoice, @RequestParam int numberWagon) {
		return dataService.getDataElementRegister(fkNumberInvoice, numberWagon);
	}
		
	//Get all entries from the register table
	@GetMapping(value = "/register/get/all")
	public List<DataElementRegister> getDataElementRegisterAll() {
		return dataService.getDataElementRegisterAll();
	}
		
	//Processing a POST request for a register table
	@PostMapping(value = "/register/insert")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void insertDataElementRegister(@Valid @RequestBody DataElementRequestInsertRegister request) {
		dataService.insertDataElementRegister(
				request.getFkNumberInvoice(),
				request.getNumberWagon(),
				request.getSerialNumber(),
				request.getsD());
	}
		
	@PostMapping(value = "/register/update")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void updateDataElementRegister(@Valid @RequestBody DataElementRequestInsertRegister request) {
		dataService.updateDataElementRegister(
				request.getFkNumberInvoice(),
				request.getNumberWagon(),
				request.getSerialNumber(),
				request.getsD());
	}
		
	@PostMapping(value = "/register/delete")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void deleteDataElementRegister(@Valid @RequestBody DataElementRequestDeleteRegister request) {
		dataService.deleteDataElementRegister(request.getFkNumberInvoice(), request.getNumberWagon());
	}
}