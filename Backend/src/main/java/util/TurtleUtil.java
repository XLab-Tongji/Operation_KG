package util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.apache.commons.io.FileUtils;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static global.globalvalue.*;

public class TurtleUtil {


    public static void readOntologyJson(String fileName) throws IOException {
//        File fileOntology = new File(TurtleUtil.class.getResource("/").getPath().replace("classes","upload/type")+fileName+".json");
        File fileOntology = new File(path2TypeJson+fileName+".json");
        String ontologyData = FileUtils.readFileToString(fileOntology);
        JSONObject ontology=JSON.parseObject(ontologyData);
//        String savePath = TurtleUtil.class.getResource("/").getPath().replace("classes","turtle/type");
        String savePath = path2TypeTtl;
        File folder = new File(savePath);
        //文件夹路径不存在
        if (!folder.exists() && !folder.isDirectory()) {
            folder.mkdirs();
        }
        File ontologyTtlFile=new File(savePath + fileName + ".ttl");
        ontologyTtlFile.setExecutable(true);
        ontologyTtlFile.setReadable(true);
        ontologyTtlFile.setWritable(true);
        if(!ontologyTtlFile.exists()){
            ontologyTtlFile.createNewFile();
            ontologyTtlFile.setExecutable(true);
            ontologyTtlFile.setReadable(true);
            ontologyTtlFile.setWritable(true);
        }
        FileWriter ontologyTtlWriter =new FileWriter(ontologyTtlFile);
        //命名空间声明
        ontologyTtlWriter.write("@prefix :      <http://10.60.38.181/KGns/> .\n" +
                "@prefix owl:   <http://www.w3.org/2002/07/owl#> .\n" +
                "@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" +
                "@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" +
                "@prefix services: <http://10.60.38.181/KGns/Service/> .\n" +
                "@prefix environment: <http://10.60.38.181/KGns/Environment/> .\n" +
                "@prefix servers_rel: <http://servers/10.60.38.181/> .\n" +
                "@prefix pods_rel: <http://pods/10.60.38.181/> .\n" +
                "@prefix namespace_rel: <http://namespace/10.60.38.181/> .\n" +
                "@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
                "@prefix namespace: <http://10.60.38.181/KGns/Namespace/> .\n" +
                "@prefix rel:   <http://10.60.38.181/KGns/relationship/> .\n" +
                "@prefix containers: <http://10.60.38.181/KGns/Container/> .\n" +
                "@prefix pods:  <http://10.60.38.181/KGns/Pod/> .\n" +
                "@prefix containers_rel: <http://containers/10.60.38.181/> .\n" +
                "@prefix environment_rel: <http://environment/10.60.38.181/> .\n" +
                "@prefix services_rel: <http://services/10.60.38.181/> .\n\n");
        ontologyTtlWriter.flush();
        ontologyJsonToTurtle(ontology,ontologyTtlWriter);

    }

    public static void ontologyJsonToTurtle(JSONObject object,FileWriter writer) throws IOException {
        JSONArray types = object.getJSONArray("types");
        List<String>attributeList=new ArrayList<>();
        for (int i = 0; i < types.size(); i++) {
            String type = types.getString(i);
            writer.write(":" + type + " rdf:type owl:Class .\n");
            writer.flush();
        }
        writer.write("\n");
        writer.flush();
        JSONArray relInfo = object.getJSONArray("rel_info");
        for (int i = 0; i < relInfo.size(); i++) {
            JSONObject rel = relInfo.getJSONObject(i);
            String relType = rel.getString("type");
            String domain=relType+"_attributes";
            JSONArray linkNodes = rel.getJSONArray("link_to_other_nodes");
            for (int j = 0; j < linkNodes.size(); j++) {
                JSONObject obj = linkNodes.getJSONObject(j);
                String relation = obj.getString("relation");
                String to = obj.getString("to");
                writer.write(relation + " rdf:type owl:ObjectProperty;\n\t\t");
                writer.flush();
                writer.write("rdfs:domain :" + relType + ";\n\t\t");
                writer.flush();
                writer.write("rdfs:range :" + to + " .\n\n");
                writer.flush();
            }
            JSONArray attributes = rel.getJSONArray("attributes");
            for (int k = 0; k < attributes.size(); k++) {
                String attribute=attributes.getString(k);
                attributeList.add(domain+"|"+attribute+"|"+relType);
            }
        }
        for (String s:attributeList){
            String[]input=s.split("\\|");
            writer.write(input[0]+":"+input[1]+" rdf:type owl:ObjectProperty;\n\t\trdfs:domain :"+input[2]+" .\n\n");
            writer.flush();
        }
    }

