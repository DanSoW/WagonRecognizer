package com.server.wagons;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class DataElementMapperWagons implements RowMapper<DataElementWagons> {
	@Override
	public DataElementWagons mapRow(ResultSet rs, int rowNum) throws SQLException{
        return new DataElementWagons(
        		rs.getInt(DataElementDaoImplWagons.NAME_ATTRIBUT_NUMBER_WAGON),
        		rs.getString(DataElementDaoImplWagons.NAME_ATTRIBUT_ARRIVAL_DATE),
        		rs.getString(DataElementDaoImplWagons.NAME_ATTRIBUT_IMAGE_PATH),
        		rs.getDouble(DataElementDaoImplWagons.NAME_ATTRIBUT_LEVEL_CORRECT));
	}
}
