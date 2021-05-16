package client.tests;

import client.data.*;
import client.network.DataNetwork;
import javafx.scene.control.Alert;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//*************************************************************
//Тестирование функционала класса DataNetwork,
//обеспечивающего взаимодействие с серверной частью приложения
//*************************************************************

class DataNetworkTest {

    @Test
    void updateDataElementRegisterNonException(){
        //Обновление записи соответствия полувагона накладной без выхода исключений
        DataElementInvoice dataInvoice = new DataElementInvoice(
                "invoice1",
                "supplier1",
                (short)5,
                "2001-01-01",
                "1999-12-10");
        assertDoesNotThrow(() -> {
            DataNetwork.updateDataElement("http://localhost:8080/database/invoices/insert", dataInvoice);
        });

        DataElementRegisterInsert[] register = new DataElementRegisterInsert[]{
                new DataElementRegisterInsert("invoice1", 12345678, (short)1, (float)0.0),
                new DataElementRegisterInsert("invoice1", 87654321, (short)2, (float)2.0),
                new DataElementRegisterInsert("invoice1", 12345679, (short)3, (float)4.3),
                new DataElementRegisterInsert("invoice1", 12348765, (short)4, (float)0.09)
        };

        DataElementRegisterInsert[] registerUpdate = new DataElementRegisterInsert[]{
                new DataElementRegisterInsert("invoice1", 12345678, (short)1, (float)0.0),
                new DataElementRegisterInsert("invoice1", 87654321, (short)2, (float)2.0),
                new DataElementRegisterInsert("invoice1", 12345679, (short)3, (float)4.3),
                new DataElementRegisterInsert("invoice1", 12348765, (short)4, (float)10.4)
        };


        for(int i = 0; i < register.length; i++){
            int finalI = i;
            assertDoesNotThrow(() -> {
                DataNetwork.updateDataElement("http://localhost:8080/database/register/insert", register[finalI]);
            });
        }

        for(int i = 0; i < registerUpdate.length; i++){
            int finalI = i;
            assertDoesNotThrow(() -> {
                DataNetwork.updateDataElement("http://localhost:8080/database/register/update", registerUpdate[finalI]);
            });
        }

        assertDoesNotThrow(() ->{
            DataNetwork.updateDataElement("http://localhost:8080/database/invoices/delete", dataInvoice);
        });
    }

    @Test
    void updateDataElementRegisterException(){
        //Обновление записи соответствия полувагона накладной с выходом исключений
        DataElementInvoice dataInvoice = new DataElementInvoice(
                "invoice1",
                "supplier1",
                (short)5,
                "2001-01-01",
                "1999-12-10");
        assertDoesNotThrow(() -> {
            DataNetwork.updateDataElement("http://localhost:8080/database/invoices/insert", dataInvoice);
        });

        DataElementRegisterInsert[] register = new DataElementRegisterInsert[]{
                new DataElementRegisterInsert("invoice1", 12345678, (short)1, (float)0.0),
                new DataElementRegisterInsert("invoice1", 87654321, (short)2, (float)2.0),
                new DataElementRegisterInsert("invoice1", 12345679, (short)3, (float)4.3),
                new DataElementRegisterInsert("invoice1", 12348765, (short)4, (float)0.09)
        };

        DataElementRegisterInsert[] registerUpdate = new DataElementRegisterInsert[]{
                new DataElementRegisterInsert("invoice1", 12345678, (short)9, (float)0.0),
                new DataElementRegisterInsert("invoice1", 87654321, (short)0, (float)2.0),
                new DataElementRegisterInsert("invoice1", 12345677, (short)3, (float)4.3),
                new DataElementRegisterInsert("invoice1", 12348765, (short)4, (float)(-10))
        };


        for(int i = 0; i < register.length; i++){
            int finalI = i;
            assertDoesNotThrow(() -> {
                DataNetwork.updateDataElement("http://localhost:8080/database/register/insert", register[finalI]);
            });
        }

        Throwable[] thrown = new Throwable[registerUpdate.length];

        for(int i = 0; i < registerUpdate.length; i++){
            int finalI = i;
            thrown[i] = assertThrows(Exception.class, () -> {
                DataNetwork.updateDataElement("http://localhost:8080/database/register/update", registerUpdate[finalI]);
            });

            assertNotNull(thrown[i].getMessage());
            System.out.println(thrown[i].getMessage());
        }

        assertDoesNotThrow(() ->{
            DataNetwork.updateDataElement("http://localhost:8080/database/invoices/delete", dataInvoice);
        });
    }

