package com.server.wagons;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DataElementDaoImplWagons implements DataElementDaoWagons {

	//Main table name
	private static final String NAME_WAGONS_TABLE = "Wagons";
	
	//Attributes
	public static final String NAME_ATTRIBUT_NUMBER_WAGON = "numberWagon";
	public static final String NAME_ATTRIBUT_ARRIVAL_DATE = "arrivalDate";
	public static final String NAME_ATTRIBUT_IMAGE_PATH = "imagePath";
	public static final String NAME_ATTRIBUT_LEVEL_CORRECT = "levelCorrectRecognize";
	
	//Queries to the table in the format of SQL commands
	private static final String SQL_GET_DATA_BY_NUMBER = 
			"SELECT * FROM " + NAME_WAGONS_TABLE + " WHERE " + NAME_ATTRIBUT_NUMBER_WAGON + " = :" + NAME_ATTRIBUT_NUMBER_WAGON;
	private static final String SQL_INSERT_DATA =
			"INSERT INTO " + NAME_WAGONS_TABLE + " (" 
	+ NAME_ATTRIBUT_NUMBER_WAGON + ", "
	+ NAME_ATTRIBUT_ARRIVAL_DATE + ", "
	+ NAME_ATTRIBUT_IMAGE_PATH + ", "
	+ NAME_ATTRIBUT_LEVEL_CORRECT + ") "
	+ "VALUES ("
	+ ":" + NAME_ATTRIBUT_NUMBER_WAGON + ", "
	+ ":" + NAME_ATTRIBUT_ARRIVAL_DATE + ", "
	+ ":" + NAME_ATTRIBUT_IMAGE_PATH + ", "
	+ ":" + NAME_ATTRIBUT_LEVEL_CORRECT + ")";
	
	private static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + NAME_WAGONS_TABLE + " ( " +
	NAME_ATTRIBUT_NUMBER_WAGON + " INT PRIMARY KEY, " +
    NAME_ATTRIBUT_ARRIVAL_DATE + " DATE, " +
	NAME_ATTRIBUT_IMAGE_PATH + " VARCHAR(255), " + 
    NAME_ATTRIBUT_LEVEL_CORRECT + " DOUBLE);";
	
	private final DataElementMapperWagons dataMapper;
	private final NamedParameterJdbcTemplate jdbcTemplate;
	
	@Autowired
	public DataElementDaoImplWagons(
			DataElementMapperWagons dMapper,
			NamedParameterJdbcTemplate jTempl
			) {
		this.dataMapper = dMapper;
		this.jdbcTemplate = jTempl;
		
		this.jdbcTemplate.execute(SQL_CREATE_TABLE, new PreparedStatementCallback<Object>() {
			@Override
			public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				ps.execute();
				return null;
			}
		});
	}
	
	@Override
	public Optional<DataElementWagons> getDataElementWagonsByNumber(int numberWagon){
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue(NAME_ATTRIBUT_NUMBER_WAGON, numberWagon);
		try {
			return Optional.ofNullable(
					jdbcTemplate.queryForObject(
							SQL_GET_DATA_BY_NUMBER, 
							params, 
							dataMapper)
					);
		}catch(Exception e) {
			return Optional.empty();
		}
	}

	@Override
	public void insertDataElementWagons(int numberWagon, String arrivalDate, String imagePath, double levelCorrectRecognize) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue(NAME_ATTRIBUT_NUMBER_WAGON, numberWagon);
		params.addValue(NAME_ATTRIBUT_ARRIVAL_DATE, arrivalDate);
		params.addValue(NAME_ATTRIBUT_IMAGE_PATH, imagePath);
		params.addValue(NAME_ATTRIBUT_LEVEL_CORRECT, levelCorrectRecognize);
		jdbcTemplate.update(SQL_INSERT_DATA, params);
	}
}
