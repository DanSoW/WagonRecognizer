package com.server.database.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.server.database.dao.DataElementDaoImpl;
import com.server.database.elements.DataElementInvoices;

@Component
public class DataElementMapperInvoices implements RowMapper<DataElementInvoices>{
	@Override
	public DataElementInvoices mapRow(ResultSet rs, int rowNum) throws SQLException{
        return new DataElementInvoices(
        		rs.getString(DataElementDaoImpl.NAME_ATTRIBUT_NUMBER_INVOICES),
        		rs.getString(DataElementDaoImpl.NAME_ATTRIBUT_NAME_SUPPLIER),
        		rs.getShort(DataElementDaoImpl.NAME_ATTRIBUT_TOTAL_WAGONS),
        		rs.getString(DataElementDaoImpl.NAME_ATTRIBUT_ARRIVAL_TRAIN_DATE),
        		rs.getString(DataElementDaoImpl.NAME_ATTRIBUT_DEPARTURE_TRAIN_DATE));
	}
}
