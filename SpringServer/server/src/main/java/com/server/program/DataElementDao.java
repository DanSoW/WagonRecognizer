package com.server.program;

import java.util.Optional;

public interface DataElementDao {
	//интерфейс, методы которого определяют взаимодействие с коллекцией элементов единиц данных DataElement
	Optional<DataElement> getDataElementById(int idWagon); 	//возвращает объект из коллекции, по его индексу
	void insertDataElement(int invoicesId, String cargo,	//добавление объекта с определённым набором данных
			String numberWagon, String pathImage, int numberOrder, String dateArrival);
}