    @Test
    void insertDataElementRegisterNonException(){
        //Добавление соответствия полувагона накладной без выхода исключений
        DataElementInvoice dataInvoice = new DataElementInvoice(
                "invoice1",
                "supplier1",
                (short)5,
                "2001-01-01",
                "1999-12-10");
        assertDoesNotThrow(() -> {
            DataNetwork.updateDataElement("http://localhost:8080/database/invoices/insert", dataInvoice);
        });

        DataElementRegisterInsert[] register = new DataElementRegisterInsert[]{
                new DataElementRegisterInsert("invoice1", 12345678, (short)1, (float)0.0),
                new DataElementRegisterInsert("invoice1", 87654321, (short)2, (float)2.0),
                new DataElementRegisterInsert("invoice1", 12345679, (short)3, (float)4.3),
                new DataElementRegisterInsert("invoice1", 12348765, (short)4, (float)0.09)
        };

        for(int i = 0; i < register.length; i++){
            int finalI = i;
            assertDoesNotThrow(() -> {
                DataNetwork.updateDataElement("http://localhost:8080/database/register/insert", register[finalI]);
            });
        }

        assertDoesNotThrow(() ->{
            DataNetwork.updateDataElement("http://localhost:8080/database/invoices/delete", dataInvoice);
        });
    }

    @Test
    void insertDataElementRegisterException(){
        //Добавление соответствия полувагона накладной с выходом исключений
        DataElementInvoice dataInvoice = new DataElementInvoice(
                "invoice1",
                "supplier1",
                (short)5,
                "2001-01-01",
                "1999-12-10");
        assertDoesNotThrow(() -> {
            DataNetwork.updateDataElement("http://localhost:8080/database/invoices/insert", dataInvoice);
        });

        DataElementRegisterInsert[] register = new DataElementRegisterInsert[]{
                new DataElementRegisterInsert("invoice1", 1, (short)1, (float)0.0),
                new DataElementRegisterInsert("2", 87654321, (short)2, (float)0.0),
                new DataElementRegisterInsert("invoice5", 12345679, (short)0, (float)0.0),
                new DataElementRegisterInsert("invoice1", 12348765, (short)4, (float)(-0.9)),
                new DataElementRegisterInsert("invoice5", 12345679, (short)5, (float)0.0)
        };

        Throwable[] thrown = new Throwable[register.length];

        for(int i = 0; i < thrown.length; i++){
            int finalI = i;
            thrown[i] = assertThrows(Exception.class, () -> {
               DataNetwork.updateDataElement("http://localhost:8080/database/register/insert", register[finalI]);
            });

            assertNotNull(thrown[i].getMessage());
            System.out.println(thrown[i].getMessage());
        }

        assertDoesNotThrow(() ->{
            DataNetwork.updateDataElement("http://localhost:8080/database/invoices/delete", dataInvoice);
        });
    }

