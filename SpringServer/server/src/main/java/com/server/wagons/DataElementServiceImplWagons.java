package com.server.wagons;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service
public class DataElementServiceImplWagons implements DataElementServiceWagons {

	private final DataElementDaoWagons dataDao;
	
	@Autowired
	public DataElementServiceImplWagons(DataElementDaoWagons dDao) {
		this.dataDao = dDao;
	}
	
	@Override
	public DataElementWagons getDataElementWagons(int numberWagon) {
		return dataDao.getDataElementWagonsByNumber(numberWagon)
				.orElseThrow(() -> new WagonNotFoundException(numberWagon));
	}

	@Override
	public void insertDataElementWagons(int numberWagon, String arrivalDate, String imagePath, double levelCorrectRecognize) {
		dataDao.insertDataElementWagons(numberWagon, arrivalDate, imagePath, levelCorrectRecognize);
	}
}
