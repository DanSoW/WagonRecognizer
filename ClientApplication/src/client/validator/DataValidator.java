package client.validator;

import javafx.scene.control.TextField;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class DataValidator {
    public static boolean requiredValidator(String[] listText){
        if((listText == null) || (listText.length == 0))
            return false;

        for(String i : listText){
            if((i == null) || (i.length() == 0))
                return false;
        }

        return true;
    }

    public static boolean isAllNumber(String text){
        if((text == null) || (text.length() == 0))
            return false;

        for(int i = 0; i < text.length(); i++){
            if(!Character.isDigit(text.charAt(i)))
                return false;
        }

        return true;
    }

    public static boolean isFloatNumber(String text){
        if((text == null) || (text.length() == 0))
            return false;

        short countPoint = 0;
        for(int i = 0; i < text.length(); i++){
            if((!Character.isDigit(text.charAt(i))) && (text.charAt(i) != '.')){
                return false;
            }

            if(text.charAt(i) == '.')
                countPoint++;
        }

        if(countPoint > 1)
            return false;

        return true;
    }

    public static boolean dateTimeValidator(String dateDeparture, String dateArrival){
        if((!dateTextValidator(dateDeparture)) || (!dateTextValidator(dateArrival)))
            return false;

        DateFormat format = new SimpleDateFormat("yyyy-mm-dd");
        try {
            if(format.parse(dateArrival).before(format.parse(dateDeparture)))
                return false;
        } catch (ParseException e) {
            return false;
        }

        return true;
    }

    public static boolean dateTextValidator(String date){
        if((date == null) || (date.length() == 0))
            return false;
        int count = 0;
        for(int i = 0; i < date.length(); i++){
            if(date.charAt(i) == '-'){
                count++;
            }else if(!Character.isDigit(date.charAt(i))){
                return false;
            }
        }
        if(count != 2)
            return false;

        String[] dateSplit = date.split("\\-");
        if((dateSplit.length != 3) || (dateSplit[0].length() != 4)
                || (dateSplit[1].length() != 2) || (dateSplit[2].length() != 2))
            return false;

        int value = Integer.valueOf(dateSplit[2]);
        if((value <= 0) || (value > 31))
            return false;

        value = Integer.valueOf(dateSplit[1]);
        if((value <= 0) || (value > 12))
            return false;

        value = Integer.valueOf(dateSplit[0]);
        if((value <= 0) || (value > 9999))
            return false;

        return true;
    }
}
