package com.server.services;

import com.server.program.DataElement;

public interface DataElementService {
	DataElement getElement(int idWagon);
	void createDataElement(int invoicesId, String cargo,
			String numberWagon, String pathImage, int numberOrder, String dateArrival);
}