    @Test
    void updateDataElementWagonException(){
        //Обновление полувагонов с выходом исключений
        DataElementInvoice dataInvoice = new DataElementInvoice(
                "invoice1",
                "supplier1",
                (short)5,
                "2001-01-01",
                "1999-12-10");
        assertDoesNotThrow(() -> {
            DataNetwork.updateDataElement("http://localhost:8080/database/invoices/insert", dataInvoice);
        });

        DataElementRegisterInsert[] register = new DataElementRegisterInsert[]{
                new DataElementRegisterInsert("invoice1", 12345678, (short)1, (float)0.0),
                new DataElementRegisterInsert("invoice1", 87654321, (short)2, (float)0.0),
                new DataElementRegisterInsert("invoice1", 12345679, (short)3, (float)0.0),
                new DataElementRegisterInsert("invoice1", 12348765, (short)4, (float)0.0)
        };

        for(int i = 0; i < register.length; i++){
            int finalI = i;
            assertDoesNotThrow(() -> {
                DataNetwork.updateDataElement("http://localhost:8080/database/register/insert", register[finalI]);
            });
        }

        DataElementWagon[] dataElement = new DataElementWagon[]{
                new DataElementWagon(
                        12345678,
                        "2006-01-12",
                        "C:\\Projects\\SpringServer\\server\\Images\\test_image.jpg",
                        0.0
                ),
                new DataElementWagon(
                        87654321,
                        "2011-01-10",
                        "C:\\Projects\\SpringServer\\server\\Images\\test_image.jpg",
                        0.0
                ),
                new DataElementWagon(
                        12345679,
                        "2019-12-12",
                        "C:\\Projects\\SpringServer\\server\\Images\\test_image.jpg",
                        0.0
                ),
                new DataElementWagon(
                        12348765,
                        "2015-01-04",
                        "C:\\Projects\\SpringServer\\server\\Images\\test_image.jpg",
                        0.0
                )
        };

        DataElementWagon[] dataUpdate = new DataElementWagon[]{
                new DataElementWagon(
                        12345678,
                        "1950-09-12",
                        "C:\\Projects\\SpringServer\\server\\Images\\test_image.jpg",
                        0.0
                ),
                new DataElementWagon(
                        87654321,
                        "2011-01-10",
                        "C:\\Projects\\teskjhgfcx.jpg",
                        0.0
                ),
                new DataElementWagon(
                        12345679,
                        "1997-01-01",
                        "C:\\Projects\\SpringServer\\server\\Images\\test_image.jpg",
                        -90.0
                ),
                new DataElementWagon(
                        12348765,
                        "2015-01-04",
                        "awfawfawf",
                        0.0
                )
        };

        for(int i = 0; i < dataElement.length; i++){
            int finalI = i;
            assertDoesNotThrow(() -> {
                DataNetwork.updateDataElement("http://localhost:8080/database/wagons/insert", dataElement[finalI]);
            });
        }

        Throwable[] thrown = new Throwable[dataUpdate.length];
        for(int i = 0; i < thrown.length; i++){
            int finalI = i;
            thrown[i] = assertThrows(Exception.class, () -> {
               DataNetwork.updateDataElement("http://localhost:8080/database/wagons/update", dataUpdate[finalI]);
            });

            assertNotNull(thrown[i].getMessage());
            System.out.println(thrown[i].getMessage());
        }

        for(int i = 0; i < dataElement.length; i++){
            int finalI = i;
            assertDoesNotThrow(() -> {
                DataNetwork.updateDataElement("http://localhost:8080/database/wagons/delete",
                        new DataElementWagonsDelete(dataElement[finalI].numberWagon));
            });
        }

        assertDoesNotThrow(() ->{
            DataNetwork.updateDataElement("http://localhost:8080/database/invoices/delete", dataInvoice);
        });
    }

