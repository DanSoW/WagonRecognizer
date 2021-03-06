package com.server.database.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.server.database.elements.DataElementInvoices;
import com.server.database.elements.DataElementRegister;
import com.server.database.elements.DataElementWagons;
import com.server.database.mappers.DataElementMapperInvoices;
import com.server.database.mappers.DataElementMapperRegister;
import com.server.database.mappers.DataElementMapperWagons;

@Repository
public class DataElementDaoImpl implements DataElementDao {
	
	//Имя базы данных
	@SuppressWarnings("unused")
	private static final String NAME_DATABASE = "WagonRecognize";

	//Имена таблиц
	private static final String NAME_WAGONS_TABLE = "Wagons";
	private static final String NAME_INVOICES_TABLE = "Invoices";
	private static final String NAME_REGISTER_TABLE = "Register";
	
	//Атрибуты таблицы Wagons
	public static final String NAME_ATTRIBUT_NUMBER_WAGON = "numberWagon";
	public static final String NAME_ATTRIBUT_ARRIVAL_DATE = "arrivalDate";
	public static final String NAME_ATTRIBUT_IMAGE_PATH = "imagePath";
	public static final String NAME_ATTRIBUT_LEVEL_CORRECT = "levelCorrectRecognize";
	
	//Атрибуты таблицы Invoices
	public static final String NAME_ATTRIBUT_NUMBER_INVOICES = "numberInvoices";
	public static final String NAME_ATTRIBUT_NAME_SUPPLIER = "nameSupplier";
	public static final String NAME_ATTRIBUT_TOTAL_WAGONS = "totalWagons";
	public static final String NAME_ATTRIBUT_ARRIVAL_TRAIN_DATE = "arrivalTrainDate";
	public static final String NAME_ATTRIBUT_DEPARTURE_TRAIN_DATE = "departureTrainDate";
	
	//Атрибуты таблицы Register
	public static final String NAME_ATTRIBUT_ID = "id";
	public static final String NAME_ATTRIBUT_REF_NUMBER_INVOICE = "refNumberInvoice";
	public static final String NAME_ATTRIBUT_FOR_THIS_NUMBER_WAGON = "thisNumberWagon";
	public static final String NAME_ATTRIBUT_ARRIVAL_MARK = "arrivalMark";
	public static final String NAME_ATTRIBUT_SERIAL_NUMBER = "serialNumber";
	public static final String NAME_ATTRIBUT_ACTUAL_SERIAL_NUMBER = "actualSerialNumber";
	public static final String NAME_ATTRIBUT_SD = "sD";
	
	//***************************************************
	//Запросы к базе данных в формате SQL команд:
	
	//SQL-запросы для таблицы Wagons
	private static final String SQL_GET_DATA_BY_NUMBER_WAGONS = 
			"SELECT * FROM " + NAME_WAGONS_TABLE + " WHERE " + NAME_ATTRIBUT_NUMBER_WAGON + " = :" + NAME_ATTRIBUT_NUMBER_WAGON;
	private static final String SQL_INSERT_DATA_WAGONS =
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
	
	private static final String SQL_SELECT_WAGONS = "SELECT * FROM " + NAME_WAGONS_TABLE + ";";
	private static final String SQL_SET_ARRIVAL_MARK_TRUE = 
			"UPDATE " + NAME_REGISTER_TABLE + " SET " + NAME_ATTRIBUT_ARRIVAL_MARK + "=TRUE"
			+ " WHERE " + NAME_ATTRIBUT_FOR_THIS_NUMBER_WAGON + "=:" + NAME_ATTRIBUT_NUMBER_WAGON + ";";
	
	private static final String SQL_SET_ARRIVAL_MARK_FALSE = 
			"UPDATE " + NAME_REGISTER_TABLE + " SET " + NAME_ATTRIBUT_ARRIVAL_MARK + "=FALSE"
			+ " WHERE " + NAME_ATTRIBUT_FOR_THIS_NUMBER_WAGON + "=:" + NAME_ATTRIBUT_NUMBER_WAGON + ";";
	
