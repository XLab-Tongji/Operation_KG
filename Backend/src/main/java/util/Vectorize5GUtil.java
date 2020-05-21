package util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static global.globalvalue.getFilePath;

class PatternNode{

    public PatternNode(int _id){
        id = _id;
    }

    public void setId_count(Map<Integer, Integer> map){
        if (map.containsKey(id)){
            map.put(id, map.get(id)+1);
        }else{
            map.put(id, 1);
        }
        id_count = map.get(id);
    }

    public int id;
    public int id_count;
    public String type = "normal";
    public List attr = new ArrayList<>();
    public boolean cycle_child = false;
    public PatternNode nextNode = null;
    public PatternNode concurrencyNode = null;
    public List<PatternNode> cycleChildren = new ArrayList<>();
//    public boolean hasChild = true;
}

public class Vectorize5GUtil {

    public static JSONArray getJsonInfo(String jsonPath){
        File jsonFilePath = new File(jsonPath);
        String jsonInput = null;
        try {
            //读取json文件
            jsonInput = FileUtils.readFileToString(jsonFilePath, "UTF-8");
        }catch (Exception e){
            e.printStackTrace();
        }
        return JSON.parseArray(jsonInput.replace("\r\n", ""));
    }

    public static JSONArray readFile(String txtName, String jsonName, String stateId) {
        int i = 0,j=0;
        ArrayList<Object>transactions=new ArrayList<>();
//        ArrayList<Object>patterns=new ArrayList();
        JSONArray transctionData = getJsonInfo(jsonName);
        try {
            String csvLine,txtLine;
            //CSV部分
           /* BufferedReader csvReader = new BufferedReader(new FileReader(csvName));//换成你的文件名
            csvReader.readLine();
            while ((csvLine = csvReader.readLine()) != null) {
                Map<String,Object>pattern=new HashMap<>();
                //name
                pattern.put("name","pattern"+(i+1));
                //attr
                String items[] = csvLine.split(",");//CSV格式文件为逗号分隔符文件，这里根据逗号切分
                ArrayList<String>attributes=new ArrayList<>();
                for(String attribute:items) {
                    attributes.add(attribute);
                }
                pattern.put("attr",attributes);
                //children
                ArrayList<Integer>children=new ArrayList<>();
                pattern.put("children",children);
                //content
                Map<String,Object> content=new HashMap<>();
                pattern.put("content",content);
                patterns.add(pattern);
                i++;
            }*/
            //txt部分
            BufferedReader br = new BufferedReader(new FileReader(txtName));
            while((txtLine = br.readLine()) != null){
                Map<String,Object>transaction=new HashMap<>();
                //name
//                transaction.put("name","transaction_"+(j+1));
                transaction.put("name", transctionData.getJSONObject(j).getString("name"));
                transaction.put("id", j);
                //pattenid to name
                Map <Integer, String> id2Name = new HashMap<>();
                for (Object jb:transctionData.getJSONObject(j).getJSONArray("transactionlist").getJSONObject(0).getJSONArray("patternlist")
                     ) {
                    id2Name.put(((JSONObject)jb).getInteger("logkey"), ((JSONObject)jb).getString("patternid"));
                }
                //content
                ArrayList<Map> content=new ArrayList<>();
                String items[] = txtLine.split(" ");
                PatternNode startNode = new PatternNode(Integer.parseInt(items[0]));
                PatternNode nextNode = startNode;
                for (int k = 1; k < items.length; k++) {
                    nextNode.nextNode = new PatternNode(Integer.parseInt(items[k]));
                    nextNode = nextNode.nextNode;
                }
                nextNode = startNode;
                //处理子循环
                while (nextNode != null) {
//                    System.out.println(2);
                    if (nextNode.id < 0){
                        int cycle_length = (-nextNode.id)%1000;
                        nextNode.id = (-nextNode.id)/1000;
                        PatternNode begin = nextNode;
                        PatternNode child = begin;
                        nextNode.cycle_child = true;
                        for (int k = 1; k < cycle_length; k++) {
                            child = child.nextNode;
                            begin.cycleChildren.add(child);
                        }
                        begin.nextNode = child.nextNode;
                        if (cycle_length != 1){
                            child.nextNode = begin;
                        }
                        nextNode = begin.nextNode;
                    }else {
                        nextNode = nextNode.nextNode;
                    }
                }
                nextNode = startNode;
                Map<Integer, Integer> id_countMap = new HashMap<>();
                //处理并行
                while (nextNode != null) {
//                    System.out.println(3);
                    if (nextNode.id >= 1000){
                        nextNode.concurrencyNode = new PatternNode(nextNode.id%1000);
                        nextNode.concurrencyNode.cycleChildren = nextNode.cycleChildren;
                        nextNode.concurrencyNode.nextNode = nextNode.nextNode;
                        nextNode.id = nextNode.id/1000;
                    }
                    nextNode.setId_count(id_countMap);
                    if (nextNode.concurrencyNode != null){
                        nextNode.concurrencyNode.setId_count(id_countMap);
                    }
                    //进入循环
                    for (PatternNode iPNode:nextNode.cycleChildren
                         ) {
                        if (iPNode.id >= 1000){
                            iPNode.concurrencyNode = new PatternNode(nextNode.id%1000);
                            iPNode.concurrencyNode.cycleChildren = iPNode.cycleChildren;
                            iPNode.concurrencyNode.nextNode = iPNode.nextNode;
                            iPNode.id = iPNode.id/1000;
                        }
                        iPNode.setId_count(id_countMap);
                        if (iPNode.concurrencyNode != null){
                            iPNode.concurrencyNode.setId_count(id_countMap);
                        }
                    }
                    nextNode = nextNode.nextNode;
                }
                nextNode = startNode;
                while (nextNode != null) {
//                    System.out.println(1);
                    Map<String, Object> pattern = new HashMap<>();
                    pattern.put("id", String.valueOf(nextNode.id));
                    pattern.put("id_count", nextNode.id_count);
                    pattern.put("type", nextNode.type);
                    pattern.put("name", id2Name.get(nextNode.id));
                    List cyc_child = new ArrayList<>();
                    if (nextNode.cycle_child && nextNode.cycleChildren.isEmpty()){
                        cyc_child.add(-1);
                    }else if (nextNode.cycle_child){
                        for (PatternNode iPNode:nextNode.cycleChildren
                        ) {
                            boolean last = iPNode.nextNode == nextNode;
                            boolean nextCon = iPNode.nextNode != null && iPNode.nextNode.concurrencyNode != null;
                            Map<String, Object> iPattern = new HashMap<>();
                            iPattern.put("id", String.valueOf(iPNode.id));
                            iPattern.put("name", id2Name.get(iPNode.id));
                            iPattern.put("id_count", iPNode.id_count);
                            iPattern.put("type", iPNode.type);
                            iPattern.put("cycle_children", iPNode.cycleChildren);
                            iPattern.put("attr", iPNode.attr);
                            iPattern.put("children", new ArrayList<Integer>());
                            Map<String, Object> cPattern = null;
                            if (iPNode.concurrencyNode != null){
                                cPattern = new HashMap<>();
                                cPattern.put("id", String.valueOf(iPNode.concurrencyNode.id));
                                cPattern.put("name", id2Name.get(iPNode.concurrencyNode.id));
                                cPattern.put("id_count", iPNode.concurrencyNode.id_count);
                                cPattern.put("type", iPNode.concurrencyNode.type);
                                cPattern.put("cycle_children", iPNode.concurrencyNode.cycleChildren);
                                cPattern.put("attr", iPNode.concurrencyNode.attr);
                                cPattern.put("children", new ArrayList<Integer>());
                                if (last){
//                                    ((List)cPattern.get("children")).add(-1);
//                                    ((List)iPattern.get("children")).add(-1);
                                }else{
                                    ((List)cPattern.get("children")).add(cyc_child.size()+2);
                                    ((List)iPattern.get("children")).add(cyc_child.size()+2);
                                    if (nextCon){
                                        ((List)cPattern.get("children")).add(cyc_child.size()+3);
                                        ((List)iPattern.get("children")).add(cyc_child.size()+3);
                                    }
                                }
                            }else {
                                if (last){
//                                    ((List)iPattern.get("children")).add(-1);
                                }else{
                                    ((List)iPattern.get("children")).add(cyc_child.size()+1);
                                    if (nextCon){
                                        ((List)iPattern.get("children")).add(cyc_child.size()+2);
                                    }
                                }
                            }
                            cyc_child.add(iPattern);
                            if (cPattern != null) cyc_child.add(cPattern);
                        }
                    }
                    pattern.put("cycle_children", cyc_child);
                    pattern.put("attr", nextNode.attr);
                    pattern.put("children", new ArrayList<>());
                    boolean nextCon = nextNode.nextNode != null && nextNode.nextNode.concurrencyNode != null;
                    Map<String, Object> cPattern = null;
                    if (nextNode.concurrencyNode != null){
                        cPattern = new HashMap<>();
                        cPattern.put("id", String.valueOf(nextNode.concurrencyNode.id));
                        cPattern.put("name", id2Name.get(nextNode.concurrencyNode.id));
                        cPattern.put("id_count", nextNode.concurrencyNode.id_count);
                        cPattern.put("type", nextNode.concurrencyNode.type);
                        cPattern.put("cycle_children", nextNode.concurrencyNode.cycleChildren);
                        cPattern.put("attr", nextNode.concurrencyNode.attr);
                        cPattern.put("children", new ArrayList<Integer>());
                        if (nextNode.nextNode != null){
                            ((List)pattern.get("children")).add(content.size()+2);
                            ((List)cPattern.get("children")).add(content.size()+2);
                            if (nextCon){
                                ((List)pattern.get("children")).add(content.size()+3);
                                ((List)cPattern.get("children")).add(content.size()+3);
                            }
                        }
                    }else {
                        if (nextNode.nextNode != null){
                            ((List)pattern.get("children")).add(content.size()+1);
                            if (nextCon){
                                ((List)pattern.get("children")).add(content.size()+2);
                            }
                        }
                    }
//                    if (nextNode.nextNode != null && nextNode.nextNode.concurrencyNode != null){
//                        ((List)pattern.get("children")).add(content.size()+1);
//                        ((List)pattern.get("children")).add(content.size()+2);
////                        if (!nextNode.hasChild){
////                            ((List)pattern.get("children")).clear();
////                        }
//                        nextNode = nextNode.nextNode;
//                    }else if (nextNode.concurrencyNode != null){
//                        ((List)pattern.get("children")).add(content.size()+2);
//                        if (nextNode.nextNode == null || !nextNode.hasChild){
//                            ((List)pattern.get("children")).clear();
//                        }
//                        nextNode = nextNode.concurrencyNode;
//                    }else {
//                        ((List)pattern.get("children")).add(content.size()+1);
//                        if (nextNode.nextNode == null || !nextNode.hasChild){
//                            ((List)pattern.get("children")).clear();
//                        }
                    content.add(pattern);
                    if (cPattern != null) content.add(cPattern);
                    nextNode = nextNode.nextNode;
//                    }
                }
                transaction.put("content",content);
                transaction.put("children", new ArrayList<>());
                transactions.add(transaction);
                j++;
                ((List)transaction.get("children")).add(j);
            }
            ((Map)transactions.get(transactions.size()-1)).put("children", new ArrayList<>());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            File writename = new File(getFilePath("compareInfo")+stateId+".json"); // 相对路径，如果没有则要建立一个新的output。txt文件
            writename.createNewFile(); // 创建新文件
            BufferedWriter out = new BufferedWriter(new FileWriter(writename));
            out.write(new JSONArray(transactions).toJSONString()); // \r\n即为换行
            out.flush(); // 把缓存区内容压入文件
            out.close(); // 最后记得关闭文件
        }catch (Exception e){
            e.printStackTrace();
        }
//        System.out.print(new JSONArray(transactions));
        return new JSONArray(transactions);
    }