    @Test
    void insertDataElementWagonNonException(){
        //Добавление полувагонов без выхода исключения
        //Все данные корректны
        DataElementInvoice dataInvoice = new DataElementInvoice(
                "invoice1",
                "supplier1",
                (short)5,
                "2001-01-01",
                "1999-12-10");
        assertDoesNotThrow(() -> {
            DataNetwork.updateDataElement("http://localhost:8080/database/invoices/insert", dataInvoice);
        });

        DataElementRegisterInsert[] register = new DataElementRegisterInsert[]{
                new DataElementRegisterInsert("invoice1", 12345678, (short)1, (float)0.0),
                new DataElementRegisterInsert("invoice1", 87654321, (short)2, (float)0.0),
                new DataElementRegisterInsert("invoice1", 12345679, (short)3, (float)0.0),
                new DataElementRegisterInsert("invoice1", 12348765, (short)4, (float)0.0)
        };

        for(int i = 0; i < register.length; i++){
            int finalI = i;
            assertDoesNotThrow(() -> {
                DataNetwork.updateDataElement("http://localhost:8080/database/register/insert", register[finalI]);
            });
        }

        DataElementWagon[] dataElement = new DataElementWagon[]{
                new DataElementWagon(
                        12345678,
                        "2006-01-12",
                        "C:\\Projects\\SpringServer\\server\\Images\\test_image.jpg",
                        0.0
                ),
                new DataElementWagon(
                        87654321,
                        "2011-01-10",
                        "C:\\Projects\\SpringServer\\server\\Images\\test_image.jpg",
                        0.0
                ),
                new DataElementWagon(
                        12345679,
                        "2019-12-12",
                        "C:\\Projects\\SpringServer\\server\\Images\\test_image.jpg",
                        0.0
                ),
                new DataElementWagon(
                        12348765,
                        "2015-01-04",
                        "C:\\Projects\\SpringServer\\server\\Images\\test_image.jpg",
                        0.0
                )
        };

        for(int i = 0; i < dataElement.length; i++){
            int finalI = i;
            assertDoesNotThrow(() -> {
               DataNetwork.updateDataElement("http://localhost:8080/database/wagons/insert", dataElement[finalI]);
            });
        }

        for(int i = 0; i < dataElement.length; i++){
            int finalI = i;
            assertDoesNotThrow(() -> {
                DataNetwork.updateDataElement("http://localhost:8080/database/wagons/delete",
                        new DataElementWagonsDelete(dataElement[finalI].numberWagon));
            });
        }

        assertDoesNotThrow(() ->{
            DataNetwork.updateDataElement("http://localhost:8080/database/invoices/delete", dataInvoice);
        });
    }

    @Test
    void insertDataElementWagonException(){
        //Добавление полувагонов с выходом исключения (ошибочные входные данные)
        DataElementInvoice dataInvoice = new DataElementInvoice(
                "invoice1",
                "supplier1",
                (short)5,
                "2001-01-01",
                "1999-12-10");
        assertDoesNotThrow(() -> {
            DataNetwork.updateDataElement("http://localhost:8080/database/invoices/insert", dataInvoice);
        });

        DataElementRegisterInsert[] register = new DataElementRegisterInsert[]{
                new DataElementRegisterInsert("invoice1", 12345678, (short)1, (float)0.0),
                new DataElementRegisterInsert("invoice1", 87654321, (short)2, (float)0.0),
                new DataElementRegisterInsert("invoice1", 12345679, (short)3, (float)0.0),
                new DataElementRegisterInsert("invoice1", 12348765, (short)4, (float)0.0)
        };

        for(int i = 0; i < register.length; i++){
            int finalI = i;
            assertDoesNotThrow(() -> {
                DataNetwork.updateDataElement("http://localhost:8080/database/register/insert", register[finalI]);
            });
        }

        DataElementWagon[] dataElement = new DataElementWagon[]{
                new DataElementWagon(
                        23234455,
                        "2001-01-12",
                        "C:\\Projects\\SpringServer\\server\\Images\\test_image.jpg",
                        0.0
                ),
                new DataElementWagon(
                        1267890,
                        "2001-01-10",
                        "C:\\Projects\\SpringServer\\server\\Images\\test_image.jpg",
                        0.0
                ),
                new DataElementWagon(
                        87654321,
                        "1900-12-12",
                        "C:\\Projects\\SpringServer\\server\\Images\\test_image.jpg",
                        0.0
                ),
                new DataElementWagon(
                        567898766,
                        "2001-01-04",
                        "C:\\Projects\\SpringServer\\server\\Images\\test_image.jpg",
                        0.0
                ),
                new DataElementWagon(
                        12345679,
                        "2awgahahawhawh",
                        "C:\\Projects\\SpringServer\\server\\Images\\test_image.jpg",
                        0.0
                ),
                new DataElementWagon(
                        12348765,
                        "2001-01-04",
                        "NONE",
                        0.0
                )
        };

        Throwable[] thrown = new Throwable[dataElement.length];

        for(int i = 0; i < dataElement.length; i++){
            int finalI = i;
            thrown[i] = assertThrows(Exception.class, () -> {
                DataNetwork.updateDataElement("http://localhost:8080/database/wagons/insert", dataElement[finalI]);
            });
        }

        for(int i = 0; i < thrown.length; i++){
            assertNotNull(thrown[i].getMessage());
            System.out.println(thrown[i].getMessage());
        }

        assertDoesNotThrow(() ->{
           DataNetwork.updateDataElement("http://localhost:8080/database/invoices/delete", dataInvoice);
        });
    }

