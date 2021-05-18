package client.network;

import client.data.DataElementNumberWagon;
import com.google.gson.Gson;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

//*******************************************************************
//Класс, осуществляющий взаимодействие с серверной частью приложения
//*******************************************************************

public class DataNetwork {
    //класс для обработки ошибок
    private class ErrorMessage{
        public String message;
    }

    //класс для представления полного пути к файлу в локальном хранилище сервера
    private class DataElementImageFile{
        public String filePath;
    }

    private class DataAnswer{
        public Boolean answer;

        public DataAnswer(Boolean answer) {
            this.answer = answer;
        }
    }

    //определяет, присутствует ли данный номер полувагона в базе данных (в таблице
    //соответствия номеров полувагонов накладным)
    public static boolean isNumberWagon(String address, Integer numberWagon) throws Exception {
        address = address + "?numberWagon=" + String.valueOf(numberWagon);
        URL url = null;
        try {
            url = new URL(address);
        } catch (MalformedURLException e) {
            throw new Exception("Не корректный адрес для обращения к серверу!");
        }

        HttpURLConnection http = null;

        try{
            http = (HttpURLConnection) url.openConnection();
        }catch (Exception e){
            throw new Exception("Не удалось подключиться к серверу!");
        }

        http.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        http.disconnect();

        Gson g = new Gson();

        String result = response.toString();

        if((result != null) && (result.length() != 0) && (result.contains("message"))){
            ErrorMessage mes = g.fromJson(result, ErrorMessage.class);
            throw new Exception(mes.message);
        }

        DataAnswer dataAnswer = null;
        try{
            dataAnswer = g.fromJson(result, DataAnswer.class);
        }catch (Exception e){
            throw new Exception("При передачи данных с сервера произошла ошибка! Данные не могут быть получены!");
        }

        return dataAnswer.answer;
    }

    //обобщённая функция добавления/обновления/удаления данных в базе данных
    public static <T> void updateDataElement(String address, T dataElement) throws Exception{
        URL url = null;
        try {
            url = new URL(address);
        } catch (MalformedURLException e) {
            throw new Exception("Не корректный адрес для обращения к серверу!");
        }

        HttpURLConnection http = null;
        try {
            http = (HttpURLConnection)url.openConnection();
        } catch (IOException e) {
            throw new Exception("Не удалось подключиться к серверу!");
        }

        //настройка запроса
        http.setRequestMethod("POST");
        http.setDoOutput(true);
        http.setRequestProperty("Content-Type", "application/json");

        Gson g = new Gson();

        String data = g.toJson(dataElement);    //преобразование данных в JSON формат (строковое представление)

        byte[] out = data.getBytes(StandardCharsets.UTF_8);

        OutputStream stream = http.getOutputStream();
        stream.write(out);

        String result = null;

        BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        result = response.toString();

        http.disconnect();

        //обработка ошибки, которая была получена с сервера в виде ответа
        if((result != null) && (result.length() != 0) && (result.contains("message"))){
            ErrorMessage mes = g.fromJson(result, ErrorMessage.class);
            throw new Exception(mes.message);
        }
    }

    //чтение всех не прибывших номеров полувагонов из таблицы Register
    public static DataElementNumberWagon[] getListDataElementNumberWagon(String address) throws Exception {
        URL url = null;
        try {
            url = new URL(address);
        } catch (MalformedURLException e) {
            throw new Exception("Не корректный адрес для обращения к серверу!");
        }

        HttpURLConnection http = null;

        try{
            http = (HttpURLConnection) url.openConnection();
        }catch (Exception e){
            throw new Exception("Не удалось подключиться к серверу!");
        }

        http.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        http.disconnect();

        Gson g = new Gson();

        //конвертация JSON-строки в массив объектов типа DataElementNumberWagon
        DataElementNumberWagon[] dataElementWagonNumbers = null;

        try{
            dataElementWagonNumbers = g.fromJson(response.toString(), DataElementNumberWagon[].class);
        }catch (Exception e){
            throw new Exception("При передачи данных с сервера произошла ошибка! Данные не могут быть получены!");
        }

        return dataElementWagonNumbers;
    }

    //получение информации о местонахождении файла в локальном хранилище сервера
    public static String getFilePath(String address, String fileName) throws Exception {
        address = address + "?fileName=" + fileName;
        URL url = null;
        try {
            url = new URL(address);
        } catch (MalformedURLException e) {
            throw new Exception("Не корректный адрес для обращения к серверу!");
        }

        HttpURLConnection http = null;

        try{
            http = (HttpURLConnection) url.openConnection();
        }catch (Exception e){
            throw new Exception("Не удалось подключиться к серверу!");
        }

        http.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        http.disconnect();

        Gson g = new Gson();

        String result = response.toString();

        if((result != null) && (result.length() != 0) && (result.contains("message"))){
            ErrorMessage mes = g.fromJson(result, ErrorMessage.class);
            throw new Exception(mes.message);
        }

        DataElementImageFile dataPath = null;
        try{
            dataPath = g.fromJson(result, DataElementImageFile.class);
        }catch (Exception e){
            throw new Exception("При передачи данных с сервера произошла ошибка! Данные не могут быть получены!");
        }

        return dataPath.filePath;
    }

    //загрузка файла в локальное хранилище на сервере
    public static String uploadImage(String address, String filePath) throws Exception {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        File file = null;
        try{
            file = new File(filePath);
            if(!file.exists())
                throw new Exception();
        }catch (Exception e){
            throw new Exception("Файл с данным путём не найден! Загрузка файла в локальное" +
                    " хранилище сервера невозможна!");
        }
        ContentType fileContentType = ContentType.create("image/jpg");

        String fileName = file.getName();
        String[] fileData = filePath.split("\\\\");
        builder.addTextBody("name", fileData[(fileData.length - 1)].split("\\.")[0] + ".jpg");
        builder.addBinaryBody("file", file, fileContentType, fileName);
        HttpEntity entity = builder.build();

        HttpPost request = new HttpPost(address);
        request.setEntity(entity);

        HttpClient client = HttpClients.createDefault();
        try{
            client.execute(request);
        }catch (Exception e){
            throw new Exception("Данный файл повреждён! Невозможно загрузить файл в локальное хранилище сервера!");
        }

        return getFilePath("http://localhost:8080/load/filepath", fileData[(fileData.length - 1)].split("\\.")[0] + ".jpg");
    }
}