	private static final String SQL_CREATE_TABLE_WAGONS = "CREATE TABLE IF NOT EXISTS " + NAME_WAGONS_TABLE + " ( " +
	NAME_ATTRIBUT_NUMBER_WAGON + " INT PRIMARY KEY, " +
    NAME_ATTRIBUT_ARRIVAL_DATE + " DATE, " +
	NAME_ATTRIBUT_IMAGE_PATH + " VARCHAR(255), " + 
    NAME_ATTRIBUT_LEVEL_CORRECT + " DOUBLE);";
	
	private static final String SQL_UPDATE_TABLE_WAGONS = "UPDATE " + NAME_WAGONS_TABLE
			+ " SET " + NAME_ATTRIBUT_ARRIVAL_DATE + "=:" + NAME_ATTRIBUT_ARRIVAL_DATE
			+ ", " + NAME_ATTRIBUT_IMAGE_PATH + "=:" + NAME_ATTRIBUT_IMAGE_PATH
			+ ", " + NAME_ATTRIBUT_LEVEL_CORRECT + "=:" + NAME_ATTRIBUT_LEVEL_CORRECT
			+ " WHERE " + NAME_ATTRIBUT_NUMBER_WAGON + "=:" + NAME_ATTRIBUT_NUMBER_WAGON
			+ ";";
	
	private static final String SQL_DELETE_RECORD_WAGONS = "DELETE FROM " + NAME_WAGONS_TABLE + " WHERE "
			+ NAME_ATTRIBUT_NUMBER_WAGON + "=:" + NAME_ATTRIBUT_NUMBER_WAGON + ";";
	
	//SQL-запросы для таблицы Invoices
	private static final String SQL_GET_DATA_BY_NUMBER_INVOICES = 
			"SELECT * FROM " + NAME_INVOICES_TABLE + " WHERE " + NAME_ATTRIBUT_NUMBER_INVOICES + " = :" + NAME_ATTRIBUT_NUMBER_INVOICES;
	private static final String SQL_INSERT_DATA_INVOICES =
			"INSERT INTO " + NAME_INVOICES_TABLE + " (" 
	+ NAME_ATTRIBUT_NUMBER_INVOICES + ", "
	+ NAME_ATTRIBUT_NAME_SUPPLIER + ", "
	+ NAME_ATTRIBUT_TOTAL_WAGONS + ", "
	+ NAME_ATTRIBUT_ARRIVAL_TRAIN_DATE + ", "
	+ NAME_ATTRIBUT_DEPARTURE_TRAIN_DATE + ") "
	+ "VALUES ("
	+ ":" + NAME_ATTRIBUT_NUMBER_INVOICES + ", "
	+ ":" + NAME_ATTRIBUT_NAME_SUPPLIER + ", "
	+ ":" + NAME_ATTRIBUT_TOTAL_WAGONS + ", "
	+ ":" + NAME_ATTRIBUT_ARRIVAL_TRAIN_DATE + ", "
	+ ":" + NAME_ATTRIBUT_DEPARTURE_TRAIN_DATE + ")";
	
	private static final String SQL_CREATE_TABLE_INVOICES = "CREATE TABLE IF NOT EXISTS " + NAME_INVOICES_TABLE + " ( " +
			NAME_ATTRIBUT_NUMBER_INVOICES + " VARCHAR(20) PRIMARY KEY, " +
			NAME_ATTRIBUT_NAME_SUPPLIER + " NVARCHAR(100), " +
			NAME_ATTRIBUT_TOTAL_WAGONS + " SMALLINT, " + 
			NAME_ATTRIBUT_ARRIVAL_TRAIN_DATE + " DATE, " + 
			NAME_ATTRIBUT_DEPARTURE_TRAIN_DATE + " DATE);";
	
	private static final String SQL_SELECT_INVOICES = "SELECT * FROM " + NAME_INVOICES_TABLE + ";";
	
