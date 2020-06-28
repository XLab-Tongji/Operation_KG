package global;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.File;

public class InitData {

    public static JSONArray getKpiName() {
//        String[] kpis = kpiName.split(",");
//        JSONArray jsonArray = new JSONArray();
//        for (String i : kpiName
//        ) {
//            JSONObject jb = new JSONObject();
//            jb.put("name", i);
//            jb.put("value", i);
//            jsonArray.add(jb);
//        }
//        return jsonArray;
        return file2Array(globalvalue.getFilePath("kpi")+"kpi.json");
    }

    public static JSONArray getKpiData(){
        return file2Array(globalvalue.getFilePath("kpi")+"all_data.json");
    }

    public static JSONObject getLog(){
        File jsonFilePath = new File(globalvalue.getFilePath("kpi")+"log.json");
        String jsonInput = null;
        try {
            //读取json文件
            jsonInput = FileUtils.readFileToString(jsonFilePath, "UTF-8");
        }catch (Exception e){
            e.printStackTrace();
        }
        return JSONObject.parseObject(jsonInput);
    }

    public static void main(String[] args) {
        System.out.println(getLog());
    }

    private static JSONArray file2Array(String filePath){
        File jsonFilePath = new File(filePath);
        String jsonInput = null;
        try {
            //读取json文件
            jsonInput = FileUtils.readFileToString(jsonFilePath, "UTF-8");
        }catch (Exception e){
            e.printStackTrace();
        }
        return JSONArray.parseArray(jsonInput);
    }


}