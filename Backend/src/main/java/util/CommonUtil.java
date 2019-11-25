package util;

import java.util.HashMap;
import java.util.Map;

public class CommonUtil {
    public static String getlabel(String name) {
        if(Character.isLetter(name.charAt(0))||Character.isDigit(name.charAt(0))){
            return name.substring(0,1);
        }else {
            return "Spe";
        }

    }

    public static String getAlertNum(String key){
        Map<String,String> alert = new HashMap<>();
        alert.put("CPU_usage", "0.2");
        alert.put("Network_Output_Packets", "1000");
        alert.put("Network_Input_Packets", "50");
        alert.put("Network_Output_Bytes", "30000");
        alert.put("Network_Input_Bytes", "8000");
        alert.put("MEM_Usage", "100000000");
        alert.put("Latency", "0.2");
        return alert.get(key);
    }

}