	private static final String SQL_UPDATE_TABLE_INVOICES = "UPDATE " + NAME_INVOICES_TABLE
			+ " SET " + NAME_ATTRIBUT_NAME_SUPPLIER + "=:" + NAME_ATTRIBUT_NAME_SUPPLIER
			+ ", " + NAME_ATTRIBUT_TOTAL_WAGONS + "=:" + NAME_ATTRIBUT_TOTAL_WAGONS
			+ ", " + NAME_ATTRIBUT_ARRIVAL_TRAIN_DATE + "=:" + NAME_ATTRIBUT_ARRIVAL_TRAIN_DATE
			+ ", " + NAME_ATTRIBUT_DEPARTURE_TRAIN_DATE + "=:" + NAME_ATTRIBUT_DEPARTURE_TRAIN_DATE
			+ " WHERE " + NAME_ATTRIBUT_NUMBER_INVOICES + "=:" + NAME_ATTRIBUT_NUMBER_INVOICES
			+ ";";
	
	private static final String SQL_DELETE_RECORD_INVOICES = "DELETE FROM " + NAME_INVOICES_TABLE + " WHERE "
			+ NAME_ATTRIBUT_NUMBER_INVOICES + "=:" + NAME_ATTRIBUT_NUMBER_INVOICES + ";";
	
	//SQL-запросы для таблицы Register
	private static final String SQL_GET_DATA_BY_ID_REGISTER = 
			"SELECT * FROM " + NAME_REGISTER_TABLE + " WHERE " + NAME_ATTRIBUT_REF_NUMBER_INVOICE + " = :" + NAME_ATTRIBUT_REF_NUMBER_INVOICE
			+ " AND " + NAME_ATTRIBUT_FOR_THIS_NUMBER_WAGON + "=:" + NAME_ATTRIBUT_FOR_THIS_NUMBER_WAGON + ";";
	
	private static final String SQL_INSERT_DATA_REGISTER =
			"INSERT INTO " + NAME_REGISTER_TABLE + " (" 
	+ NAME_ATTRIBUT_REF_NUMBER_INVOICE + ", "
	+ NAME_ATTRIBUT_FOR_THIS_NUMBER_WAGON + ", "
	+ NAME_ATTRIBUT_ARRIVAL_MARK + ", "
	+ NAME_ATTRIBUT_SERIAL_NUMBER + ", "
	+ NAME_ATTRIBUT_ACTUAL_SERIAL_NUMBER + ", "
	+ NAME_ATTRIBUT_SD + ") "
	+ "VALUES ("
	+ ":" + NAME_ATTRIBUT_REF_NUMBER_INVOICE + ", "
	+ ":" + NAME_ATTRIBUT_FOR_THIS_NUMBER_WAGON + ", "
	+ "FALSE, "
	+ ":" + NAME_ATTRIBUT_SERIAL_NUMBER 
	+ ", 0, " 
	+ ":" + NAME_ATTRIBUT_SD + ");";
	private static final String SQL_SELECT_REGISTER = "SELECT * FROM " + NAME_REGISTER_TABLE + ";";
	
	private static final String SQL_CREATE_TABLE_REGISTER = "CREATE TABLE IF NOT EXISTS " + NAME_REGISTER_TABLE + " (" +
	NAME_ATTRIBUT_ID + " INT PRIMARY KEY AUTO_INCREMENT, " +
    NAME_ATTRIBUT_REF_NUMBER_INVOICE + " VARCHAR(20) NOT NULL, " +
    NAME_ATTRIBUT_FOR_THIS_NUMBER_WAGON + " INT NOT NULL UNIQUE, " +
	NAME_ATTRIBUT_ARRIVAL_MARK + " BOOLEAN NOT NULL, " + 
    NAME_ATTRIBUT_SERIAL_NUMBER + " SMALLINT NOT NULL, " +
	NAME_ATTRIBUT_ACTUAL_SERIAL_NUMBER + " SMALLINT NULL, " +
    NAME_ATTRIBUT_SD + " FLOAT NOT NULL, " +
	"FOREIGN KEY (" + NAME_ATTRIBUT_REF_NUMBER_INVOICE + ") " +
    "REFERENCES " + NAME_INVOICES_TABLE + " (" + NAME_ATTRIBUT_NUMBER_INVOICES + "));";
	
