package com.server.controllers;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.server.database.requests.DataElementRequestSetting;
import com.server.database.services.DataElementService;

//*****************************************************************************
//Главный контроллер предоставляющий интерфейс для взаимодействия клиентской
//части приложения с серверной посредством POST/GET запросов
//*****************************************************************************

@RestController
//при транспортировки данных с сервера на клиент и обратно, данные представляются в JSON формате, для удобства передачи
@RequestMapping(value = "/database", produces = MediaType.APPLICATION_JSON_VALUE)
public class MainController{
	private final DataElementService dataService;
	
	@Autowired
	public MainController(DataElementService element) {
		this.dataService = element;
	}
	
	
	//Настраиваемые серверные параметры (на прямую не изменяемые в базе данных, то есть
	//при SQL командах типа UPDATE, данные параметры не могут быть изменены
	private static short SIZE_NUMBER_WAGON = 8;			//число цифр в номере полувагона
	private static short SIZE_MAX_NUMBER_INVOICE = 20;	//максимальное число цифр в номере накладной
	private static short SIZE_MIN_NUMBER_INVOICE = 2;	//минимальное число цифр в номере накладной
	
	//*******************************************
	//Реализация POST/GET запросов для настройки параметров серверной части приложения
	
	@PostMapping(value = "/settings/sizenumberwagon")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void updateMaxSizeNumberWagon(@Valid @RequestBody DataElementRequestSetting request) throws Exception {
		if(request.getSize() <= 0)
			throw new Exception("Число цифр в номере полувагона не может быть меньше нуля или равно нулю!");
		else if(request.getSize() > 10)
			throw new Exception("Число цифр в номере полувагона не может быть больше 10!");
		SIZE_NUMBER_WAGON = request.getSize();
	}
	
	@PostMapping(value = "/settings/minsizenumberinvoice")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void updateMaxSizeNumberInvoice(@Valid @RequestBody DataElementRequestSetting request) throws Exception {
		if(request.getSize() < 2)
			throw new Exception("Минимальное число символов в номере накладной не должно быть меньше 2!");
		else if(request.getSize() > 20)
			throw new Exception("Минимальное число символов в номере накладной не должно быть больше 20!");
		SIZE_MIN_NUMBER_INVOICE = request.getSize();
	}
	
	//*******************************************
	//GET запрос для получения информации об одном конкретном полувагоне
	@GetMapping(value = "/wagons/get/")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public DataElementWagons getDataElementWagons(@RequestParam("numberWagon") int numberWagon) throws Exception{
		String number = String.valueOf(numberWagon);
		if(number.length() != SIZE_NUMBER_WAGON)
			throw new Exception("Номер полувагона должен состоять из " + String.valueOf(SIZE_NUMBER_WAGON) + " цифр!");
		return dataService.getDataElementWagons(numberWagon);
	}
	
	//GET запрос для получения информации о всех полувагонах, содержащихся в таблице Wagons
	@GetMapping(value = "/wagons/get/all")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public List<DataElementWagons> getDataElementWagonsAll() {
		return dataService.getDataElementWagonsAll();
	}
	
