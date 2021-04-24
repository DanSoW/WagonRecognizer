package com.server.controllers;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import com.sun.el.parser.ParseException;
import com.sun.istack.NotNull;

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
	
	
	//Part of valid constants
	private static short SIZE_NUMBER_WAGON = 8;
	private static short SIZE_MAX_NUMBER_INVOICE = 20;
	private static short SIZE_MIN_NUMBER_INVOICE = 2;
	
	private class SizeMinNumberInvoice{
		@NotNull
		private Short size;

		public Short getSize() {
			return size;
		}

		@SuppressWarnings("unused")
		public void setSize(Short size) {
			this.size = size;
		}
		
	}
	
	private class SizeNumberWagon{
		@NotNull
		private Short size;

		public Short getSize() {
			return size;
		}

		@SuppressWarnings("unused")
		public void setSize(Short size) {
			this.size = size;
		}
	}
	
	//*******************************************
	//Server side settings
	
	@PostMapping(value = "/settings/sizenumberwagon")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void updateMaxSizeNumberWagon(@Valid @RequestBody SizeNumberWagon request) throws Exception {
		if(request.getSize() <= 0)
			throw new Exception("Error! The number of characters in the gondola car number must not be less than or equal to zero!");
		else if(request.getSize() >= 10)
			throw new Exception("Error! The number of characters in the gondola car number can not be more than 10!");
		SIZE_NUMBER_WAGON = request.getSize();
	}
	
	@PostMapping(value = "/settings/minsizenumberinvoice")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void updateMaxSizeNumberWagon(@Valid @RequestBody SizeMinNumberInvoice request) throws Exception {
		if(request.getSize() < 2)
			throw new Exception("Error! The minimum size of the invoice ID must not be less than 2 characters!");
		else if(request.getSize() > 10)
			throw new Exception("Error! The minimum size of the invoice ID must not exceed 10 characters!");
		SIZE_MIN_NUMBER_INVOICE = request.getSize();
	}
	
	//*******************************************
	//Processing a GET request for a wagons table
	@GetMapping(value = "/wagons/get/")
	public DataElementWagons getDataElementWagons(@RequestParam("numberWagon") int numberWagon) throws Exception{
		String number = String.valueOf(numberWagon);
		if(number.length() != SIZE_NUMBER_WAGON)
			throw new Exception("Error! The wagon number must consist of " + String.valueOf(SIZE_NUMBER_WAGON) + " numbers!");
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
	public void insertDataElementWagons(@Valid @RequestBody DataElementRequestInsertWagons request) throws Exception {
		if(!(new File(request.getImagePath()).exists()))
			throw new Exception("Error! The snapshot file with this path was not found on the server!");
		
		String number = String.valueOf(request.getNumberWagon());
		if(number.length() != SIZE_NUMBER_WAGON)
			throw new Exception("Error! The wagon number must consist of " + String.valueOf(SIZE_NUMBER_WAGON) + " numbers!");
		
		List<DataElementRegister> registers = dataService.getDataElementRegisterAll();
		String numberInvoice = "";
		for(int i = 0; i < registers.size(); i++) {
			if(registers.get(i).getNumberWagon() == request.getNumberWagon()) {
				numberInvoice = registers.get(i).getFkNumberInvoice();
				break;
			}
		}
		registers.clear();
		
		if(numberInvoice.equals(""))
			throw new Exception("Error! The wagon with this number is not present in any invoice!");
		
		List<DataElementInvoices> invoices = dataService.getDataElementInvoicesAll();

		for(DataElementInvoices i : invoices) {
			if(i.getNumberInvoice().equals(numberInvoice)) {
				 DateFormat format = new SimpleDateFormat("yyyy-mm-dd");
			     if(format.parse(request.getArrivalDate()).before(
			            format.parse(i.getArrivalTrainDate())
			     )){
			    	 throw new Exception("Error! The arrival of the wagons can not be earlier than its arrival recorded in the invoice!");
			    }
			     break;
			}
		}
		invoices.clear();
		
		List<DataElementWagons> wagons = dataService.getDataElementWagonsAll();
		for(DataElementWagons i : wagons) {
			if(i.getNumberWagon() == request.getNumberWagon()) {
				throw new Exception("Error! The wagon with this ID is already registered in the database!");
			}
		}
		wagons.clear();
		
		dataService.insertDataElementWagons(
				request.getNumberWagon(),
				request.getArrivalDate(),
				request.getImagePath(),
				request.getLevelCorrectRecognize());
	}
	
	@PostMapping(value = "/wagons/update")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void updateDataElementWagons(@Valid @RequestBody DataElementRequestInsertWagons request) throws Exception {
		if(!(new File(request.getImagePath())).exists())
			throw new Exception("Error! The snapshot file with this path was not found on the server!");
		String number = String.valueOf(request.getNumberWagon());
		if(number.length() != SIZE_NUMBER_WAGON)
			throw new Exception("Error! The wagon number must consist of " + String.valueOf(SIZE_NUMBER_WAGON) + " numbers!");
		
		List<DataElementWagons> wagons = dataService.getDataElementWagonsAll();
		DataElementWagons findWagon = null;
		for(int i = 0; i < wagons.size(); i++) {
			if(wagons.get(i).getNumberWagon() == request.getNumberWagon()) {
				findWagon = wagons.get(i);
				break;
			}
		}
		wagons.clear();
		
		if(findWagon == null)
			throw new Exception("Error! An entry with this wagon number does not exist in the registration list!");
		
		List<DataElementRegister> registers = dataService.getDataElementRegisterAll();
		String numberInvoice = "";
		for(int i = 0; i < registers.size(); i++) {
			if(registers.get(i).getNumberWagon() == request.getNumberWagon()) {
				numberInvoice = registers.get(i).getFkNumberInvoice();
				break;
			}
		}
		registers.clear();
		
		if(numberInvoice.equals(""))
			throw new Exception("Error! The wagon with this number is not present in any invoice!");
		
		List<DataElementInvoices> invoices = dataService.getDataElementInvoicesAll();

		for(DataElementInvoices i : invoices) {
			if(i.getNumberInvoice().equals(numberInvoice)) {
				 DateFormat format = new SimpleDateFormat("yyyy-mm-dd");
			     if(format.parse(request.getArrivalDate()).before(
			            format.parse(i.getArrivalTrainDate())
			     )){
			    	 throw new Exception("Error! The arrival of the wagons can not be earlier than its arrival recorded in the invoice!");
			    }
			     break;
			}
		}
		invoices.clear();
		
		dataService.updateDataElementWagons(
				request.getNumberWagon(),
				request.getArrivalDate(),
				request.getImagePath(),
				request.getLevelCorrectRecognize());
	}
	
	@PostMapping(value = "/wagons/delete")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void deleteDataElementWagons(@Valid @RequestBody DataElementRequestDeleteWagons request) throws Exception {
		String number = String.valueOf(request.getNumberWagon());
		if(number.length() != SIZE_NUMBER_WAGON)
			throw new Exception("Error! The wagon number must consist of " + String.valueOf(SIZE_NUMBER_WAGON) + " numbers!");
		
		List<DataElementWagons> wagons = dataService.getDataElementWagonsAll();
		DataElementWagons findWagon = null;
		for(int i = 0; i < wagons.size(); i++) {
			if(wagons.get(i).getNumberWagon() == request.getNumberWagon()) {
				findWagon = wagons.get(i);
				break;
			}
		}
		wagons.clear();
		
		if(findWagon == null)
			throw new Exception("Error! An entry with this wagon number does not exist in the registration list!");
		
		dataService.deleteDataElementWagons(request.getNumberWagon());
	}
	
	//**************************************************
	
	//Processing a GET request for a invoices table
	@GetMapping(value = "/invoices/get/")
	public DataElementInvoices getDataElementInvoices(@RequestParam String numberInvoice) throws Exception {
		if((numberInvoice.length() < SIZE_MIN_NUMBER_INVOICE)
				|| (numberInvoice.length() > SIZE_MAX_NUMBER_INVOICE)) {
			throw new Exception("Error! The number of characters identifying the invoice numbers must be in the range ["
					+ String.valueOf(SIZE_MIN_NUMBER_INVOICE) + "; " + String.valueOf(SIZE_MAX_NUMBER_INVOICE) + "]");
		}
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
	public void insertDataElementInvoices(@Valid @RequestBody DataElementRequestInsertInvoices request) throws Exception {
		if((request.getNumberInvoice().length() < SIZE_MIN_NUMBER_INVOICE)
				|| (request.getNumberInvoice().length() > SIZE_MAX_NUMBER_INVOICE)) {
			throw new Exception("Error! The number of characters identifying the invoice numbers must be in the range ["
					+ String.valueOf(SIZE_MIN_NUMBER_INVOICE) + "; " + String.valueOf(SIZE_MAX_NUMBER_INVOICE) + "]");
		}
		
		if(request.getTotalWagons() <= 0)
			throw new Exception("Error! The value of total Wagons cannot be negative or equal to zero");
		
		try {
			DateFormat format = new SimpleDateFormat("yyyy-mm-dd");
		     if(format.parse(request.getArrivalTrainDate()).after(
		            format.parse(request.getDepartureTrainDate())
		     )){
		    	 throw new Exception("Error! The departure date cannot be later than the arrival date!");
		    }
		}catch(ParseException e) {
			throw new Exception("Error! The dates are presented in an incorrect form!");
		}
		
		List<DataElementInvoices> invoices = dataService.getDataElementInvoicesAll();
		for(DataElementInvoices i : invoices) {
			if(i.getNumberInvoice().equals(request.getNumberInvoice())) {
				throw new Exception("Error! The invoice with this ID is already present in the database!");
			}
		}
		invoices.clear();
		
		dataService.insertDataElementInvoices(
				request.getNumberInvoice(),
				request.getNameSupplier(),
				request.getTotalWagons(),
				request.getArrivalTrainDate(),
				request.getDepartureTrainDate());
	}
	
	@PostMapping(value = "/invoices/update")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void updateDataElementInvoices(@Valid @RequestBody DataElementRequestInsertInvoices request) throws Exception {
		if((request.getNumberInvoice().length() < SIZE_MIN_NUMBER_INVOICE)
				|| (request.getNumberInvoice().length() > SIZE_MAX_NUMBER_INVOICE)) {
			throw new Exception("Error! The number of characters identifying the invoice numbers must be in the range ["
					+ String.valueOf(SIZE_MIN_NUMBER_INVOICE) + "; " + String.valueOf(SIZE_MAX_NUMBER_INVOICE) + "]");
		}
		
		if(request.getTotalWagons() <= 0)
			throw new Exception("Error! The value of total Wagons cannot be negative or equal to zero");
		
		try {
			DateFormat format = new SimpleDateFormat("yyyy-mm-dd");
		     if(format.parse(request.getArrivalTrainDate()).after(
		            format.parse(request.getDepartureTrainDate())
		     )){
		    	 throw new Exception("Error! The departure date cannot be later than the arrival date!");
		    }
		}catch(ParseException e) {
			throw new Exception("Error! The dates are presented in an incorrect form!");
		}
		
		List<DataElementRegister> register = dataService.getDataElementRegisterAll();
		int count = 0;
		for(DataElementRegister i : register) {
			if(i.getFkNumberInvoice().equals(request.getNumberInvoice())) {
				count++;
			}
		}
		register.clear();
		
		if(count > request.getTotalWagons())
			throw new Exception("Error! You can not change the number of wagons to less than the already registered one!");
		
		dataService.updateDataElementInvoices(
				request.getNumberInvoice(),
				request.getNameSupplier(),
				request.getTotalWagons(),
				request.getArrivalTrainDate(),
				request.getDepartureTrainDate());
	}
	
	@PostMapping(value = "/invoices/delete")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void deleteDataElementInvoices(@Valid @RequestBody DataElementRequestDeleteInvoices request) throws Exception {
		if((request.getNumberInvoice().length() < SIZE_MIN_NUMBER_INVOICE)
				|| (request.getNumberInvoice().length() > SIZE_MAX_NUMBER_INVOICE)) {
			throw new Exception("Error! The number of characters identifying the invoice numbers must be in the range ["
					+ String.valueOf(SIZE_MIN_NUMBER_INVOICE) + "; " + String.valueOf(SIZE_MAX_NUMBER_INVOICE) + "]");
		}
		
		dataService.deleteDataElementInvoices(request.getNumberInvoice());
	}
	
	//**************************************************
	//Processing a GET request for a register table
	@GetMapping(value = "/register/get/")
	public DataElementRegister getDataElementRegister(@RequestParam String fkNumberInvoice, @RequestParam int numberWagon) throws Exception {
		if((fkNumberInvoice.length() < SIZE_MIN_NUMBER_INVOICE)
				|| (fkNumberInvoice.length() > SIZE_MAX_NUMBER_INVOICE)) {
			throw new Exception("Error! The number of characters identifying the invoice numbers must be in the range ["
					+ String.valueOf(SIZE_MIN_NUMBER_INVOICE) + "; " + String.valueOf(SIZE_MAX_NUMBER_INVOICE) + "]");
		}
		String number = String.valueOf(numberWagon);
		if(number.length() != SIZE_NUMBER_WAGON)
			throw new Exception("Error! The wagon number must consist of " + String.valueOf(SIZE_NUMBER_WAGON) + " numbers!");
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
	public void insertDataElementRegister(@Valid @RequestBody DataElementRequestInsertRegister request) throws Exception {
		if((request.getFkNumberInvoice().length() < SIZE_MIN_NUMBER_INVOICE)
				|| (request.getFkNumberInvoice().length() > SIZE_MAX_NUMBER_INVOICE)) {
			throw new Exception("Error! The number of characters identifying the invoice numbers must be in the range ["
					+ String.valueOf(SIZE_MIN_NUMBER_INVOICE) + "; " + String.valueOf(SIZE_MAX_NUMBER_INVOICE) + "]");
		}
		String number = String.valueOf(request.getNumberWagon());
		if(number.length() != SIZE_NUMBER_WAGON)
			throw new Exception("Error! The wagon number must consist of " + String.valueOf(SIZE_NUMBER_WAGON) + " numbers!");
		
		if(request.getsD() < 0)
			throw new Exception("Error! The Sd value must not be negative!");
		
		if(request.getSerialNumber() <= 0)
			throw new Exception("Error! The number in the order cannot be less than or equal to zero!");
		
		List<DataElementRegister> register = dataService.getDataElementRegisterAll();
		
		int count = 0;
		for(DataElementRegister i : register) {
			if(i.getNumberWagon() == request.getNumberWagon()) {
				throw new Exception("Error! A wagon with this ID is already registered!");
			}else if(i.getFkNumberInvoice().equals(request.getFkNumberInvoice())) {
				count++;
			}
		}
		register.clear();
		
		List<DataElementInvoices> invoice = dataService.getDataElementInvoicesAll();
		short total = 0;
		for(DataElementInvoices i : invoice) {
			if((i.getNumberInvoice().equals(request.getFkNumberInvoice())) 
					&& (i.getTotalWagons() == count))
				throw new Exception("Error! Exceeded the limit for adding wagons! There can be no more wagons " + 
					String.valueOf(count));
			
			if(i.getNumberInvoice().equals(request.getFkNumberInvoice())) {
				total = i.getTotalWagons();
				break;
			}
		}
		invoice.clear();
		
		if(request.getSerialNumber() > total)
			throw new Exception("Error! The serial number in the train can not be greater than the number of wagons present in the train!");
		
		dataService.insertDataElementRegister(
				request.getFkNumberInvoice(),
				request.getNumberWagon(),
				request.getSerialNumber(),
				request.getsD());
	}
		
	@PostMapping(value = "/register/update")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void updateDataElementRegister(@Valid @RequestBody DataElementRequestInsertRegister request) throws Exception {
		if((request.getFkNumberInvoice().length() < SIZE_MIN_NUMBER_INVOICE)
				|| (request.getFkNumberInvoice().length() > SIZE_MAX_NUMBER_INVOICE)) {
			throw new Exception("Error! The number of characters identifying the invoice numbers must be in the range ["
					+ String.valueOf(SIZE_MIN_NUMBER_INVOICE) + "; " + String.valueOf(SIZE_MAX_NUMBER_INVOICE) + "]");
		}
		String number = String.valueOf(request.getNumberWagon());
		if(number.length() != SIZE_NUMBER_WAGON)
			throw new Exception("Error! The wagon number must consist of " + String.valueOf(SIZE_NUMBER_WAGON) + " numbers!");
		
		if(request.getsD() < 0)
			throw new Exception("Error! The Sd value must not be negative!");
		
		if(request.getSerialNumber() <= 0)
			throw new Exception("Error! The number in the order cannot be less than or equal to zero!");
		
		List<DataElementInvoices> invoice = dataService.getDataElementInvoicesAll();
		short total = 0;
		for(DataElementInvoices i : invoice) {
			if(i.getNumberInvoice().equals(request.getFkNumberInvoice())) {
				total = i.getTotalWagons();
				break;
			}
		}
		invoice.clear();
		
		if(request.getSerialNumber() > total)
			throw new Exception("Error! The serial number in the train can not be greater than the number of wagons present in the train!");
		
		dataService.updateDataElementRegister(
				request.getFkNumberInvoice(),
				request.getNumberWagon(),
				request.getSerialNumber(),
				request.getsD());
	}
		
	@PostMapping(value = "/register/delete")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void deleteDataElementRegister(@Valid @RequestBody DataElementRequestDeleteRegister request) throws Exception {
		if((request.getFkNumberInvoice().length() < SIZE_MIN_NUMBER_INVOICE)
				|| (request.getFkNumberInvoice().length() > SIZE_MAX_NUMBER_INVOICE)) {
			throw new Exception("Error! The number of characters identifying the invoice numbers must be in the range ["
					+ String.valueOf(SIZE_MIN_NUMBER_INVOICE) + "; " + String.valueOf(SIZE_MAX_NUMBER_INVOICE) + "]");
		}
		String number = String.valueOf(request.getNumberWagon());
		if(number.length() != SIZE_NUMBER_WAGON)
			throw new Exception("Error! The wagon number must consist of " + String.valueOf(SIZE_NUMBER_WAGON) + " numbers!");
		
		dataService.deleteDataElementRegister(request.getFkNumberInvoice(), request.getNumberWagon());
	}
}