	private static final String SQL_UPDATE_TABLE_REGISTER = "UPDATE " + NAME_REGISTER_TABLE
			+ " SET " + NAME_ATTRIBUT_SERIAL_NUMBER + "=:" + NAME_ATTRIBUT_SERIAL_NUMBER
			+ ", " + NAME_ATTRIBUT_SD + "=:" + NAME_ATTRIBUT_SD
			+ " WHERE " + NAME_ATTRIBUT_REF_NUMBER_INVOICE + "=:" + NAME_ATTRIBUT_REF_NUMBER_INVOICE
			+ " AND " + NAME_ATTRIBUT_FOR_THIS_NUMBER_WAGON + "=:" + NAME_ATTRIBUT_FOR_THIS_NUMBER_WAGON
			+ ";";
	
	private static final String SQL_UPDATE_ACTUAL_SERIAL_NUMBER = "UPDATE " + NAME_REGISTER_TABLE
			+ " SET " + NAME_ATTRIBUT_ACTUAL_SERIAL_NUMBER + "=:" + NAME_ATTRIBUT_ACTUAL_SERIAL_NUMBER
			+ " WHERE " + NAME_ATTRIBUT_REF_NUMBER_INVOICE + "=:" + NAME_ATTRIBUT_REF_NUMBER_INVOICE
			+ " AND " + NAME_ATTRIBUT_FOR_THIS_NUMBER_WAGON + "=:" + NAME_ATTRIBUT_FOR_THIS_NUMBER_WAGON
			+ ";";
	
	private static final String SQL_DELETE_RECORD_REGISTER = "DELETE FROM " + NAME_REGISTER_TABLE + " WHERE "
			+ NAME_ATTRIBUT_REF_NUMBER_INVOICE + "=:" + NAME_ATTRIBUT_REF_NUMBER_INVOICE + " AND "
			+ NAME_ATTRIBUT_FOR_THIS_NUMBER_WAGON + "=:" + NAME_ATTRIBUT_FOR_THIS_NUMBER_WAGON + ";";
			
	
	private final DataElementMapperWagons dataMapperWagons;
	private final DataElementMapperInvoices dataMapperInvoices;
	private final DataElementMapperRegister dataMapperRegister;
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	
	@Autowired
	public DataElementDaoImpl(
			DataElementMapperWagons dMapperWagons,
			DataElementMapperInvoices dMapperInvoices,
			DataElementMapperRegister dMapperRegister,
			NamedParameterJdbcTemplate jTempl
			) {
		this.dataMapperWagons = dMapperWagons;
		this.dataMapperInvoices = dMapperInvoices;
		this.dataMapperRegister = dMapperRegister;
		this.jdbcTemplate = jTempl;
		
		//Создание таблицы Wagons
		this.jdbcTemplate.execute(SQL_CREATE_TABLE_WAGONS, new PreparedStatementCallback<Object>() {
			@Override
			public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				ps.execute();
				return null;
			}
		});
		
