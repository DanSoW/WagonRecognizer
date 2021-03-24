package com.server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.server.exceptions.DataElementNotFoundException;
import com.server.program.DataElement;
import com.server.program.DataElementDao;

@Primary	//Primary означает то, что данный сервис является первичным (наиболее важным)
@Service
public class DataElementServiceImpl implements DataElementService {

	private final DataElementDao dataDao;
	
	@Autowired
	public DataElementServiceImpl(DataElementDao dDao) {
		this.dataDao = dDao;
	}
	
	@Override
	public DataElement getElement(int idWagon) {
		return dataDao.getDataElementById(idWagon)
				.orElseThrow(() -> new DataElementNotFoundException(idWagon));
	}

	@Override
	public void createDataElement(int invoicesId, String cargo,
			String numberWagon, String pathImage, int numberOrder, String dateArrival) {
		dataDao.insertDataElement(invoicesId, cargo, numberWagon, pathImage,
				numberOrder, dateArrival);
	}

}
