package web;

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

}