	//POST запрос для добавления информации об одном конкретном полувагоне в таблицу Wagons
	@PostMapping(value = "/wagons/insert")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void insertDataElementWagons(@Valid @RequestBody DataElementRequestInsertWagons request) throws Exception {
		if(!((new File(request.getImagePath())).exists())) {
			String[] strs = request.getImagePath().split("\\\\");
			File f = new File(FileLoadController.nameDirectory + "\\" + strs[strs.length-1]);
			if(!f.exists())
				throw new Exception("Изображение с данным именем не найдено в локальном хранилище сервера!");
		}
		
		String number = String.valueOf(request.getNumberWagon());
		if(number.length() != SIZE_NUMBER_WAGON)
			throw new Exception("Номер полувагона должен состоять из " + String.valueOf(SIZE_NUMBER_WAGON) + " цифр!");
		
		List<DataElementRegister> registers = dataService.getDataElementRegisterAll();
		String numberInvoice = "";
		for(int i = 0; i < registers.size(); i++) {
			if(registers.get(i).getNumberWagon() == request.getNumberWagon()) {
				numberInvoice = registers.get(i).getFkNumberInvoice();
				break;
			}
		}
		
		if(numberInvoice.equals(""))
			throw new Exception("Полувагон с данным идентификационным номером не зарегистрирован ни в одной накладной!");
		
		short max = 0;
		for(int i = 0; i < registers.size(); i++) {
			if(registers.get(i).getFkNumberInvoice().equals(numberInvoice)){
				if(registers.get(i).getActualSerialNumber() > max) {
					max = registers.get(i).getActualSerialNumber();
				}
			}
		}
		registers.clear();
		
		List<DataElementInvoices> invoices = dataService.getDataElementInvoicesAll();

		for(DataElementInvoices i : invoices) {
			if(i.getNumberInvoice().equals(numberInvoice)) {
				 DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				 boolean flag = false;
				 try {
					 flag = format.parse(request.getArrivalDate()).before(
					            format.parse(i.getDepartureTrainDate()));
				 }catch(Exception e) {
					 throw new Exception("Дата прибытия полувагона представлена в не корректной форме!"
								+ " Форма представления должна быть в формате гггг-мм-дд");
				 }
				 
			     if(flag == true){
			    	 throw new Exception("Прибытие полувагона не может быть раньше, чем отправка всего состава по данной накладной!");
			     }
			     break;
			}
		}
		invoices.clear();
		
		List<DataElementWagons> wagons = dataService.getDataElementWagonsAll();
		for(DataElementWagons i : wagons) {
			if(i.getNumberWagon() == request.getNumberWagon()) {
				throw new Exception("Полувагон с данным идентификационным номером уже зарегистрирован в базе данных!");
			}
		}
		wagons.clear();
		
		dataService.insertDataElementWagons(
				request.getNumberWagon(),
				request.getArrivalDate(),
				request.getImagePath(),
				request.getLevelCorrectRecognize());
		
		dataService.updateDataElementRegisterActualNumber(numberInvoice, request.getNumberWagon(), (short)(max+1));
	}
	
