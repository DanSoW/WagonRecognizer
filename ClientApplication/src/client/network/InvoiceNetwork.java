package client.network;

import client.data.DataElementInvoice;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class InvoiceNetwork {
    private class ErrorMessage{
        public String message;
    }

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
}
