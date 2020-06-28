package web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.*;
import global.InitData;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static global.globalvalue.getFilePath;
import static neo4j.MongoDriver.*;
import static util.HttpPostUtil.postRootData;
import static util.Vectorize5GUtil.*;

@CrossOrigin
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

    //transction相关
    @RequestMapping(value = "/api/getTransctionData",method = RequestMethod.GET,produces = "application/json")
    public JSONObject getTransctionDataFirstTime(@RequestParam("stateId") String id){
        return getGenerateWorkflowInfoByStateId(id);
//        File jsonFilePath = new File(getFilePath("originalWorkflowInfo")+"6675.json");
//        String jsonInput = null;
//        try {
//            //读取json文件
//            jsonInput = FileUtils.readFileToString(jsonFilePath, "UTF-8");
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return jsonInput;
    }

    @RequestMapping(value = "/api/getSystemStates",method = RequestMethod.POST,produces = "application/json")
    public List getSystemStatesByTime(@RequestParam("start") String startTime, @RequestParam("end") String endTime){
        //start 2020-04-07 end 2020-05-07
        return getAllStateId(startTime, endTime);
    }

    //只要名字，不要后缀
    @RequestMapping(value = "/api/addNewState",method = RequestMethod.GET,produces = "application/json")
    public boolean addNewState(@RequestParam("txtName") String txt, @RequestParam("jsonName") String json){
        Date day=new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = df.format(day);
        File oldName = new File(getFilePath("new2vectorize")+txt+".txt");
        File newName = new File(getFilePath("new2vectorize")+time+".txt");
        File oldName2 = new File(getFilePath("originalWorkflowInfo")+json+".json");
        File newName2 = new File(getFilePath("originalWorkflowInfo")+time+".json");
        if(oldName.renameTo(newName) && oldName2.renameTo(newName2)) {
            System.out.println("已重命名");
        } else {
            System.out.println("Error");
            System.out.println(newName.getAbsolutePath());
            System.out.println(newName2.getAbsolutePath());
            return false;
        }
        return saveGenerateWorkflowInfo2Mongo(JSONObject.toJSONString(readInforJson(newName.getAbsolutePath(), newName2.getAbsolutePath(), time))
                , time);
    }

    @RequestMapping(value = "/api/compareState",method = RequestMethod.POST,produces = "application/json")
    public Map compareState(@RequestParam("state1") String state1, @RequestParam("state2") String state2){
        Map re = new HashMap();
        Map s1 = new HashMap();
        Map s2 = new HashMap();
        s1.put("compareInfo", readFile(state1));
        s1.put("patternInfo", "");
        s2.put("compareInfo", readFile(state2));
        s2.put("patternInfo", "");
        re.put(state1, s1);
        re.put(state2, s2);
        return re;
    }

    @RequestMapping(value = "/api/changeState",method = RequestMethod.POST,produces = "application/json")
    public boolean changeState(@RequestParam("stateId") String stateId, @RequestParam("state") String state) {
        return modifyState(stateId, state);
    }

    @RequestMapping(value = "/api/modifyPattern",method = RequestMethod.POST,produces = "application/json")
    public boolean modifyPattern(@RequestParam("stateId") String stateId, @RequestParam("workflowId") int workflowId,
                                 @RequestParam("patternId") int patternId, @RequestParam("data") String newData) {
        return changeGenerateWorkflowInfo(stateId, workflowId, patternId, newData);
    }


    @RequestMapping(value = "/api/changeEntityName",method = RequestMethod.POST,produces = "application/json")
    public boolean changeEntity(@RequestParam("stateId") String stateId, @RequestParam("workflowId") int workflowId,
                                @RequestParam("patternId") int patternId, @RequestParam("oldName") String oldName,
                                @RequestParam("newName") String newName) {
        return changeEntityName(stateId, workflowId, patternId, oldName, newName);
    }

    @RequestMapping(value = "/api/changeAttributeName",method = RequestMethod.POST,produces = "application/json")
    public boolean changeAttribute(@RequestParam("stateId") String stateId, @RequestParam("workflowId") int workflowId,
                                @RequestParam("patternId") int patternId, @RequestParam("oldName") String oldName,
                                @RequestParam("newName") String newName, @RequestParam("entityName") String entityName) {
        return changeAttributeName(stateId, workflowId, patternId, entityName, oldName, newName);
    }

    @RequestMapping(value = "/api/addEntityName",method = RequestMethod.POST,produces = "application/json")
    public boolean addEntity(@RequestParam("stateId") String stateId, @RequestParam("workflowId") int workflowId,
                                @RequestParam("patternId") int patternId, @RequestParam("entityName") String name,
                                @RequestParam("attribute") JSONArray attribute) {
        System.out.println(attribute);
        return addEntityName(stateId, workflowId, patternId, name, attribute);
    }

    @RequestMapping(value = "/api/addAttributeName",method = RequestMethod.POST,produces = "application/json")
    public boolean addAttribute(@RequestParam("stateId") String stateId, @RequestParam("workflowId") int workflowId,
                                @RequestParam("patternId") int patternId, @RequestParam("entityName") String entityName,
                                @RequestParam("attribute") String attribute) {
        return addAttributeName(stateId, workflowId, patternId, entityName, attribute);
    }

    @RequestMapping(value = "/api/removeEntityName",method = RequestMethod.POST,produces = "application/json")
    public boolean removeEntity(@RequestParam("stateId") String stateId, @RequestParam("workflowId") int workflowId,
                                @RequestParam("patternId") int patternId, @RequestParam("name") String name) {
        return removeEntityName(stateId, workflowId, patternId, name);
    }

    @RequestMapping(value = "/api/removeAttributeName",method = RequestMethod.POST,produces = "application/json")
    public boolean removeAttribute(@RequestParam("stateId") String stateId, @RequestParam("workflowId") int workflowId,
                             @RequestParam("patternId") int patternId, @RequestParam("entityName") String entityName,
                             @RequestParam("attribute") String attribute) {
        return removeAttributeName(stateId, workflowId, patternId, entityName, attribute);
    }

    @RequestMapping(value = "/api/getPatternLog",method = RequestMethod.GET,produces = "application/json")
    public HashMap getKpiName(@RequestParam("pattern") String patternId){
        HashMap map = new HashMap();
        map.put("data", InitData.getLog().getString(patternId.split("_")[0]));
        return map;
    }

    // 算法 root_cause
    @RequestMapping(value = "/api/getKpiName",method = RequestMethod.GET,produces = "application/json")
    public JSONArray getKpiName(){
        return InitData.getKpiName();
    }

    @RequestMapping(value = "/api/getKpiRootCause",method = RequestMethod.GET,produces = "application/json")
    public boolean getRootCause(@RequestParam("kpi") String kpi){
        JSONObject jb = new JSONObject();
        jb.put("anomaly", kpi);
        jb.put("kpi_series", InitData.getKpiData());
        return postRootData(jb.toString());
    }



    public static void main(String[] args) {
//        new AlgorithmController().addNewState("new2vectorize(2)", "");
        new AlgorithmController().getRootCause("service/carts/qps(2xx)");
    }
}
