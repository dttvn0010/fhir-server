package vn.moh.fhir.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public static String parseDateToString(Date date,String format)    {
        try {
            var sdf= new SimpleDateFormat(format);
                return sdf.format(date);
            } catch (Exception e) {
        }
        return "";
    }
    
    private static Date parseStringToDate(String dateStr,String format){
        try {
            SimpleDateFormat sdf= new SimpleDateFormat(format);
            return sdf.parse(dateStr);
        } catch (Exception e) {
        }
        return null;
    }
    
    private static String normalizeDateString(String dateStr) {
        String delim = dateStr.contains("/")? "/" : "-";
        String[] arr = dateStr.split(delim);
        if(arr.length != 3) return dateStr;
        
        if(arr[0].length() == 4) {
            var builder = new StringBuilder();
            builder.append(arr[0]).append("-");
            
            if(arr[1].length() == 1) builder.append("0");
            builder.append(arr[1]).append("-");
            
            if(arr[2].length() == 1) builder.append("0");
            builder.append(arr[2]);
            
            return builder.toString();
            
        }else if(arr[2].length() == 4) {
            var builder = new StringBuilder();
            builder.append(arr[2]).append("-");
            
            if(arr[1].length() == 1) builder.append("0");
            builder.append(arr[1]).append("-");
            
            if(arr[0].length() == 1) builder.append("0");
            builder.append(arr[0]);
            
            return builder.toString();            
        }
        return dateStr;
    }
    
    private static String normalizeTimeString(String timeStr) {
        String[] arr = timeStr.split(":");
        if(arr.length < 2) return timeStr;
        
        var builder = new StringBuilder();
        
        if(arr[0].length() == 1) builder.append("0");
        builder.append(arr[0]).append(":");
        
        if(arr[1].length() == 1) builder.append("0");
        builder.append(arr[1]).append(":");
        
        if(arr.length == 2) {
            builder.append("00");
        }else {
            if(arr[2].length() == 1) builder.append("0");
            builder.append(arr[2]);
        }

        return builder.toString();
    }
    
    public static Date parseStringToDate(String dateStr) {
        
        if(dateStr != null) {
            if(!dateStr.contains(" ")) {
                return parseStringToDate(normalizeDateString(dateStr), "yyyy-MM-dd");
                
            }else {
                String[] arr = dateStr.split(" ");
                if(!arr[0].contains(":")) {
                    dateStr = normalizeDateString(arr[0]) + " " + normalizeTimeString(arr[1]);
                }else {
                    dateStr = normalizeDateString(arr[1]) + " " + normalizeTimeString(arr[0]);
                }                
                
                return parseStringToDate(dateStr, "yyyy-MM-dd HH:mm:ss");                 
            }
        }
        
        return null;
    }
}
