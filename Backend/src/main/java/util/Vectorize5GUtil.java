package util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
//    public String cycle_child = "";
    public PatternNode nextNode = null;
    public PatternNode concurrencyNode = null;
    public PatternNode cycleChild = null;
    public boolean hasChild = true;
}

public class Vectorize5GUtil {

    public static void readFile(String txtName, String csvName) {
        int i = 0,j=0;
        ArrayList<Object>transactions=new ArrayList<>();
//        ArrayList<Object>patterns=new ArrayList();
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
                transaction.put("name","transaction_"+(j+1));
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
                    if (nextNode.id < 0){
                        int cycle_length = -nextNode.id%1000;
                        nextNode.id = -nextNode.id/1000;
                        PatternNode begin = nextNode;
                        for (int k = 0; k < cycle_length; k++) {
                            if (k == cycle_length -1) {
                                nextNode.cycleChild = begin;
                            }else {
                                nextNode.hasChild = false;
                                nextNode.cycleChild = nextNode.nextNode;
                            }
                            nextNode = nextNode.nextNode;
                        }
                    }else {
                        nextNode = nextNode.nextNode;
                    }
                }
                nextNode = startNode;
                Map<Integer, Integer> id_countMap = new HashMap<>();
                //处理并行
                while (nextNode != null) {
                    if (nextNode.id >= 1000){
                        nextNode.concurrencyNode = new PatternNode(nextNode.id%1000);
                        nextNode.concurrencyNode.cycleChild = nextNode.cycleChild;
                        nextNode.concurrencyNode.nextNode = nextNode.nextNode;
                        nextNode.id = nextNode.id/1000;
                    }
                    nextNode.setId_count(id_countMap);
                    if (nextNode.concurrencyNode != null){
                        nextNode.concurrencyNode.setId_count(id_countMap);
                    }
                    nextNode = nextNode.nextNode;
                }
                nextNode = startNode;
                while (nextNode != null) {
                    Map<String, Object> pattern = new HashMap<>();
                    pattern.put("id", String.valueOf(nextNode.id));
                    pattern.put("id_count", nextNode.id_count);
                    pattern.put("type", nextNode.type);
                    List<String> cyc_child = new ArrayList<>();
                    if (nextNode.cycleChild != null){
                        cyc_child.add(nextNode.cycleChild.id+"_"+nextNode.cycleChild.id_count);
                        if (nextNode.cycleChild.concurrencyNode != null){
                            cyc_child.add(nextNode.cycleChild.concurrencyNode.id+"_"+nextNode.cycleChild.concurrencyNode.id_count);
                        }
                    }
                    pattern.put("cycle_child", cyc_child);
                    pattern.put("attr", nextNode.attr);
                    pattern.put("children", new ArrayList<>());
                    if (nextNode.nextNode != null && nextNode.nextNode.concurrencyNode != null){
                        ((List)pattern.get("children")).add(content.size()+1);
                        ((List)pattern.get("children")).add(content.size()+2);
                        if (!nextNode.hasChild){
                            ((List)pattern.get("children")).clear();
                        }
                        nextNode = nextNode.nextNode;
                    }else if (nextNode.concurrencyNode != null){
                        ((List)pattern.get("children")).add(content.size()+2);
                        if (nextNode.nextNode == null || !nextNode.hasChild){
                            ((List)pattern.get("children")).clear();
                        }
                        nextNode = nextNode.concurrencyNode;
                    }else {
                        ((List)pattern.get("children")).add(content.size()+1);
                        if (nextNode.nextNode == null || !nextNode.hasChild){
                            ((List)pattern.get("children")).clear();
                        }
                        nextNode = nextNode.nextNode;
                    }
                    content.add(pattern);
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
            File writename = new File("/Users/jiang/re-vec.json"); // 相对路径，如果没有则要建立一个新的output。txt文件
            writename.createNewFile(); // 创建新文件
            BufferedWriter out = new BufferedWriter(new FileWriter(writename));
            out.write(new JSONArray(transactions).toJSONString()); // \r\n即为换行
            out.flush(); // 把缓存区内容压入文件
            out.close(); // 最后记得关闭文件
        }catch (Exception e){
            e.printStackTrace();
        }
//        System.out.print(new JSONArray(transactions));
    }
    public static void main(String[] args){
        readFile("/Users/jiang/Library/Containers/com.tencent.xinWeChat/Data/Library/Application Support/com.tencent.xinWeChat/2.0b4.0.9/4b6962f76687c3983d1dc38c8ecd88b9/Message/MessageTemp/82179e106359a79246b90256b693dac8/File/new2vectorize.txt",
                "wqdq");
    }
}