    public static  void readSystemJson(String fileName) throws IOException {
//        File fileSystem = new File(TurtleUtil.class.getResource("/").getPath().replace("classes","upload/system")+fileName+".json");
        File fileSystem = new File(path2SystemJson+fileName+".json");
        String systemData = FileUtils.readFileToString(fileSystem);
        JSONObject system=JSON.parseObject(systemData);
//        String savePath = TurtleUtil.class.getResource("/").getPath().replace("classes","turtle/system");
        String savePath = path2SystemTtl;
        File folder = new File(savePath);
        //文件夹路径不存在
        if (!folder.exists() && !folder.isDirectory()) {
            folder.mkdirs();
        }
        File systemTtlFile=new File(savePath + fileName + ".ttl");
        systemTtlFile.setExecutable(true);
        systemTtlFile.setReadable(true);
        systemTtlFile.setWritable(true);
        if(!systemTtlFile.exists()){
            systemTtlFile.createNewFile();
            systemTtlFile.setExecutable(true);
            systemTtlFile.setReadable(true);
            systemTtlFile.setWritable(true);
        }
        FileWriter systemTtlWriter =new FileWriter(systemTtlFile);
        //命名空间声明
        systemTtlWriter.write("@prefix :      <http://10.60.38.181/KGns/> .\n" +
                "@prefix owl:   <http://www.w3.org/2002/07/owl#> .\n" +
                "@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" +
                "@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" +
                "@prefix services: <http://10.60.38.181/KGns/Service/> .\n" +
                "@prefix environment: <http://10.60.38.181/KGns/Environment/> .\n" +
                "@prefix servers_rel: <http://servers/10.60.38.181/> .\n" +
                "@prefix pods_rel: <http://pods/10.60.38.181/> .\n" +
                "@prefix namespace_rel: <http://namespace/10.60.38.181/> .\n" +
                "@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
                "@prefix namespace: <http://10.60.38.181/KGns/Namespace/> .\n" +
                "@prefix rel:   <http://10.60.38.181/KGns/relationship/> .\n" +
                "@prefix containers: <http://10.60.38.181/KGns/Container/> .\n" +
                "@prefix pods:  <http://10.60.38.181/KGns/Pod/> .\n" +
                "@prefix containers_rel: <http://containers/10.60.38.181/> .\n" +
                "@prefix environment_rel: <http://environment/10.60.38.181/> .\n" +
                "@prefix services_rel: <http://services/10.60.38.181/> .\n\n");
        systemTtlWriter.flush();
        systemJsonToTurtle(system,systemTtlWriter);
    }

    public static void systemJsonToTurtle(JSONObject object,FileWriter writer) throws IOException {
        List serverList=new ArrayList();
        JSONArray namespaces=object.getJSONArray("namespaces");
        JSONArray services=object.getJSONArray("services");
        JSONArray servers=object.getJSONArray("servers");
        JSONArray pods=object.getJSONArray("pods");
        for(int i=0;i< servers.size();i++){
            JSONObject serverObj=servers.getJSONObject(i);
            String server="http://server/10.60.38.181/"+serverObj.getString("name");
            serverList.add(server);
            writer.write("<"+server+">\n"+
                    " rdf:type :Server .\n\n");
            writer.flush();
            writer.write("<"+server+"/manage>\n"+
                    "rdf:type     owl:ObjectProperty ;\n" +
                    " rdfs:subPropertyOf  servers_rel:manage .\n\n");
            writer.flush();
        }

        for(int i=0;i<serverList.size();i++){
            writer.write("<http://environment/10.60.38.181> "+"<http://environment/10.60.38.181/has> <"+serverList.get(i)+"> .\n");
            writer.flush();
            writer.write("<http://server/10.60.38.181/192.168.199.191> "+"<http://server/10.60.38.181/192.168.199.191/manage> <"+serverList.get(i)+"> .\n");
            writer.flush();
        }

        writer.write("\n");
        writer.flush();

        for(int i=0;i<namespaces.size();i++)
        {
            JSONObject namespaceObj=namespaces.getJSONObject(i);
            String namespace="http://namespace/10.60.38.181/"+namespaceObj.getString("name");
            writer.write("<"+namespace+">\n"+
                    " rdf:type :Namespace .\n\n");
            writer.flush();
            writer.write("<"+namespace+"/supervises>\n"+
                    "rdf:type     owl:ObjectProperty ;\n" +
                    " rdfs:subPropertyOf  namespace_rel:supervises .\n\n");
            writer.flush();
        }

        for(int i=0;i<services.size();i++){
            JSONObject serviceObj=services.getJSONObject(i);
            String service="http://services/10.60.38.181/sock-shop/"+serviceObj.getString("name");
            writer.write("<"+service+">\n"+
                    " rdf:type :Service .\n\n");
            writer.flush();
        }

        for(int i=0;i<pods.size();i++){
            JSONObject podObj=pods.getJSONObject(i);
            String container="http://containers/10.60.38.181/sock-shop/"+podObj.getString("container");
            writer.write("<"+container+">\n"+
                    " rdf:type :Container .\n\n");
            writer.flush();
            String pod="http://pods/10.60.38.181/sock-shop/"+podObj.getString("name");
            writer.write("<"+pod+">\n"+
                    " rdf:type :Pod .\n\n");
            writer.flush();

            writer.write("<"+pod+"/provides>\n"+
                    "rdf:type     owl:ObjectProperty ;\n" +
                    " rdfs:subPropertyOf  pods_rel:provides .\n\n");
            writer.flush();

            writer.write("<"+pod+"/deployed_in>\n"+
                    "rdf:type     owl:ObjectProperty ;\n" +
                    " rdfs:subPropertyOf  pods_rel:deployed_in .\n\n");
            writer.flush();

            writer.write("<"+pod+"/contains>\n"+
                    "rdf:type     owl:ObjectProperty ;\n" +
                    " rdfs:subPropertyOf  pods_rel:contains .\n\n");
            writer.flush();
        }

        for(int i=0;i<pods.size();i++) {
            JSONObject podObj = pods.getJSONObject(i);
            String pod="http://pods/10.60.38.181/sock-shop/"+podObj.getString("name");
            String container="http://containers/10.60.38.181/sock-shop/"+podObj.getString("container");
            String server="http://server/10.60.38.181/"+podObj.getString("server");
            String service="http://services/10.60.38.181/sock-shop/"+podObj.getString("service");

            writer.write("<"+pod+"> <"+pod+"/contains> <"+container+"> .\n\n");
            writer.flush();

            writer.write("<"+pod+"> <"+pod+"/provides> <"+service+"> .\n\n");
            writer.flush();

            writer.write("<"+pod+"> <"+pod+"/deployed_in> <"+server+"> .\n\n");
            writer.flush();
        }
    }


    public static void main(String[] args) throws IOException {
        //readOntologyJson("ontology");
        readSystemJson("system");
    }
}