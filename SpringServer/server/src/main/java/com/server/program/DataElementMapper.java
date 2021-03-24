package com.server.program;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class DataElementMapper implements RowMapper<DataElement> {
	//RowMapper<DataElement> обрабатывает отдельно каждую запись, полученную из БД, 
	//и возвращает уже готовый объект - модель данных
	//в данном случае, класс DataElementMapper переопределяет функцию mapRow() класса RowMapper, и формирует
	//объект, которую возвращает.
	@Override
	public DataElement mapRow(ResultSet rs, int rowNum) throws SQLException{
		DataElement element = new DataElement();
		element.setInvoicesId(rs.getInt("invoices_ID"));
		element.setCargo(rs.getString("cargo"));
		element.setNumberWagon(rs.getString("numberWagon"));
		element.setPathImage(rs.getString("pathImage"));
		element.setNumberOrder(rs.getInt("numberOrder"));
		element.setDateArrival(rs.getString("dateArrival"));
        return element;
	}
}
