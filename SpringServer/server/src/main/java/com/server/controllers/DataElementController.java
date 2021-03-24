package com.server.controllers;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.server.program.DataElement;
import com.server.program.DataElementRequest;
import com.server.services.DataElementService;

@RestController
@RequestMapping(value = "/element", produces = MediaType.APPLICATION_JSON_VALUE)
public class DataElementController {
	private final DataElementService dataService;
	
	@Autowired
	public DataElementController(DataElementService element) {
		this.dataService = element;
	}
	
	@GetMapping(value = "/{idWagon:\\d+}")
	public DataElement getElement(@PathVariable int idWagon) {
		return dataService.getElement(idWagon);
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public void createDataElement(@Valid @RequestBody DataElementRequest request) {
		//обработка POST-запроса, с помощью которого в базу данных заносится информация о прибытии вагона
		dataService.createDataElement(
				request.getInvoicesId(),
				request.getCargo(),
				request.getNumberWagon(),
				request.getPathImage(),
				request.getNumberOrder(),
				request.getDateArrival());
	}
}
