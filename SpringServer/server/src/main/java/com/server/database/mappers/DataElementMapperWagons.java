package com.server.database.mappers;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.server.database.dao.DataElementDaoImpl;
import com.server.database.elements.DataElementWagons;

@Component
public class DataElementMapperWagons implements RowMapper<DataElementWagons> {
	@Override
	public DataElementWagons mapRow(ResultSet rs, int rowNum) throws SQLException{
        return new DataElementWagons(
        		rs.getInt(DataElementDaoImpl.NAME_ATTRIBUT_NUMBER_WAGON),
        		rs.getString(DataElementDaoImpl.NAME_ATTRIBUT_ARRIVAL_DATE),
        		rs.getString(DataElementDaoImpl.NAME_ATTRIBUT_IMAGE_PATH),
        		rs.getDouble(DataElementDaoImpl.NAME_ATTRIBUT_LEVEL_CORRECT));
	}
}