    @Test
    void updateDataElementInvoiceNonException(){
        //Обновление информации о накладной без выхода исключений.
        DataElementInvoice[] dataElement = new DataElementInvoice[]{
                new DataElementInvoice("invoice1",
                        "supplier1",
                        (short) 25,
                        "2016-01-01",
                        "2015-12-05"),
                new DataElementInvoice("invoice2",
                        "supplier2",
                        (short) 4,
                        "2020-01-01",
                        "2015-12-05"),
                new DataElementInvoice("invoice3",
                        "supplier3",
                        (short) 3,
                        "2001-01-01",
                        "2000-12-01"),
                new DataElementInvoice("invoice4",
                        "supplier4",
                        (short) 2,
                        "2020-10-15",
                        "2019-10-11"),
                new DataElementInvoice("invoice5",
                        "supplier5",
                        (short) 8,
                        "2015-12-20",
                        "2015-12-05")
        };

        DataElementInvoice[] dataInput = new DataElementInvoice[]{
                new DataElementInvoice("invoice1",
                        "supplier1",
                        (short) 4,
                        "2017-01-01",
                        "2015-12-05"),
                new DataElementInvoice("invoice2",
                        "Имя поставщика",
                        (short) 5,
                        "2020-01-01",
                        "2015-12-05"),
                new DataElementInvoice("invoice3",
                        "ООО Компания",
                        (short) 11,
                        "2001-01-01",
                        "2000-12-01"),
                new DataElementInvoice("invoice4",
                        "Name",
                        (short) 2,
                        "2021-03-11",
                        "2019-10-11"),
                new DataElementInvoice("invoice5",
                        "Name 2",
                        (short) 8,
                        "2015-12-20",
                        "2000-01-01")
        };


        for(int i = 0; i < dataElement.length; i++){
            int finalI = i;
            assertDoesNotThrow( () -> {
                DataNetwork.updateDataElement("http://localhost:8080/database/invoices/insert", dataElement[finalI]);
            });
        }

        for(int i = 0; i < dataInput.length; i++){
            int finalI = i;
            assertDoesNotThrow(() -> {
                DataNetwork.updateDataElement("http://localhost:8080/database/invoices/update", dataInput[finalI]);
            });
        }

        assertDoesNotThrow(() ->{
            DataElementInvoice[] data = DataNetwork.getListDataInvoices("http://localhost:8080/database/invoices/get/all");
            if(data.length != dataInput.length){
                for(int i = 0; i < dataInput.length; i++){
                    int finalI = i;
                    int finalI1 = i;
                    assertDoesNotThrow( () -> {
                        DataNetwork.updateDataElement("http://localhost:8080/database/invoices/delete",
                                new DataElementInvoiceDelete(dataInput[finalI1].numberInvoice));
                    });
                }
                throw new Exception("База данных не подготовлена для тестирования!");
            }

            for(int i = 0; i < dataInput.length; i++){
                int index = (-1);
                for(int j = 0; j < data.length; j++){
                    if((data[j].numberInvoice.equals(dataInput[i].numberInvoice))
                            && (data[j].nameSupplier.equals(dataInput[i].nameSupplier))
                            && (data[j].arrivalTrainDate.equals(dataInput[i].arrivalTrainDate))
                            && (data[j].departureTrainDate.equals(dataInput[i].departureTrainDate))
                            && (data[j].totalWagons == dataInput[i].totalWagons)){
                        index = j;
                        break;
                    }
                }
                if(index < 0){
                    for(int k = 0; k < dataInput.length; k++){
                        int finalI = k;
                        assertDoesNotThrow( () -> {
                            DataNetwork.updateDataElement("http://localhost:8080/database/invoices/delete",
                                    new DataElementInvoiceDelete(dataInput[finalI].numberInvoice));
                        });
                    }
                    throw new Exception("База данных не подготовлена для тестирования!");
                }
            }
        });

        for(int k = 0; k < dataElement.length; k++){
            int finalI = k;
            assertDoesNotThrow( () -> {
                DataNetwork.updateDataElement("http://localhost:8080/database/invoices/delete",
                        new DataElementInvoiceDelete(dataElement[finalI].numberInvoice));
            });
        }
    }

