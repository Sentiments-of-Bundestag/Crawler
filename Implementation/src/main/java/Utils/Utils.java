package Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.DataFormatException;

public class Utils {
    public static Date StringToDate(String s){
        if(s == null || s.isBlank()){
            return null;
        }
        try{
            return new SimpleDateFormat("dd.MM.yyyy").parse(s);
        }
        catch(ParseException e){
            System.out.println(e.toString());
            return null;
        }
    }
}