	//POST запрос для обновления одной конкретной записи в таблице Wagons
	@PostMapping(value = "/wagons/update")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void updateDataElementWagons(@Valid @RequestBody DataElementRequestInsertWagons request) throws Exception {
		if(!((new File(request.getImagePath())).exists())) {
			String[] strs = request.getImagePath().split("\\\\");
			File f = new File(FileLoadController.nameDirectory + "\\" + strs[strs.length-1]);
			if(!f.exists())
				throw new Exception("Изображение с данным именем не найдено в локальном хранилище сервера!");
		}
		
		String number = String.valueOf(request.getNumberWagon());
		if(number.length() != SIZE_NUMBER_WAGON)
			throw new Exception("Номер полувагона должен состоять из " + String.valueOf(SIZE_NUMBER_WAGON) + " цифр!");
		
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
			throw new Exception("Полувагона с данным идентификационным номером не обнаружено в таблице прибывших полувагонов!");
		
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
			throw new Exception("Полувагон с данным идентификационным номером не зарегистрирован ни в одной накладной!");
		
		List<DataElementInvoices> invoices = dataService.getDataElementInvoicesAll();

		for(DataElementInvoices i : invoices) {
			if(i.getNumberInvoice().equals(numberInvoice)) {
				 DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				 boolean flag = false;
				 try {
					 flag = format.parse(request.getArrivalDate()).before(
					            format.parse(i.getDepartureTrainDate()));
				 }catch(Exception e) {
					 throw new Exception("Дата прибытия полувагона представлена в не корректной форме!"
								+ " Форма представления должна быть в формате гггг-мм-дд");
				 }
				 
			     if(flag == true){
			    	 throw new Exception("Прибытие полувагона не может быть раньше, чем отправка всего состава по данной накладной!");
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
	
	//POST запрос для удаления одной конкретной записи из таблицы Wagons
	@PostMapping(value = "/wagons/delete")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void deleteDataElementWagons(@Valid @RequestBody DataElementRequestDeleteWagons request) throws Exception {
		String number = String.valueOf(request.getNumberWagon());
		if(number.length() != SIZE_NUMBER_WAGON)
			throw new Exception("Номер полувагона должен состоять из " + String.valueOf(SIZE_NUMBER_WAGON) + " цифр!");
		
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
			throw new Exception("Полувагона с данным идентификационным номером не обнаружено в таблице прибывших полувагонов!");
		
		List<DataElementRegister> registers = dataService.getDataElementRegisterAll();
		int index = (-1);
		for(int i = 0; i < registers.size(); i++) {
			if(registers.get(i).getNumberWagon() == request.getNumberWagon()) {
				index = i;
				break;
			}
		}
		
		if(index >= 0) {
			dataService.updateDataElementRegisterActualNumber(registers.get(index).getFkNumberInvoice(), request.getNumberWagon(), (short)0);
		}
		
		(new File(findWagon.getImagePath())).delete(); 		//Удаление файла с изображением полувагона из локального хранилища сервера
		dataService.deleteDataElementWagons(request.getNumberWagon());
	}
	
	//**************************************************
	
	//GET запрос для получения информации об одной конкретной записи из таблицы Invoices
	@GetMapping(value = "/invoices/get/")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public DataElementInvoices getDataElementInvoices(@RequestParam String numberInvoice) throws Exception {
		if((numberInvoice.length() < SIZE_MIN_NUMBER_INVOICE)
				|| (numberInvoice.length() > SIZE_MAX_NUMBER_INVOICE)) {
			throw new Exception("Идентификационный номер накладной должен содержать общее число символов из диапазона ["
					+ String.valueOf(SIZE_MIN_NUMBER_INVOICE) + "; " + String.valueOf(SIZE_MAX_NUMBER_INVOICE) + "]");
		}
		return dataService.getDataElementInvoices(numberInvoice);
	}
	
	//GET запрос для получения информации о всех накладных, содержащихся в таблице Invoices
	@GetMapping(value = "/invoices/get/all")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public List<DataElementInvoices> getDataElementInvoicesAll() {
		return dataService.getDataElementInvoicesAll();
	}
	
	//POST запрос для добавления конкретной записи в таблицу Invoices
	@PostMapping(value = "/invoices/insert")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void insertDataElementInvoices(@Valid @RequestBody DataElementRequestInsertInvoices request) throws Exception {
		if((request.getNumberInvoice().length() < SIZE_MIN_NUMBER_INVOICE)
				|| (request.getNumberInvoice().length() > SIZE_MAX_NUMBER_INVOICE)) {
			throw new Exception("Идентификационный номер накладной должен содержать общее число символов из диапазона ["
					+ String.valueOf(SIZE_MIN_NUMBER_INVOICE) + "; " + String.valueOf(SIZE_MAX_NUMBER_INVOICE) + "]");
		}
		
		if(request.getTotalWagons() <= 0)
			throw new Exception("Общее число прибывающих полувагонов по накладной не может быть меньше либо равно нулю!");
		
		try {
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		     if(format.parse(request.getArrivalTrainDate()).before(
		            format.parse(request.getDepartureTrainDate())
		     )){
		    	 throw new Exception("Дата приезда состава не может быть раньше даты отправки состава!");
		    }
		}catch(Exception e) {
			throw new Exception("Дата приезда или отправки состава представлена в не корректной форме!"
					+ " Форма представления должна быть в формате гггг-мм-дд");
		}
		
		List<DataElementInvoices> invoices = dataService.getDataElementInvoicesAll();
		for(DataElementInvoices i : invoices) {
			if(i.getNumberInvoice().equals(request.getNumberInvoice())) {
				throw new Exception("Идентификационный номер данной накладной уже присутствует в базе данных!");
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
	
	//POST запрос для обновления одной конкретной записи из таблицы Invoices
	@PostMapping(value = "/invoices/update")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void updateDataElementInvoices(@Valid @RequestBody DataElementRequestInsertInvoices request) throws Exception {
		if((request.getNumberInvoice().length() < SIZE_MIN_NUMBER_INVOICE)
				|| (request.getNumberInvoice().length() > SIZE_MAX_NUMBER_INVOICE)) {
			throw new Exception("Идентификационный номер накладной должен содержать общее число символов из диапазона ["
					+ String.valueOf(SIZE_MIN_NUMBER_INVOICE) + "; " + String.valueOf(SIZE_MAX_NUMBER_INVOICE) + "]");
		}
		
		if(request.getTotalWagons() <= 0)
			throw new Exception("Общее число прибывающих полувагонов по накладной не может быть меньше либо равно нулю!");
		
		try {
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		     if(format.parse(request.getArrivalTrainDate()).before(
		            format.parse(request.getDepartureTrainDate())
		     )){
		    	 throw new Exception("Дата приезда состава не может быть раньше даты отправки состава!");
		    }
		}catch(Exception e) {
			throw new Exception("Дата приезда или отправки состава представлена в не корректной форме!"
					+ " Форма представления должна быть в формате гггг-мм-дд");
		}
		
		List<DataElementRegister> reg = dataService.getDataElementRegisterAll();
		short count = 0;
		short maxSerialNumber = 0;
		for(DataElementRegister i : reg) {
			if(i.getFkNumberInvoice().equals(request.getNumberInvoice())) {
				count++;
				if(maxSerialNumber < i.getSerialNumber())
					maxSerialNumber = i.getSerialNumber();
			}
		}
		reg.clear();
		
		if((count > request.getTotalWagons()) || (maxSerialNumber > request.getTotalWagons()))
			throw new Exception("Общее число полувагонов не должно быть меньше числа полувагонов, которые уже зарегистрированны по данной"
					+ " накладной!");
		
		dataService.updateDataElementInvoices(
				request.getNumberInvoice(),
				request.getNameSupplier(),
				request.getTotalWagons(),
				request.getArrivalTrainDate(),
				request.getDepartureTrainDate());
	}
	
	//POST запрос для удаления одной конкретной записи из таблицы Invoices
	@PostMapping(value = "/invoices/delete")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void deleteDataElementInvoices(@Valid @RequestBody DataElementRequestDeleteInvoices request) throws Exception {
		if((request.getNumberInvoice().length() < SIZE_MIN_NUMBER_INVOICE)
				|| (request.getNumberInvoice().length() > SIZE_MAX_NUMBER_INVOICE)) {
			throw new Exception("Идентификационный номер накладной должен содержать общее число символов из диапазона ["
					+ String.valueOf(SIZE_MIN_NUMBER_INVOICE) + "; " + String.valueOf(SIZE_MAX_NUMBER_INVOICE) + "]");
		}
		
		List<DataElementRegister> registers = dataService.getDataElementRegisterAll();
		for(int i = 0; i < registers.size(); i++) {
			if(registers.get(i).getFkNumberInvoice().equals(request.getNumberInvoice())) {
				dataService.deleteDataElementRegister(registers.get(i).getFkNumberInvoice(),
						registers.get(i).getNumberWagon());
			}
		}
		registers.clear();
		
		dataService.deleteDataElementInvoices(request.getNumberInvoice());
	}
	
	//**************************************************
	//GET запрос для получения информации об одной конкретной записи из таблицы Register
	@GetMapping(value = "/register/get/")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public DataElementRegister getDataElementRegister(@RequestParam String fkNumberInvoice, @RequestParam int numberWagon) throws Exception {
		if((fkNumberInvoice.length() < SIZE_MIN_NUMBER_INVOICE)
				|| (fkNumberInvoice.length() > SIZE_MAX_NUMBER_INVOICE)) {
			throw new Exception("Идентификационный номер накладной должен содержать общее число символов из диапазона ["
					+ String.valueOf(SIZE_MIN_NUMBER_INVOICE) + "; " + String.valueOf(SIZE_MAX_NUMBER_INVOICE) + "]");
		}
		String number = String.valueOf(numberWagon);
		if(number.length() != SIZE_NUMBER_WAGON)
			throw new Exception("Номер полувагона должен состоять из " + String.valueOf(SIZE_NUMBER_WAGON) + " цифр!");
		return dataService.getDataElementRegister(fkNumberInvoice, numberWagon);
	}
		
	//GET запрос для получения информации о всех записях из таблицы Register
	@GetMapping(value = "/register/get/all")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public List<DataElementRegister> getDataElementRegisterAll() {
		return dataService.getDataElementRegisterAll();
	}
	
	private class DataElementNumberWagon{
		private int numberWagon;
		
		public DataElementNumberWagon(int numberWagon) {
			this.numberWagon = numberWagon;
		}

		@SuppressWarnings("unused")
		public int getNumberWagon() {
			return numberWagon;
		}

		@SuppressWarnings("unused")
		public void setNumberWagon(int numberWagon) {
			this.numberWagon = numberWagon;
		}
	}
	
	//GET запрос для получения информации о всех зарегистрированных номерах полувагонов из таблицы Register
	@GetMapping(value = "/register/get/all/numbers")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public List<DataElementNumberWagon> getDataNumberWagonRegisterAll() {
		List<DataElementRegister> register = dataService.getDataElementRegisterAll();
		List<DataElementNumberWagon> numbers = new ArrayList<DataElementNumberWagon>();
		for(DataElementRegister i : register) {
			if(!i.isArrivalMark()) {
				numbers.add(new DataElementNumberWagon(i.getNumberWagon()));
			}
		}
		
		return numbers;
	}
		
	//POST запрос для добавления информации об одной накладной и полувагоне, зарегистрированного в данной накладной
	@PostMapping(value = "/register/insert")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void insertDataElementRegister(@Valid @RequestBody DataElementRequestInsertRegister request) throws Exception {
		if((request.getFkNumberInvoice().length() < SIZE_MIN_NUMBER_INVOICE)
				|| (request.getFkNumberInvoice().length() > SIZE_MAX_NUMBER_INVOICE)) {
			throw new Exception("Идентификационный номер накладной должен содержать общее число символов из диапазона ["
					+ String.valueOf(SIZE_MIN_NUMBER_INVOICE) + "; " + String.valueOf(SIZE_MAX_NUMBER_INVOICE) + "]");
		}
		String number = String.valueOf(request.getNumberWagon());
		if(number.length() != SIZE_NUMBER_WAGON)
			throw new Exception("Номер полувагона должен состоять из " + String.valueOf(SIZE_NUMBER_WAGON) + " цифр!");
		
		if(request.getsD() < 0)
			throw new Exception("Значение Sd% не может быть меньше нуля!");
		
		if(request.getSerialNumber() <= 0)
			throw new Exception("Порядковый номер полувагона в составе не может быть меньше либо равен 0!");
		
		List<DataElementRegister> register = dataService.getDataElementRegisterAll();
		
		int count = 0;
		for(DataElementRegister i : register) {
			if(i.getNumberWagon() == request.getNumberWagon()) {
				throw new Exception("Полувагон с данным идентификационным номером уже зарегистрирован в таблице соответствия полувагонов"
						+ " накладным!");
			}
			
			if((i.getSerialNumber() == request.getSerialNumber()) && (i.getFkNumberInvoice().equals(request.getFkNumberInvoice()))) {
				throw new Exception("Полувагон с данным порядковым номером уже присутствует в таблице соответствия полувагонов накладным!");
			}
			
			if(i.getFkNumberInvoice().equals(request.getFkNumberInvoice())) {
				count++;
			}
		}
		register.clear();
		
		List<DataElementInvoices> invoice = dataService.getDataElementInvoicesAll();
		short total = 0;
		for(DataElementInvoices i : invoice) {
			if((i.getNumberInvoice().equals(request.getFkNumberInvoice())) 
					&& (i.getTotalWagons() == count))
				throw new Exception("Превышен лимит добавления информации о полувагонах к накладной! Записей может быть не более " + 
					String.valueOf(count));
			
			if(i.getNumberInvoice().equals(request.getFkNumberInvoice())) {
				total = i.getTotalWagons();
				break;
			}
		}
		invoice.clear();
		
		if(total == 0) {
			throw new Exception("Накладной с данным идентификатором нет в базе данных!");
		}
		
		if(request.getSerialNumber() > total)
			throw new Exception("Порядковый номер полувагона в составе не может превышать общего числа полувагонов прибывающих "
					+ "по данной накладной!");
		
		dataService.insertDataElementRegister(
				request.getFkNumberInvoice(),
				request.getNumberWagon(),
				request.getSerialNumber(),
				request.getsD());
	}

	//POST запрос для обновление одной конкретной записи из таблицы Register
	@PostMapping(value = "/register/update")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void updateDataElementRegister(@Valid @RequestBody DataElementRequestInsertRegister request) throws Exception {
		if((request.getFkNumberInvoice().length() < SIZE_MIN_NUMBER_INVOICE)
				|| (request.getFkNumberInvoice().length() > SIZE_MAX_NUMBER_INVOICE)) {
			throw new Exception("Идентификационный номер накладной должен содержать общее число символов из диапазона ["
					+ String.valueOf(SIZE_MIN_NUMBER_INVOICE) + "; " + String.valueOf(SIZE_MAX_NUMBER_INVOICE) + "]");
		}
		String number = String.valueOf(request.getNumberWagon());
		if(number.length() != SIZE_NUMBER_WAGON)
			throw new Exception("Номер полувагона должен состоять из " + String.valueOf(SIZE_NUMBER_WAGON) + " цифр!");
		
		if(request.getsD() < 0)
			throw new Exception("Значение Sd% не может быть меньше нуля!");
		
		if(request.getSerialNumber() <= 0)
			throw new Exception("Порядковый номер полувагона в составе не может быть меньше либо равен 0!");
		
		List<DataElementInvoices> invoice = dataService.getDataElementInvoicesAll();
		short total = 0;
		for(DataElementInvoices i : invoice) {
			if(i.getNumberInvoice().equals(request.getFkNumberInvoice())) {
				total = i.getTotalWagons();
				break;
			}
		}
		invoice.clear();
		
		if(total == 0) {
			throw new Exception("Накладной с данным идентификатором нет в базе данных!");
		}
		
		if(request.getSerialNumber() > total)
			throw new Exception("Порядковый номер полувагона в составе не может превышать общего числа полувагонов прибывающих "
					+ "по данной накладной!");
		
		List<DataElementRegister> register = dataService.getDataElementRegisterAll();
		boolean flag = false;
		for(DataElementRegister i : register) {
			if((i.getSerialNumber() != request.getSerialNumber())
					&& (i.getNumberWagon() == request.getNumberWagon())) {
				throw new Exception("Порядковый номер полувагона не изменяется! Чтобы изменить порядковый номер полувагона"
						+ " необходимо удалить запись с данным порядковым номером и добавить на её основе новую, с новым порядковым номером!");
			}
			
			if((i.getNumberWagon() == request.getNumberWagon()) && (i.getFkNumberInvoice().equals(request.getFkNumberInvoice()))) {
				flag = true;
			}
		}
		
		if(!flag) {
			throw new Exception("Записи с данным номером накладной и номером полувагона не присутствует в таблице регистрации!");
		}
		
		
		register.clear();
		
		dataService.updateDataElementRegister(
				request.getFkNumberInvoice(),
				request.getNumberWagon(),
				request.getSerialNumber(),
				request.getsD());
	}
	
	private class DataAnswer{
		private Boolean answer;
		
		public DataAnswer(Boolean answer) {
			super();
			this.answer = answer;
		}

		@SuppressWarnings("unused")
		public Boolean getAnswer() {
			return answer;
		}

		@SuppressWarnings("unused")
		public void setAnswer(Boolean answer) {
			this.answer = answer;
		}
	}
	
	@GetMapping(value = "/register/numberwagon/is")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public DataAnswer isNumberWagon(@RequestParam Integer numberWagon) {
		List<DataElementRegister> registers = dataService.getDataElementRegisterAll();
		for(DataElementRegister i : registers) {
			if(i.getNumberWagon() == numberWagon) {
				return new DataAnswer(true);
			}
		}
		
		return new DataAnswer(false);
	}
		
	//POST запрос для удаления конкретной записи в таблице соответствия накладной конкретному полувагону (Register)
	@PostMapping(value = "/register/delete")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void deleteDataElementRegister(@Valid @RequestBody DataElementRequestDeleteRegister request) throws Exception {
		if((request.getFkNumberInvoice().length() < SIZE_MIN_NUMBER_INVOICE)
				|| (request.getFkNumberInvoice().length() > SIZE_MAX_NUMBER_INVOICE)) {
			throw new Exception("Идентификационный номер накладной должен содержать общее число символов из диапазона ["
					+ String.valueOf(SIZE_MIN_NUMBER_INVOICE) + "; " + String.valueOf(SIZE_MAX_NUMBER_INVOICE) + "]");
		}
		String number = String.valueOf(request.getNumberWagon());
		if(number.length() != SIZE_NUMBER_WAGON)
			throw new Exception("Номер полувагона должен состоять из " + String.valueOf(SIZE_NUMBER_WAGON) + " цифр!");
		
		List<DataElementRegister> registers = dataService.getDataElementRegisterAll();
		int index = (-1);
		for(int i = 0; i < registers.size(); i++) {
			if((registers.get(i).getFkNumberInvoice().equals(request.getFkNumberInvoice()))
					&& (registers.get(i).getNumberWagon() == request.getNumberWagon())) {
				index = i;
				break;
			}
		}
		
		if(index < 0)
			throw new Exception("Полувагон с данным идентификационным номером по данной накладной не зарегистрирован!");
		
		short actualSerialNumber = registers.get(index).getActualSerialNumber();
		
		dataService.deleteDataElementWagons(registers.get(index).getNumberWagon());
		dataService.deleteDataElementRegister(request.getFkNumberInvoice(), request.getNumberWagon());
		
		registers = dataService.getDataElementRegisterAll();
		for(int i = 0; i < registers.size(); i++) {
			if(registers.get(i).getFkNumberInvoice().equals(request.getFkNumberInvoice())
					&& (registers.get(i).getActualSerialNumber() > actualSerialNumber)) {
				dataService.updateDataElementRegisterActualNumber(registers.get(i).getFkNumberInvoice(), 
						registers.get(i).getNumberWagon(), (short)(registers.get(i).getActualSerialNumber() - 1));
			}
		}
	}
}