    @Test
    void updateDataElementInvoiceException(){
        //Обновление информации о накладной с выходом исключений.
        DataElementInvoice[] dataElement = new DataElementInvoice[]{
                new DataElementInvoice("invoice1",
                        "supplier1",
                        (short) 25,
                        "2016-01-01",
                        "2015-12-05"),
                new DataElementInvoice("invoice2",
                        "supplier2",
                        (short) 4,
                        "2020-01-01",
                        "2015-12-05"),
                new DataElementInvoice("invoice3",
                        "supplier3",
                        (short) 3,
                        "2001-01-01",
                        "2000-12-01"),
                new DataElementInvoice("invoice4",
                        "supplier4",
                        (short) 2,
                        "2020-10-15",
                        "2019-10-11"),
                new DataElementInvoice("invoice5",
                        "supplier5",
                        (short) 8,
                        "2015-12-20",
                        "2015-12-05")
        };

        DataElementInvoice[] dataInput = new DataElementInvoice[]{
                new DataElementInvoice("invoice1",
                        "supplier1",
                        (short) 4,
                        "2012-01-01",
                        "2015-12-05"),
                new DataElementInvoice("invoice2",
                        "supplier2",
                        (short) 0,
                        "2020-01-01",
                        "2015-12-05"),
                new DataElementInvoice("invoice3",
                        "supplier3",
                        (short) -10,
                        "2001-01-01",
                        "2000-12-01"),
                new DataElementInvoice("invoice4",
                        "supplier4",
                        (short) 2,
                        "awfawf",
                        "2019-10-11"),
                new DataElementInvoice("invoice5",
                        "supplier5",
                        (short) 8,
                        "2015-12-20",
                        "awfawfaw")
        };


        for(int i = 0; i < dataElement.length; i++){
            int finalI = i;
            assertDoesNotThrow( () -> {
                DataNetwork.updateDataElement("http://localhost:8080/database/invoices/insert", dataElement[finalI]);
            });
        }

        Throwable[] thrown = new Throwable[dataInput.length];
        for(int i = 0; i < thrown.length; i++){
            int finalI = i;
            thrown[i] = assertThrows(Exception.class, () -> {
                DataNetwork.updateDataElement("http://localhost:8080/database/invoices/update", dataInput[finalI]);
            });
        }

        for(int i = 0; i < thrown.length; i++){
            assertNotNull(thrown[i].getMessage());
            System.out.println(thrown[i].getMessage());
        }

        for(int k = 0; k < dataElement.length; k++){
            int finalI = k;
            assertDoesNotThrow( () -> {
                DataNetwork.updateDataElement("http://localhost:8080/database/invoices/delete",
                        new DataElementInvoiceDelete(dataElement[finalI].numberInvoice));
            });
        }
    }