		//Создание таблицы Invoices
		this.jdbcTemplate.execute(SQL_CREATE_TABLE_INVOICES, new PreparedStatementCallback<Object>() {
			@Override
			public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				ps.execute();
				return null;
			}
		});
		
		//Создание таблицы Register
		this.jdbcTemplate.execute(SQL_CREATE_TABLE_REGISTER, new PreparedStatementCallback<Object>() {
			@Override
			public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				ps.execute();
				return null;
			}
		});
	}
	
	//*************************************
	//Логика для взаимодействия с таблицей Wagons
	@Override
	public Optional<DataElementWagons> getDataElementWagonsByNumber(int numberWagon){
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue(NAME_ATTRIBUT_NUMBER_WAGON, numberWagon);
		try {
			return Optional.ofNullable(
					jdbcTemplate.queryForObject(
							SQL_GET_DATA_BY_NUMBER_WAGONS, 
							params, 
							dataMapperWagons)
					);
		}catch(Exception e) {
			return Optional.empty();
		}
	}
	
	@Override
	public List<Optional<DataElementWagons>> getDataElementWagonsAll(){
		List<DataElementWagons> wagons = jdbcTemplate.query(SQL_SELECT_WAGONS, dataMapperWagons);
		
		return wagons.stream()
				.map(Optional::ofNullable)
				.collect(Collectors.toList());
	}

	@Override
	public void insertDataElementWagons(int numberWagon, String arrivalDate, String imagePath, double levelCorrectRecognize) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue(NAME_ATTRIBUT_NUMBER_WAGON, numberWagon);
		params.addValue(NAME_ATTRIBUT_ARRIVAL_DATE, arrivalDate);
		params.addValue(NAME_ATTRIBUT_IMAGE_PATH, imagePath);
		params.addValue(NAME_ATTRIBUT_LEVEL_CORRECT, levelCorrectRecognize);
		
		jdbcTemplate.update(SQL_INSERT_DATA_WAGONS, params);
		
		//Обновление значений о прибытии (с FALSE на TRUE)
		MapSqlParameterSource params1 = new MapSqlParameterSource();
		params1.addValue(NAME_ATTRIBUT_NUMBER_WAGON, numberWagon);
		jdbcTemplate.update(SQL_SET_ARRIVAL_MARK_TRUE, params1);
	}
	
	@Override
	public void updateDataElementWagons(int numberWagon, String arrivalDate, String imagePath,
			double levelCorrectRecognize) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue(NAME_ATTRIBUT_NUMBER_WAGON, numberWagon);
		params.addValue(NAME_ATTRIBUT_ARRIVAL_DATE, arrivalDate);
		params.addValue(NAME_ATTRIBUT_IMAGE_PATH, imagePath);
		params.addValue(NAME_ATTRIBUT_LEVEL_CORRECT, levelCorrectRecognize);
		jdbcTemplate.update(SQL_UPDATE_TABLE_WAGONS, params);
	}
	
	@Override
	public void deleteDataElementWagons(int numberWagon) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue(NAME_ATTRIBUT_NUMBER_WAGON, numberWagon);
		jdbcTemplate.update(SQL_DELETE_RECORD_WAGONS, params);
		
		//Обновление значений о прибытии (с TRUE на FALSE)
		MapSqlParameterSource params1 = new MapSqlParameterSource();
		params1.addValue(NAME_ATTRIBUT_NUMBER_WAGON, numberWagon);
		jdbcTemplate.update(SQL_SET_ARRIVAL_MARK_FALSE, params1);
	}
	
	//*************************************
	//Логика взаимодействия с таблицей Invoices
	@Override
	public Optional<DataElementInvoices> getDataElementInvoicesByNumber(String numberInvoices){
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue(NAME_ATTRIBUT_NUMBER_INVOICES, numberInvoices);
		try {
			return Optional.ofNullable(
					jdbcTemplate.queryForObject(
							SQL_GET_DATA_BY_NUMBER_INVOICES, 
							params, 
							dataMapperInvoices)
					);
		}catch(Exception e) {
			return Optional.empty();
		}
	}
	
	@Override
	public List<Optional<DataElementInvoices>> getDataElementInvoicesAll() {
		List<DataElementInvoices> invoices = jdbcTemplate.query(SQL_SELECT_INVOICES, dataMapperInvoices);
		
		return invoices.stream()
				.map(Optional::ofNullable)
				.collect(Collectors.toList());
	}
	
	@Override
	public void insertDataElementInvoices(String numberInvoice, String nameSupplier, short totalWagons, 
			String arrivalTrainDate, String departureTrainDate) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue(NAME_ATTRIBUT_NUMBER_INVOICES, numberInvoice);
		params.addValue(NAME_ATTRIBUT_NAME_SUPPLIER, nameSupplier);
		params.addValue(NAME_ATTRIBUT_TOTAL_WAGONS, totalWagons);
		params.addValue(NAME_ATTRIBUT_ARRIVAL_TRAIN_DATE, arrivalTrainDate);
		params.addValue(NAME_ATTRIBUT_DEPARTURE_TRAIN_DATE, departureTrainDate);
		jdbcTemplate.update(SQL_INSERT_DATA_INVOICES, params);
	}

	@Override
	public void updateDataElementInvoices(String numberInvoice, String nameSupplier, short totalWagons,
			String arrivalTrainDate, String departureTrainDate) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue(NAME_ATTRIBUT_NUMBER_INVOICES, numberInvoice);
		params.addValue(NAME_ATTRIBUT_NAME_SUPPLIER, nameSupplier);
		params.addValue(NAME_ATTRIBUT_TOTAL_WAGONS, totalWagons);
		params.addValue(NAME_ATTRIBUT_ARRIVAL_TRAIN_DATE, arrivalTrainDate);
		params.addValue(NAME_ATTRIBUT_DEPARTURE_TRAIN_DATE, departureTrainDate);
		jdbcTemplate.update(SQL_UPDATE_TABLE_INVOICES, params);
	}

	@Override
	public void deleteDataElementInvoices(String numberInvoice) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue(NAME_ATTRIBUT_NUMBER_INVOICES, numberInvoice);
		jdbcTemplate.update(SQL_DELETE_RECORD_INVOICES, params);
	}

	//*************************************
	//Логика взаимодействия с таблицей Register
	@Override
	public Optional<DataElementRegister> getDataElementRegisterById(String fkNumberInvoice, int numberWagon) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue(NAME_ATTRIBUT_REF_NUMBER_INVOICE, fkNumberInvoice);
		params.addValue(NAME_ATTRIBUT_FOR_THIS_NUMBER_WAGON, numberWagon);
		try {
			return Optional.ofNullable(
					jdbcTemplate.queryForObject(
							SQL_GET_DATA_BY_ID_REGISTER, 
							params, 
							dataMapperRegister)
					);
		}catch(Exception e) {
			return Optional.empty();
		}
	}

	@Override
	public List<Optional<DataElementRegister>> getDataElementRegisterAll() {
		List<DataElementRegister> registers = jdbcTemplate.query(SQL_SELECT_REGISTER, dataMapperRegister);
		
		return registers.stream()
				.map(Optional::ofNullable)
				.collect(Collectors.toList());
	}

	@Override
	public void insertDataElementRegister(String fkNumberInvoice, int numberWagon, short serialNumber, float sD) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue(NAME_ATTRIBUT_REF_NUMBER_INVOICE, fkNumberInvoice);
		params.addValue(NAME_ATTRIBUT_FOR_THIS_NUMBER_WAGON, numberWagon);
		params.addValue(NAME_ATTRIBUT_SERIAL_NUMBER, serialNumber);
		params.addValue(NAME_ATTRIBUT_SD, sD);
		jdbcTemplate.update(SQL_INSERT_DATA_REGISTER, params);
	}

	@Override
	public void updateDataElementRegister(String fkNumberInvoice, int numberWagon, short serialNumber, float sD) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue(NAME_ATTRIBUT_REF_NUMBER_INVOICE, fkNumberInvoice);
		params.addValue(NAME_ATTRIBUT_FOR_THIS_NUMBER_WAGON, numberWagon);
		params.addValue(NAME_ATTRIBUT_SERIAL_NUMBER, serialNumber);
		params.addValue(NAME_ATTRIBUT_SD, sD);
		jdbcTemplate.update(SQL_UPDATE_TABLE_REGISTER, params);
	}

	@Override
	public void deleteDataElementRegister(String fkNumberInvoice, int numberWagon) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue(NAME_ATTRIBUT_REF_NUMBER_INVOICE, fkNumberInvoice);
		params.addValue(NAME_ATTRIBUT_FOR_THIS_NUMBER_WAGON, numberWagon);
		jdbcTemplate.update(SQL_DELETE_RECORD_REGISTER, params);
	}

	@Override
	public void updateDataElementRegisterActualNumber(String fkNumberInvoice, int numberWagon,
			short actualSerialNumber) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue(NAME_ATTRIBUT_REF_NUMBER_INVOICE, fkNumberInvoice);
		params.addValue(NAME_ATTRIBUT_FOR_THIS_NUMBER_WAGON, numberWagon);
		params.addValue(NAME_ATTRIBUT_ACTUAL_SERIAL_NUMBER, actualSerialNumber);
		jdbcTemplate.update(SQL_UPDATE_ACTUAL_SERIAL_NUMBER, params);
	}
}
