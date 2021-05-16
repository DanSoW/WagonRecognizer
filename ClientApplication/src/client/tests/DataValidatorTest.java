package client.tests;

import client.validator.DataValidator;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

//********************************************************
//Тестирование функционала класса DataValidatorTest,
//который используется во всех классах программируемости
//пользовательских окон (интерфейс приложения)
//********************************************************

class DataValidatorTest {

    @Test
    void requiredValidator() {
        //Проверка на работоспособность метода, проверяющего
        //содержимого текстовых полей (их наполненность)
        String[][] inputData = new String[][]{
                {
                    "Hello, world!", "Text", "Text 2", ""
                },
                {
                    "", "", "New word", "Hello", "Stone", "Stage"
                },
                {
                    "Hello", "Текст", "Автомобиль"
                },
                {
                    "1", "2", "", "null"
                },
                {
                    "2.4", "4", "231212", "2344"
                }
        };

        boolean[] results = new boolean[]{
                false,
                false,
                true,
                false,
                true
        };

        for(int i = 0; i < inputData.length; i++){
            Assert.assertEquals(results[i], DataValidator.requiredValidator(inputData[i]));
        }
    }

    @Test
    void isAllNumber() {
        //Проверка на работоспособность метода, проверающего символы строки
        //на принадлежность исключительно всех к цифрам
        String[] inputData = new String[]{
                "23124",
                ",awf90212",
                ".124125",
                "2125aga",
                "125125adga",
                "12125",
                "251261"
        };

        boolean[] results = new boolean[]{
                true,
                false,
                false,
                false,
                false,
                true,
                true
        };

        for(int i = 0; i < inputData.length; i++){
            Assert.assertEquals(results[i], DataValidator.isAllNumber(inputData[i]));
        }
    }

    @Test
    void isFloatNumber() {
        //Проверка на работоспособность метода, проверяющего принадлежность строки
        //к типу данных float
        String[] inputData = new String[]{
                "24.512",
                "234",
                "2412awa,agwg",
                "21,241",
                "9.91",
                "25",
                "wfagwg"
        };

        boolean[] results = new boolean[]{
                true,
                true,
                false,
                false,
                true,
                true,
                false
        };

        for(int i = 0; i < inputData.length; i++){
            Assert.assertEquals(results[i], DataValidator.isFloatNumber(inputData[i]));
        }
    }

    @Test
    void dateTimeValidator() {
        //Проверка на работоспособность метода, сравнивающего даты отъезда и прибытия состава полувагонов
        String[][] inputData = new String[][]{
                { "2001-01-01", "2002-12-14" },
                { "2000-01-02", "1984-12-12" },
                { "asfawg", "2001-15-12" },
                { "2015-04-01", "2020-09-17" },
                { "241125awfa.,", "2rawgawga" },
                { "2018-12-12", "awgawgag"},
                { "2019-12-14", "2019-12-15" }
        };

        boolean[] results = new boolean[]{
                true,
                false,
                false,
                true,
                false,
                false,
                true
        };

        for(int i = 0; i < inputData.length; i++){
            Assert.assertEquals(results[i], DataValidator.dateTimeValidator(inputData[i][0], inputData[i][1]));
        }
    }

    @Test
    void dateTextValidator() {
        //Проверка на работоспособность метода, проверяющего формат даты
        String[] inputData = new String[]{
                "Hello, world!",
                "2001-01-01",
                "18.12.1998",
                "24.55-212512",
                "2021-02-17",
                "2014-03-19"
        };

        boolean[] results = new boolean[]{
                false,
                true,
                false,
                false,
                true,
                true
        };

        for(int i = 0; i < inputData.length; i++){
            Assert.assertEquals(results[i], DataValidator.dateTextValidator(inputData[i]));
        }
    }
}