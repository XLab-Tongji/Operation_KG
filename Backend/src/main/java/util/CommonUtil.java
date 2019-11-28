package util;

import java.util.HashMap;
import java.util.Map;

public class CommonUtil {

    static Map<String,Double> alert = new HashMap<>();

    public static String getlabel(String name) {
        if(Character.isLetter(name.charAt(0))||Character.isDigit(name.charAt(0))){
            return name.substring(0,1);
        }else {
            return "Spe";
        }

    }

    public static Double getAlertNum(String key){
        alert.put("CPU_Usage", 0.2);
        alert.put("Network_Output_Packets", 1000.0);
        alert.put("Network_Input_Packets", 50.0);
        alert.put("Network_Output_Bytes", 30000.0);
        alert.put("Network_Input_Bytes", 8000.0);
        alert.put("MEM_Usage", 100000000.0);
        alert.put("Latency", 0.2);
        return alert.get(key);
    }

    public static boolean hasAlert(String key){
        return alert.containsKey(key);
    }

}
