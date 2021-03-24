package com.server.program;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository	//определение класса, экземпляр которого напрямую обращается к базе данных
public class DataElementDaoImpl implements DataElementDao {

	//константы, характеризующие особенности взаимодействия с базой данных
	private static final String NAME_WAGONS_TABLE = "Wagon";
	private static final String SQL_GET_DATA_BY_ID = 
			"SELECT * FROM " + NAME_WAGONS_TABLE + " WHERE idWagon = :idWagon";
	private static final String SQL_INSERT_DATA =
			"INSERT INTO " + NAME_WAGONS_TABLE + " (invoices_ID, cargo, numberWagon, pathImage, numberOrder, dateArrival) "
					+ "VALUES (:invoices_ID, :cargo, :numberWagon, :pathImage, :numberOrder, :dateArrival)";
	
	private final DataElementMapper dataMapper;
	private final NamedParameterJdbcTemplate jdbcTemplate;
	
	@Autowired
	public DataElementDaoImpl(
			DataElementMapper dMapper,
			NamedParameterJdbcTemplate jTempl
			) {
		this.dataMapper = dMapper;
		this.jdbcTemplate = jTempl;
	}
	
	@Override
	public Optional<DataElement> getDataElementById(int idWagon){
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("idWagon", idWagon);
		try {
			return Optional.ofNullable(
					jdbcTemplate.queryForObject(
							SQL_GET_DATA_BY_ID, 
							params, 
							dataMapper)
					); //возвращает объект, содержащий информацию о запрашиваемом вагоне
		}catch(Exception e) {
			return Optional.empty();
		}
	}

	@Override
	public void insertDataElement(int invoicesId, String cargo,
			String numberWagon, String pathImage, int numberOrder, String dateArrival) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		//добавление значений для ввода данных в таблицу базы данных
		params.addValue("invoices_ID", invoicesId);
		params.addValue("cargo", cargo);
		params.addValue("numberWagon", numberWagon);
		params.addValue("pathImage", pathImage);
		params.addValue("numberOrder", numberOrder);
		params.addValue("dateArrival", dateArrival);
		jdbcTemplate.update(SQL_INSERT_DATA, params); //выполнение запроса
	}
}
