package util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.*;

import static javax.xml.transform.OutputKeys.ENCODING;

public class JsonUtil {

    Map map=new HashMap();

    public String jsonToXml(JSONObject jObj){
        try {
            StringBuffer buffer = new StringBuffer();
            buffer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            jsonToXmlstr(jObj,buffer);
            return buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String jsonToXmlstr(JSONObject jObj,StringBuffer buffer ){
        Set<Map.Entry<String, Object>> se = jObj.entrySet();
        for(Iterator<Map.Entry<String, Object>> it = se.iterator(); it.hasNext(); )
        {
            Map.Entry<String, Object> en = it.next();
            if(en.getValue()instanceof JSONObject){
                buffer.append("<"+en.getKey()+" "+map.get(en.getKey())+">");
                JSONObject jo = jObj.getJSONObject(en.getKey());
                jsonToXmlstr(jo,buffer);
                buffer.append("</"+en.getKey()+" "+map.get(en.getKey())+">");
            }else if(en.getValue()instanceof JSONArray){
                JSONArray jarray = jObj.getJSONArray(en.getKey());
                for (int i = 0; i < jarray.size(); i++) {
                    buffer.append("<"+en.getKey()+" "+map.get(en.getKey())+">");
                    JSONObject jsonobject =  jarray.getJSONObject(i);
                    jsonToXmlstr(jsonobject,buffer);
                    buffer.append("</"+en.getKey()+" "+map.get(en.getKey())+">");
                }
            }else{
                buffer.append("<"+en.getKey()+" "+map.get(en.getKey())+">"+en.getValue());
                buffer.append("</"+en.getKey()+" "+map.get(en.getKey())+">");
            }
        }
        return buffer.toString();
    }

    public void readJson()
    {
        File file = new File("F:\\Xlab\\test.json");
        try {
            String data = FileUtils.readFileToString(file);
            JSONArray array=JSON.parseArray(data);
            for(int i=0;i<array.size();i++){
                JSONObject object=array.getJSONObject(i);
                //Map<String, Object> myResult =parseJSON2Map(object);
                String xmlstr = jsonToXml(object);
                System.out.println(xmlstr);


            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*
    public Map<String, Object> parseJSON2Map(JSONObject json) {
        Map<String, Object> map = new HashMap<String, Object>();
        // 最外层解析
        for (Object k : json.keySet()) {
            Object v = json.get(k);
            // 如果内层还是json数组的话，继续解析
            if (v instanceof JSONArray) {
                List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                for (int i=0;i<((JSONArray) v).size();i++) {
                    JSONObject json2 =((JSONArray) v).getJSONObject(i);
                    list.add(parseJSON2Map(json2));
                }
                map.put(k.toString(), list);
            } else if (v instanceof JSONObject) {
                // 如果内层是json对象的话，继续解析
                map.put(k.toString(), parseJSON2Map((JSONObject) v));
            } else {
                // 如果内层是普通对象的话，直接放入map中
                map.put(k.toString(), v);
            }
        }
        return map;
    }
     */

    public Element readXmlRoot(String soucePath){
        try {
            File file = new File(soucePath);
            SAXReader read = new SAXReader();
            org.dom4j.Document doc = read.read(file);
            Element root = doc.getRootElement();
            return root;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    public void getNodes(Element node){
        //当前节点的名称、文本内容和属性
        String name=node.getName();
        String context=node.getTextTrim();
        if(context!=null){
            map.put(context,name);
        }
        //List<Attribute> listAttr=node.attributes();//当前节点的所有属性的list
        /*
        for(Attribute attr:listAttr){//遍历当前节点的所有属性
            String name=attr.getName();//属性名称
            String value=attr.getValue();//属性的值
            System.out.println("属性名称："+name+"属性值："+value);
        }
         */

        //递归遍历当前节点所有的子节点
        List<Element> listElement=node.elements();//所有一级子节点的list
        for(Element e:listElement){//遍历所有一级子节点
            getNodes(e);//递归
        }
    }

    public void readXMLModel(){
        Element root=readXmlRoot("F:\\Xlab\\testModel.XML");
        Map map=new HashMap();
        getNodes(root);
    }

    static public void readPodJSON(String fileName) throws IOException {
        File filePod = new File(fileName);
        String podData = FileUtils.readFileToString(filePod);
        JSONObject podObjects=JSON.parseObject(podData);
        String savePath = "F:\\Xlab\\json\\sockshop\\result";
        File folder = new File(savePath);
        //文件夹路径不存在
        if (!folder.exists() && !folder.isDirectory()) {
            folder.mkdirs();
        }
        File podJSONFile=new File(savePath+"\\pod.json");
        if(!podJSONFile.exists()){
            podJSONFile.createNewFile();
        }
        FileWriter writer =new FileWriter(podJSONFile);

        //准备写入的JSONObject
        JSONArray writeArray=new JSONArray();
        JSONObject writeObject=new JSONObject();

        JSONObject instanceDetail=podObjects.getJSONObject("detail");
        JSONObject instance=instanceDetail.getJSONObject("10.60.38.181");
        JSONArray resourceArray=instance.getJSONArray("resources");
        for(int i=0;i<resourceArray.size();i++){
            JSONObject resourceObject=resourceArray.getJSONObject(i);
            JSONObject podObject=new JSONObject();
            JSONObject metadata=resourceObject.getJSONObject("metadata");
            String podName=metadata.getString("name");
            podObject.put("name",podName);

            JSONObject service=metadata.getJSONObject("labels");
            String serviceName=service.getString("name");
            podObject.put("service",serviceName);

            JSONObject spec=resourceObject.getJSONObject("spec");
            JSONArray containerArray=spec.getJSONArray("containers");
            JSONObject containerInstance=containerArray.getJSONObject(0);
            String container=containerInstance.getString("name");
            podObject.put("container",container);

            JSONObject status=resourceObject.getJSONObject("status");
            String server=status.getString("hostIP");
            podObject.put("server",server);
            writeArray.add(podObject);
        }
        writeObject.put("pods",writeArray);
        String content=JSON.toJSONString(writeObject);
        writer.write(content);
        writer.flush();

    }

    static public void readServiceJSON(String fileName) throws IOException {
        File fileService = new File(fileName);
        String serviceData = FileUtils.readFileToString(fileService);
        JSONObject serviceObjects = JSON.parseObject(serviceData);
        String savePath = "F:\\Xlab\\json\\sockshop\\result";
        File folder = new File(savePath);
        //文件夹路径不存在
        if (!folder.exists() && !folder.isDirectory()) {
            folder.mkdirs();
        }
        File serviceJSONFile = new File(savePath + "\\service.json");
        if (!serviceJSONFile.exists()) {
            serviceJSONFile.createNewFile();
        }
        FileWriter writer = new FileWriter(serviceJSONFile);

        //准备写入的JSONObject
        JSONArray writeArray = new JSONArray();
        JSONObject writeObject = new JSONObject();

        JSONArray success=serviceObjects.getJSONArray("success");
        for(int i=0;i<success.size();i++){
            JSONObject ins=success.getJSONObject(i);
            String name=ins.getString("name");
            JSONObject service=new JSONObject();
            service.put("name",name);
            writeArray.add(service);
        }
        writeObject.put("services",writeArray);
        String content=JSON.toJSONString(writeObject);
        writer.write(content);
        writer.flush();
    }

    static public void readNamespaceJSON(String fileName) throws IOException {
        File fileNamespace = new File(fileName);
        String namespaceData = FileUtils.readFileToString(fileNamespace);
        JSONObject namespaceObjects = JSON.parseObject(namespaceData);
        String savePath = "F:\\Xlab\\json\\sockshop\\result";
        File folder = new File(savePath);
        //文件夹路径不存在
        if (!folder.exists() && !folder.isDirectory()) {
            folder.mkdirs();
        }
        File namespaceJSONFile = new File(savePath + "\\namespace.json");
        if (!namespaceJSONFile.exists()) {
            namespaceJSONFile.createNewFile();
        }
        FileWriter writer = new FileWriter(namespaceJSONFile);

        //准备写入的JSONObject
        JSONArray writeArray = new JSONArray();
        JSONObject writeObject = new JSONObject();

        JSONArray success=namespaceObjects.getJSONArray("success");
        for(int i=0;i<success.size();i++){
            JSONObject ins=success.getJSONObject(i);
            String name=ins.getString("name");
            JSONObject namespace=new JSONObject();
            namespace.put("name",name);
            writeArray.add(namespace);
        }
        writeObject.put("namespaces",writeArray);
        String content=JSON.toJSONString(writeObject);
        writer.write(content);
        writer.flush();
    }

    static public void readNodeJSON(String fileName) throws IOException {
        File fileNode = new File(fileName);
        String nodeData = FileUtils.readFileToString(fileNode);
        JSONObject nodeObjects = JSON.parseObject(nodeData);
        String savePath = "F:\\Xlab\\json\\sockshop\\result";
        File folder = new File(savePath);
        //文件夹路径不存在
        if (!folder.exists() && !folder.isDirectory()) {
            folder.mkdirs();
        }
        File namespaceJSONFile = new File(savePath + "\\node.json");
        if (!namespaceJSONFile.exists()) {
            namespaceJSONFile.createNewFile();
        }
        FileWriter writer = new FileWriter(namespaceJSONFile);

        //准备写入的JSONObject
        JSONArray writeArray = new JSONArray();
        JSONObject writeObject = new JSONObject();

        JSONArray success=nodeObjects.getJSONArray("success");
        for(int i=0;i<success.size();i++){
            JSONObject ins=success.getJSONObject(i);
            String name=ins.getString("name");
            JSONObject node=new JSONObject();
            node.put("name",name);
            writeArray.add(node);
        }
        writeObject.put("nodes",writeArray);
        String content=JSON.toJSONString(writeObject);
        writer.write(content);
        writer.flush();
    }

    static public void readAttributeJSON(String fileName) throws IOException {
        File fileAttribute = new File(fileName);
        String attributeData = FileUtils.readFileToString(fileAttribute);
        JSONObject attributeObjects = JSON.parseObject(attributeData);
        String savePath = "F:\\Xlab\\json\\sockshop\\result";
        File folder = new File(savePath);
        //文件夹路径不存在
        if (!folder.exists() && !folder.isDirectory()) {
            folder.mkdirs();
        }
        File attributeFile = new File(savePath + "\\attribute.ttl");
        if (!attributeFile.exists()) {
            attributeFile.createNewFile();
        }
        FileWriter writer = new FileWriter(attributeFile);

        JSONArray bindings=attributeObjects.getJSONArray("bindings");
        for(int i=0;i<bindings.size();i++){
            JSONObject attributeObj=bindings.getJSONObject(i);
            JSONObject provideS=attributeObj.getJSONObject("provideS");
            JSONObject provideP=attributeObj.getJSONObject("provideP");
            JSONObject objectO=attributeObj.getJSONObject("object");

            String subject=provideS.getString("value");
            String provide=provideP.getString("value");
            String object=objectO.getString("value");


            writer.write("<"+subject+"> <"+provide+"> "+object+"\n\n");
            writer.flush();
        }
    }


    public static void main(String[] args) throws IOException {
        //JsonUtil exm=new JsonUtil();
        //exm.readXMLModel();
        //exm.readJson();
        //readPodJSON("F:\\Xlab\\json\\sockshop\\pod_sock-shop.json");
        //readServiceJSON("F:\\Xlab\\json\\sockshop\\service_sock-shop.json");
        //readNamespaceJSON("F:\\Xlab\\json\\sockshop\\namespace.json");
        //readNodeJSON("F:\\Xlab\\json\\sockshop\\node.json");
        readAttributeJSON("/Users/jiang/Downloads/attribute.json");
    }
}