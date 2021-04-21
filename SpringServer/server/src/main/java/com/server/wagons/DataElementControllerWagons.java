package com.server.wagons;

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

@RestController
@RequestMapping(value = "/wagons", produces = MediaType.APPLICATION_JSON_VALUE)
public class DataElementControllerWagons{
	private final DataElementServiceWagons dataService;
	
	@Autowired
	public DataElementControllerWagons(DataElementServiceWagons element) {
		this.dataService = element;
	}
	
	@GetMapping(value = "/{numberWagon:\\d+}")
	public DataElementWagons getDataElementWagons(@PathVariable int numberWagon) {
		return dataService.getDataElementWagons(numberWagon);
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public void insertDataElementWagons(@Valid @RequestBody DataElementRequestWagons request) {
		dataService.insertDataElementWagons(
				request.getNumberWagon(),
				request.getArrivalDate(),
				request.getImagePath(),
				request.getLevelCorrectRecognize());
	}
}
