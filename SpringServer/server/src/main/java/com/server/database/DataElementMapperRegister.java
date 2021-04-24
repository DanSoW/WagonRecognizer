package com.server.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class DataElementMapperRegister implements RowMapper<DataElementRegister>{
	@Override
	public DataElementRegister mapRow(ResultSet rs, int rowNum) throws SQLException{
        return new DataElementRegister(
        		rs.getString(DataElementDaoImpl.NAME_ATTRIBUT_REF_NUMBER_INVOICE),
        		rs.getInt(DataElementDaoImpl.NAME_ATTRIBUT_FOR_THIS_NUMBER_WAGON),
        		rs.getBoolean(DataElementDaoImpl.NAME_ATTRIBUT_ARRIVAL_MARK),
        		rs.getShort(DataElementDaoImpl.NAME_ATTRIBUT_SERIAL_NUMBER),
        		rs.getShort(DataElementDaoImpl.NAME_ATTRIBUT_ACTUAL_SERIAL_NUMBER),
        		rs.getFloat(DataElementDaoImpl.NAME_ATTRIBUT_SD));
	}
}
