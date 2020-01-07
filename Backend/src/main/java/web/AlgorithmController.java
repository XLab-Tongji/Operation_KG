package web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.HashMap;

import static neo4j.MongoDriver.getCorrelation;

@RestController
public class AlgorithmController {

    // cyjtest

    @RequestMapping(value = "/api/cyjtest",method = RequestMethod.GET,produces = "application/json")
    public String cyjtest(){
        return "Hello";
    }

    //前端获取算法csv文件
    @RequestMapping(value = "/api/getClusterCSV",method = RequestMethod.POST,produces = "application/json")
    public String getClusterCSV(@RequestParam("kpi") String name){
        String savePath = AlgorithmController.class.getResource("/").getPath().replace("classes","clusterResult");
        File file = new File(savePath + name + ".json");
        StringBuilder re = new StringBuilder();
        try{
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
            while (br.ready()) {
                re.append(br.readLine()); // 一次读入一行数据
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return re.toString();
    }

    @RequestMapping(value = "/api/getCorrelation",method = RequestMethod.GET,produces = "application/json")
    public HashMap<String, Object> getCorrelationFromMongo(){
        JSONArray links = getCorrelation();
        JSONArray nodes = new JSONArray();
        JSONArray events = new JSONArray();
        JSONArray kpis = new JSONArray();

        JSONObject event = new JSONObject();
        event.put("id", "DailyTesing_HW");
        event.put("name", "DailyTesing_HW");
        event.put("type", "event");
        nodes.add(event);
        events.add("DailyTesing_HW");
        for (Object i:links
             ) {
            kpis.add(((JSONObject)i).getString("sid"));
            JSONObject kpi = new JSONObject();
            kpi.put("id", ((JSONObject)i).getString("sid"));
            kpi.put("name", ((JSONObject)i).getString("sid"));
            kpi.put("type", "kpi");
            nodes.add(kpi);
        }

        HashMap<String, Object> re = new HashMap<>();
        re.put("links", links);
        re.put("nodes", nodes);
        re.put("events", events);
        re.put("kpis", kpis);
        return re;
    }

    /*
      "correlation_list": {
    "A": 0.8366666666666667,
    "B": 0.6383333333333333,
    "C": 0.6581666666666666,
    "D": 0.6275000000000001,
    "E": 0.6090072463768116,
    "F": 0.6141666666666666
  }
}
    */
    //事件与特定kpi的关联性：kpi  {"result":[ {"event":" ","relate":" "} ,,,,,] }
    @RequestMapping(value = "/api/getKpiCorrelation",method = RequestMethod.POST,produces = "application/json")
    public HashMap getKpiCorrelation(@RequestParam("kpi") String name){
//        JSONArray results = new JSONArray();
//        HashMap r1 = new HashMap();
//        r1.put("event","A");r1.put("relate",0.8366666666666667);
//        HashMap r2 = new HashMap();
//        r2.put("event","B");r2.put("relate",0.6383333333333333);
//        HashMap r3 = new HashMap();
//        r3.put("event","C");r3.put("relate",0.6581666666666666);
//        HashMap r4 = new HashMap();
//        r4.put("event","D");r4.put("relate",0.6275000000000001);
//        HashMap r5 = new HashMap();
//        r5.put("event","E");r5.put("relate",0.6090072463768116);
//        HashMap r6 = new HashMap();
//        r6.put("event","F");r6.put("relate",0.6141666666666666);
//        results.add(r1);
//        results.add(r2);
//        results.add(r3);
//        results.add(r4);
//        results.add(r5);
//        results.add(r6);
        HashMap re = new HashMap();
        String savePath = AlgorithmController.class.getResource("/").getPath().replace("classes","clusterResult");
//        String savePath = "/Users/jiang/Desktop/";
        File file = new File(savePath + "correlation.json");
        StringBuilder jsonString = new StringBuilder();
        try{
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
            while (br.ready()) {
                jsonString.append(br.readLine()); // 一次读入一行数据
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        JSONObject jsonObject = JSON.parseObject(jsonString.toString());
        re.put("result", jsonObject.getJSONArray(name));
        return re;
    }



    @RequestMapping(value = "/api/distance",method = RequestMethod.POST,produces = "application/json")
    public HashMap getKpiDistance(@RequestParam("kpi") String name){

        HashMap re = new HashMap();
        re.put("distance", 33829.223299231686);
        return re;
    }



    }
