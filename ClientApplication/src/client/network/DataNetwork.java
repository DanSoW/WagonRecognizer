package client.network;

import client.data.DataElementInvoice;
import client.data.DataElementRegister;
import client.data.DataElementWagon;
import com.google.gson.Gson;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DataNetwork {
    private static final String IMAGE_DIRECTORY = "Images";

    private class ErrorMessage{
        public String message;
    }

    //функция добавления/обновления/удаления данных в базе данных
    public static <T> void updateDataElement(String address, T dataInvoice) throws Exception{
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

        http.setRequestMethod("POST");
        http.setDoOutput(true);
        http.setRequestProperty("Content-Type", "application/json");

        Gson g = new Gson();

        String data = g.toJson(dataInvoice);

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

        if((result != null) && (result.length() != 0) && (result.contains("message"))){
            ErrorMessage mes = g.fromJson(result, ErrorMessage.class);
            throw new Exception(mes.message);
        }
    }

    //чтение всех данных из базы данных, для таблицы Invoices
    public static DataElementInvoice[] getListDataInvoices(String address) throws Exception {
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

        DataElementInvoice[] dataElementInvoices = g.fromJson(response.toString(), DataElementInvoice[].class);

        return dataElementInvoices;
    }

    //чтение всех данных из базы данных, для таблицы Register
    public static DataElementRegister[] getListDataRegister(String address) throws Exception {
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

        DataElementRegister[] dataElementRegisters = g.fromJson(response.toString(), DataElementRegister[].class);

        return dataElementRegisters;
    }

    //чтение всех данных из базы данных, для таблицы Wagons
    public static DataElementWagon[] getListDataWagons(String address) throws Exception {
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

        DataElementWagon[] dataElementWagons = g.fromJson(response.toString(), DataElementWagon[].class);

        return dataElementWagons;
    }

    //загрузка изображения с сервера
    public static String loadImage(String address) throws Exception {
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

        File f = new File(IMAGE_DIRECTORY);
        if(!f.exists()) {
            f.mkdir();
        }

        byte[] bytes = http.getInputStream().readAllBytes();
        String[] data = address.split("\\/");
        File file = new File(IMAGE_DIRECTORY + "\\" + data[(data.length - 1)]);
        BufferedOutputStream stream =
                new BufferedOutputStream(new FileOutputStream(file));
        stream.write(bytes);
        stream.close();

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

        if((result != null) && (result.length() != 0) && (result.contains("message"))){
            Gson g = new Gson();
            ErrorMessage mes = g.fromJson(result, ErrorMessage.class);
            throw new Exception(mes.message);
        }

        return file.getAbsolutePath();
    }
}