    public static Map readInforJson(String txtPath, String jsonPath, String stateId){
        List<String> txtInput = new ArrayList<>();
        try {
            //txt
            BufferedReader br = new BufferedReader(new FileReader(txtPath));
            String txtLine = null;
            while((txtLine = br.readLine()) != null){
                txtInput.add(txtLine);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        JSONArray transctionData = getJsonInfo(jsonPath);
        Map<String, Integer> transctionName2id = new HashMap();
        List<Map> transctions = new ArrayList<>();
        List<Map> transctionLinks = new ArrayList<>();
        for (int i = 0; i < transctionData.size(); i++) {
            Map transction = new HashMap();
            JSONObject jsonO = (JSONObject)transctionData.get(i);
            transctionName2id.put(jsonO.getString("name"), i);
            transction.put("name", jsonO.get("name"));
            transction.put("id", i);
            List<Map> patterns = new ArrayList<>();
            List<Map> patternLinks = new ArrayList<>();
//            List<Map> patternList = new HashMap<>();
            int lastLogKey = -1;
            for (Object j:jsonO.getJSONArray("transactionlist").getJSONObject(0).getJSONArray("patternlist")
                 ) {
                if ((int)((JSONObject)j).get("logkey") == lastLogKey){
                    continue;
                }else {
                    lastLogKey = (int)((JSONObject)j).get("logkey");
                }
                Map pattern = new HashMap();
                pattern.put("name", ((JSONObject)j).get("patternid"));
//                pattern.put("id", ((JSONObject)j).get("logkey"));
                pattern.put("logkey", ((JSONObject)j).get("logkey"));
                pattern.put("content", ((JSONObject)j).get("content"));
                List<Map> entities = new ArrayList<>();
                Map<String, Integer> entities2name = new HashMap<>();
                List<Map> links = new ArrayList<>();

                for (int k = 0; k < ((JSONObject)j).getJSONArray("entities").size(); k++) {
                    String entityName = ((JSONObject)j).getJSONArray("entities").getJSONObject(k).getString("name");
                    entities2name.put(entityName, k);
                    Map entity = new HashMap();
                    entity.put("name", entityName);
                    entity.put("type", "entity");
                    int attributeKey = ((JSONObject)j).getJSONArray("entities").size()*k+1;
                    for (Object jb:((JSONObject)j).getJSONArray("entities").getJSONObject(k).getJSONArray("attribute")
                         ) {
                        Map attribute = new HashMap();
                        attribute.put("name", jb.toString());
                        attribute.put("id", attributeKey);
                        attribute.put("type", "attribute");
                        entities.add(attribute);
                        Map link = new HashMap();
                        link.put("sid", attributeKey);
                        link.put("tid", k);
                        links.add(link);
                        attributeKey++;
                    }
//                    entity.put("attribute", ;
                    entity.put("id", k);
                    entities.add(entity);
                }
                pattern.put("nodes", entities);
                if (!"no relation".equals(((JSONObject)j).getJSONObject("relation").getString("name"))){
                    Map link = new HashMap();
                    link.put("sid", entities2name.get(((JSONObject)j).getJSONObject("relation").getJSONArray("entities").getString(0)));
                    link.put("tid", entities2name.get(((JSONObject)j).getJSONObject("relation").getJSONArray("entities").getString(1)));
                    links.add(link);
                }
                pattern.put("links", links);
                pattern.put("loop", 0);
//                idMapPattern.put((int)pattern.get("id"), pattern);
                patterns.add(pattern);
            }
//            Map<Integer, Integer> patternTimes = new HashMap<>();
            int loopTime = 0;
            int loopCount = 0;
            int patternId = 0;
            List<Integer> lastNodes = new ArrayList<>();
            for (String patternString:txtInput.get(i).split(" ")
                 ) {
                int patternNumber = Integer.parseInt(patternString);
                if (patternNumber < 0){
                    loopCount = (-patternNumber)%1000;
                    patternNumber = (-patternNumber)/1000;
                    loopTime++;
                }
                List<Integer> nowNodes = new ArrayList<>();
                if (patternNumber >= 1000){
                    nowNodes.add(patternNumber%1000);
                    nowNodes.add(patternNumber/1000);
                }else {
                    nowNodes.add(patternNumber);
                }
                for (int nowNodeNum:nowNodes
                     ) {
//                    patterns.add(idMapPattern.get(nowNodeNum));
                    patterns.get(patternId).put("id", patternId);
                    if (loopCount > 0){
                        patterns.get(patternId).put("loop", loopTime);
                    }
                    if (!lastNodes.isEmpty()){
                        for (Integer lastName: lastNodes
                             ) {
                            Map patternLink = new HashMap();
                            patternLink.put("sid", lastName);
                            patternLink.put("tid", patternId);
                            patternLinks.add(patternLink);
                        }
                    }
                    patternId += 1;
                }
                lastNodes.clear();
                lastNodes.add(patternId-1);
                if (nowNodes.size() == 2){
                    lastNodes.add(patternId-2);
                }
                if (loopCount > 0){
                    loopCount--;
                }
            }
            transction.put("nodes", patterns);
            transction.put("links", patternLinks);
            transctions.add(transction);

            if (!jsonO.getString("lastflow").equals("none")){
                Map<String, Integer> tranLink = new HashMap();
                tranLink.put("sid", transctionName2id.get(jsonO.getString("lastflow")));
                tranLink.put("tid", i);
                transctionLinks.add(tranLink);
            }
        }
        Map data = new HashMap();
        data.put("nodes", transctions);
        data.put("links", transctionLinks);


        try {
            File writename = new File(getFilePath("originalWorkflowInfo")+stateId+".json");
            writename.createNewFile(); // 创建新文件
            BufferedWriter out = new BufferedWriter(new FileWriter(writename));
            out.write(new JSONObject(data).toJSONString()); // \r\n即为换行
            out.flush(); // 把缓存区内容压入文件
            out.close(); // 最后记得关闭文件
        }catch (Exception e){
            e.printStackTrace();
        }
        return data;
    }

    private static String getPatternTimes(Map map, int id){
        if (map.containsKey(id)){
            map.put(id, (int)map.get(id)+1);
        }else {
            map.put(id, 1);
        }
        return ((Integer)id).toString()+"_"+map.get(id);
    }

    public static void main(String[] args){
//        readFile("/Users/jiang/Library/Containers/com.tencent.xinWeChat/Data/Library/Application Support/com.tencent.xinWeChat/2.0b4.0.9/4b6962f76687c3983d1dc38c8ecd88b9/Message/MessageTemp/82179e106359a79246b90256b693dac8/File/new2vectorize(1).txt",
//                "/Users/jiang/Library/Containers/com.tencent.xinWeChat/Data/Library/Application Support/com.tencent.xinWeChat/2.0b4.0.9/4b6962f76687c3983d1dc38c8ecd88b9/Message/MessageTemp/82179e106359a79246b90256b693dac8/File/data(4).json",
//                "1234");

        readInforJson(getFilePath("new2vectorize")+"new2vectorize(1).txt",
                "/Users/jiang/Library/Containers/com.tencent.xinWeChat/Data/Library/Application Support/com.tencent.xinWeChat/2.0b4.0.9/4b6962f76687c3983d1dc38c8ecd88b9/Message/MessageTemp/82179e106359a79246b90256b693dac8/File/data(5).json",
                "6675");
    }
}
