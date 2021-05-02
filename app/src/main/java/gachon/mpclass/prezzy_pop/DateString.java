package gachon.mpclass.prezzy_pop;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateString {
    static String format = "yyyy-MM-dd/hh:mm:ss";

    public static String DateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String strDate = sdf.format(date);
        return strDate;
    }

    public static  Date StringToDate(String strDate) throws ParseException {
        SimpleDateFormat transFormat = new SimpleDateFormat(format);
        Date date = transFormat.parse(strDate);
        return date;
    }
}


