package client.setting;

//**********************************************************************
//Настройки обновления данных из базы данных и ограничения
//на значимые атрибуты базы данных (номер накладной и номер полувагона)
//**********************************************************************

public class DataSetting {
    public static int timeRead = 10000; //время через которое будет считаны данные с сервера (обновление данных)
    public static short sizeNumberWagon = 8;
    public static short sizeMinNumberInvoice = 2;
    public static final short sizeMaxNumberInvoice = 20;
}