    @Test
    void insertDataElementInvoiceNonException(){
        //Добавление информации о накладной без выхода исключений.
        //Все данные корректны.
        DataElementInvoice[] dataElement = new DataElementInvoice[]{
                new DataElementInvoice("invoice1",
                        "supplier1",
                        (short) 25,
                        "2016-01-01",
                        "2015-12-05"),
                new DataElementInvoice("invoice2",
                        "supplier2",
                        (short) 4,
                        "2020-01-01",
                        "2015-12-05"),
                new DataElementInvoice("invoice3",
                        "supplier3",
                        (short) 3,
                        "2001-01-01",
                        "2000-12-01"),
                new DataElementInvoice("invoice4",
                        "supplier4",
                        (short) 2,
                        "2020-10-15",
                        "2019-10-11"),
                new DataElementInvoice("invoice5",
                        "supplier5",
                        (short) 8,
                        "2015-12-20",
                        "2015-12-05")
        };


        for(int i = 0; i < dataElement.length; i++){
            int finalI = i;
            assertDoesNotThrow( () -> {
                DataNetwork.updateDataElement("http://localhost:8080/database/invoices/insert", dataElement[finalI]);
            });
        }

        assertDoesNotThrow(() ->{
            DataElementInvoice[] data = DataNetwork.getListDataInvoices("http://localhost:8080/database/invoices/get/all");
            if(data.length != dataElement.length){
                for(int i = 0; i < dataElement.length; i++){
                    int finalI = i;
                    int finalI1 = i;
                    assertDoesNotThrow( () -> {
                        DataNetwork.updateDataElement("http://localhost:8080/database/invoices/delete",
                                new DataElementInvoiceDelete(dataElement[finalI1].numberInvoice));
                    });
                }
                throw new Exception("База данных не подготовлена для тестирования!");
            }

            for(int i = 0; i < dataElement.length; i++){
                int index = (-1);
                for(int j = 0; j < data.length; j++){
                    if((data[j].numberInvoice.equals(dataElement[i].numberInvoice))
                    && (data[j].nameSupplier.equals(dataElement[i].nameSupplier))
                    && (data[j].arrivalTrainDate.equals(dataElement[i].arrivalTrainDate))
                    && (data[j].departureTrainDate.equals(dataElement[i].departureTrainDate))
                    && (data[j].totalWagons == dataElement[i].totalWagons)){
                        index = j;
                        break;
                    }
                }
                if(index < 0){
                    for(int k = 0; k < dataElement.length; k++){
                        int finalI = k;
                        assertDoesNotThrow( () -> {
                            DataNetwork.updateDataElement("http://localhost:8080/database/invoices/delete",
                                    new DataElementInvoiceDelete(dataElement[finalI].numberInvoice));
                        });
                    }
                    throw new Exception("База данных не подготовлена для тестирования!");
                }
            }
        });

        for(int k = 0; k < dataElement.length; k++){
            int finalI = k;
            assertDoesNotThrow( () -> {
                DataNetwork.updateDataElement("http://localhost:8080/database/invoices/delete",
                        new DataElementInvoiceDelete(dataElement[finalI].numberInvoice));
            });
        }
    }

    @Test
    void insertDataElementInvoiceException() {
        //Добавление информации о накладной с выходом исключений, возникающие на стороне
        //серверной части приложения из-за не корректнности входных данных
        DataElementInvoice[] dataElement = new DataElementInvoice[]{
                new DataElementInvoice("invoice1",
                        "supplier1",
                        (short) 25,
                        "2001-01-01",
                        "2015-12-05"),
                new DataElementInvoice("i",
                        "supplier2",
                        (short) 25,
                        "2020-01-01",
                        "2015-12-05"),
                new DataElementInvoice("invoice2",
                        "supplier1",
                        (short) -21,
                        "2001-01-01",
                        "awfawf"),
                new DataElementInvoice("invoice5",
                        "supplier1",
                        (short) 2,
                        "awfawfag",
                        "2015-12-05"),
                new DataElementInvoice("invoice7",
                        "supplier1",
                        (short) 0,
                        "awfawfag",
                        "2015-12-05"),
                new DataElementInvoice("invoiceinvoiceinvoiceinvoiceinvoice8",
                        "supplier7",
                        (short) 7,
                        "2015-12-06",
                        "2015-12-05")
        };

        Throwable[] thrown = new Throwable[dataElement.length];
        for(int i = 0; i < thrown.length; i++){
            int finalI = i;
            thrown[i] = assertThrows(Exception.class, () -> {
                DataNetwork.updateDataElement("http://localhost:8080/database/invoices/insert", dataElement[finalI]);
            });
        }

        for(int i = 0; i < thrown.length; i++){
            assertNotNull(thrown[i].getMessage());
            System.out.println(thrown[i].getMessage());
        }
    }
}