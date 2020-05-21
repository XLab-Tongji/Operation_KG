package neo4j;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import global.globalvalue;
import org.bson.Document;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static util.Vectorize5GUtil.getJsonInfo;

public class MongoDriver {

    //连接到mongodb服务
    private MongoClient mongoClient = new MongoClient(globalvalue.mongosapi, globalvalue.mongosPort);
    //连接到数据库
    private MongoDatabase mongoDatabase = mongoClient.getDatabase("knowledgegraph");
    private MongoCollection<Document> collection = mongoDatabase.getCollection("query_statements");

    public boolean save_data(Map<String, Object> data){
        try {
            Document document = new Document();
            for (String key : data.keySet()) {
                document.append(key, data.get(key));
            }
            collection.insertOne(document);
            System.out.println("文档插入成功");
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public List<Document> get_data(){
        FindIterable<Document> findIterable = collection.find();
        MongoCursor<Document> mongoCursor = findIterable.iterator();
        List<Document> result = new ArrayList<>();
        while(mongoCursor.hasNext()){
            Document d = mongoCursor.next();
            result.add(d);
        }
        return result;
    }

    public boolean clear_db(){
        try {
            collection.deleteMany(new Document());
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean save2Mongo(Map<String, Object> data){
        try {
            //连接到mongodb服务
            MongoClient mongoClient = new MongoClient(globalvalue.mongosapi, globalvalue.mongosPort);
            //连接到数据库
            MongoDatabase mongoDatabase = mongoClient.getDatabase("knowledgegraph");
            MongoCollection<Document> collection = mongoDatabase.getCollection("info");
            //获取当前时间
            Date day=new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = df.format(day);
            System.out.println(time);
            //插入文档
            Document document = new Document(data).
                    append("time", time);
            collection.insertOne(document);
            System.out.println("文档插入成功");
//            if(storeTimestamp(time)==null)
//                return false;
            mongoClient.close();
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean save2MongoByTime(Map<String, Object> data, String time){
        try {
            //连接到mongodb服务
            MongoClient mongoClient = new MongoClient(globalvalue.mongosapi, globalvalue.mongosPort);
            //连接到数据库
            MongoDatabase mongoDatabase = mongoClient.getDatabase("knowledgegraph");
            MongoCollection<Document> collection = mongoDatabase.getCollection("info");
            //插入文档
            Document document = new Document(data).
                    append("time", time);
            collection.insertOne(document);
            System.out.println("文档插入成功");
//            if(storeTimestamp(time)==null)
//                return false;
            mongoClient.close();
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean saveKapacitor2Mongo(String message){
        try {
            //连接到mongodb服务
            MongoClient mongoClient = new MongoClient(globalvalue.mongosapi, globalvalue.mongosPort);
            //连接到数据库
            MongoDatabase mongoDatabase = mongoClient.getDatabase("knowledgegraph");
            MongoCollection<Document> collection = mongoDatabase.getCollection("Kapacitor");
            //获取当前时间
            Date day=new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = df.format(day);
            System.out.println(time);
            Map<String, Object> data = new HashMap<>();
            data.put("message", message);
            data.put("type", 1);
            //插入文档
            Document document = new Document(data).
                    append("time", time);
            collection.insertOne(document);
            System.out.println("文档插入成功");
//            if(storeTimestamp(time)==null)
//                return false;
            mongoClient.close();
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean saveEvent2Mongo(String content, String source){
        try {
            //连接到mongodb服务
            MongoClient mongoClient = new MongoClient(globalvalue.mongosapi, globalvalue.mongosPort);
            //MongoClient mongoClient = new MongoClient("10.60.38.173", 27020);
            //连接到数据库
            MongoDatabase mongoDatabase = mongoClient.getDatabase("knowledgegraph");
            MongoCollection<Document> collection = mongoDatabase.getCollection("Event");
            //获取当前时间
            Date day=new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = df.format(day);
            System.out.println(time);
            Map<String, Object> data = new HashMap<>();
            data.put("content", content);
            data.put("source", source);
            switch (source) {
                case "Kapacitor":
                    data.put("type", 1);
                    break;
                case "K8s":
                    data.put("type", 2);
                    break;
                    default:
                        data.put("type", 0);
            }
            //插入文档
            Document document = new Document(data).
                    append("time", time);
            collection.insertOne(document);
            System.out.println("文档插入成功");
            mongoClient.close();
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static Map<String, Object> getOneFromMongo(String time){
        try {
            //连接到mongodb服务
            MongoClient mongoClient = new MongoClient(globalvalue.mongosapi, globalvalue.mongosPort);
            //连接到数据库
            MongoDatabase mongoDatabase = mongoClient.getDatabase("knowledgegraph");
            MongoCollection<Document> collection = mongoDatabase.getCollection("info");
            //单一条件查询
            FindIterable<Document> findIterable = collection.find(Filters.eq("time", time));//如果只有一个条件可以用这样的形式
            MongoCursor<Document> mongoCursor = findIterable.iterator();
            List<Map<String, Object>> result = new ArrayList<>();
            while(mongoCursor.hasNext()){
                Document d=mongoCursor.next();
                System.out.println(d.get("linkList"));
                System.out.println(d.get("nodeList"));
                Map<String, Object> map = new HashMap<>();
                map.putAll(d);
                result.add(map);
            }
            mongoClient.close();
            if(result.size()==0) return null;
            System.out.println(result.get(0));
            return result.get(0);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static List<List> getEventMongByTime(String startDate,String startTime, String endDate,String endTime){
        String start=startDate+' '+startTime + ":00";
        String end=endDate+' '+endTime + ":59";
        List<List> result = new ArrayList<>();
        try {
            //连接到mongodb服务
            MongoClient mongoClient = new MongoClient(globalvalue.mongosapi, globalvalue.mongosPort);//连接到数据库
            MongoDatabase mongoDatabase = mongoClient.getDatabase("knowledgegraph");
            MongoCollection<Document> collection = mongoDatabase.getCollection("info");
            //多条件查询
            FindIterable<Document> findIterable = collection.find(Filters.and(Filters.gte("time", start), Filters.lte("time", end)));
            MongoCursor<Document> mongoCursor = findIterable.iterator();
            //System.out.println(mongoCursor);
            while(mongoCursor.hasNext()){
                Document d=mongoCursor.next();
                //System.out.println(d);
                JSONObject json=JSONObject.parseObject(d.toJson());
                JSONArray eventList=json.getJSONArray("eventList");
                if (eventList.size()==0)continue;
                String eventType=eventList.getJSONObject(eventList.size() - 1).getString("type");
                ArrayList aL = new ArrayList();
                Date date = new Date();
                SimpleDateFormat DateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //加上时间
                try {
                    date=DateFormat.parse(d.get("time").toString());
                } catch(ParseException px) {
                    px.printStackTrace();
                }
                aL.add(date);
                if (eventType.equals("event")){
                    aL.add(0);
                }
//                System.out.println(aL);
                result.add(aL);
            }

            MongoCollection<Document> collectionK = mongoDatabase.getCollection("Kapacitor");
            //多条件查询
            FindIterable<Document> findIterableK = collectionK.find(Filters.and(Filters.gte("time", start), Filters.lte("time", end)));
            MongoCursor<Document> mongoCursorK = findIterableK.iterator();
            //System.out.println(mongoCursor);
            while(mongoCursorK.hasNext()){
                Document d=mongoCursorK.next();
                ArrayList aL = new ArrayList();
                Date date = new Date();
                SimpleDateFormat DateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //加上时间
                try {
                    date=DateFormat.parse(d.get("time").toString());
                } catch(ParseException px) {
                    px.printStackTrace();
                }
                aL.add(date);
                aL.add(d.getInteger("type"));
                result.add(aL);
            }
            mongoClient.close();
            if(result.size()==0) return null;
            //System.out.println(result);
            return result;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<Map<String, Object>> getAllQuery(){
        ArrayList<Map<String, Object>> result = new ArrayList<>();
        try {
            MongoDriver mongoDriver = new MongoDriver();
            List<Document> data = mongoDriver.get_data();
            for (Document d: data) {
                Map<String, Object> map = new HashMap<>();
                map.putAll(d);
                result.add(map);
            }
            if(result.size()==0)
                return null;
            return result;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static List<String> getTimesFromMongo(){
        MongoClient mongoClient = new MongoClient(globalvalue.mongosapi, globalvalue.mongosPort);
        MongoDatabase mongoDatabase = mongoClient.getDatabase("knowledgegraph");
        MongoCollection<Document> collection = mongoDatabase.getCollection("info");
        FindIterable<Document> findIterable = collection.find();
        MongoCursor<Document> mongoCursor = findIterable.iterator();
        List<String> result = new ArrayList<>();
        while(mongoCursor.hasNext()){
            Document d=mongoCursor.next();
            System.out.println(d.get("time"));
            result.add(d.get("time").toString());
        }
        mongoClient.close();
        return result;
    }

    public static boolean saveSystemTypeAndNameFile(String type, String name){
        try {
            //连接到mongodb服务
            MongoClient mongoClient = new MongoClient(globalvalue.mongosapi, globalvalue.mongosPort);
//            MongoClient mongoClient = new MongoClient("10.60.38.173", 27117);
            //连接到数据库
            MongoDatabase mongoDatabase = mongoClient.getDatabase("knowledgegraph");
            MongoCollection<Document> collection = mongoDatabase.getCollection("SystemTypeAndName");
            //判断是否当前系统类型是否存在
            BasicDBObject query = new BasicDBObject();
            query.put("type",type);
            FindIterable<Document> findIterable = collection.find(query);
            MongoCursor<Document> mongoCursor = findIterable.iterator();
            if (!mongoCursor.hasNext()){
                Map<String, Object> data = new HashMap<>();
                data.put("type",type);
                data.put("name",new ArrayList<String>());
                //插入文档
                Document document = new Document(data);
                collection.insertOne(document);
                System.out.println("文档插入成功");
            }
            else {
                Document d=mongoCursor.next();
                //System.out.println(d);
                JSONObject json=JSONObject.parseObject(d.toJson());
                JSONArray jsonArray = json.getJSONArray("name");
                jsonArray.add(name);
//                DBCollection dbCol = db.getCollection(COLLECTION_NAME);
//                DBCursor ret = dbCol.find();
                BasicDBObject doc = new BasicDBObject();
                BasicDBObject res = new BasicDBObject();
                res.put("name", JSONObject.parseArray(jsonArray.toString(), String.class));
                doc.put("$set", res);
                collection.updateMany(query,doc);
                System.out.println("文档修改成功");
                mongoClient.close();
            }
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static JSONArray getAllTypeAndName(){
        JSONArray re = new JSONArray();
        try {
            //连接到mongodb服务
            MongoClient mongoClient = new MongoClient(globalvalue.mongosapi, globalvalue.mongosPort);
//            MongoClient mongoClient = new MongoClient("10.60.38.173", 27020);
            //连接到数据库
            MongoDatabase mongoDatabase = mongoClient.getDatabase("knowledgegraph");
            MongoCollection<Document> collection = mongoDatabase.getCollection("SystemTypeAndName");
            FindIterable<Document> findIterable = collection.find();
            MongoCursor<Document> mongoCursor = findIterable.iterator();
            while (mongoCursor.hasNext()){
                Document d=mongoCursor.next();
                re.add(JSONObject.parseObject(d.toJson()));
            }
            mongoClient.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        return re;
    }

    public static JSONArray getCorrelation(){
        JSONArray re = new JSONArray();
        try {
            //连接到mongodb服务
            MongoClient mongoClient = new MongoClient(globalvalue.mongosapi, globalvalue.mongosPort);
//            MongoClient mongoClient = new MongoClient("10.60.38.173", 27020);
            //连接到数据库
            MongoDatabase mongoDatabase = mongoClient.getDatabase("knowledgegraph");
            MongoCollection<Document> collection = mongoDatabase.getCollection("correlation");
            FindIterable<Document> findIterable = collection.find();
            MongoCursor<Document> mongoCursor = findIterable.iterator();
            while (mongoCursor.hasNext()){
                Document d=mongoCursor.next();
                re.add(JSONObject.parseObject(d.toJson()));
            }
            mongoClient.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        return re;
    }

    public static boolean saveKPI2mongo(String kpi, String event, String co){
        try {
            //连接到mongodb服务
            MongoClient mongoClient = new MongoClient(globalvalue.mongosapi, globalvalue.mongosPort);
//            MongoClient mongoClient = new MongoClient("10.60.38.173", 27020);
            //连接到数据库
            MongoDatabase mongoDatabase = mongoClient.getDatabase("knowledgegraph");
            MongoCollection<Document> collection = mongoDatabase.getCollection("correlation");
            Map<String, Object> data = new HashMap<>();
            data.put("sid",kpi);
            data.put("tid",event);
            data.put("value",co);
            //插入文档
            Document document = new Document(data);
            collection.insertOne(document);
            mongoClient.close();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean saveOriginalWorkflowInfo2Mongo(JSONArray data, String stateId){
        try {
            //连接到mongodb服务
            MongoClient mongoClient = new MongoClient(globalvalue.mongosapi, globalvalue.mongosPort);
            //连接到数据库
            MongoDatabase mongoDatabase = mongoClient.getDatabase("knowledgegraph");
            MongoCollection<Document> collection = mongoDatabase.getCollection("originalWorkflowInfo");
            Map info = new HashMap();
            info.put("data", data);
            info.put("stateId", stateId);
            //插入文档
            Document document = new Document(info);
            collection.insertOne(document);
            System.out.println("文档插入成功");
            mongoClient.close();
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static JSONArray getOriginalWorkflowInfoByStateId(String stateId){
        JSONArray jsonArray = null;
        try {
            MongoClient mongoClient = new MongoClient(globalvalue.mongosapi, globalvalue.mongosPort);//连接到数据库
            MongoDatabase mongoDatabase = mongoClient.getDatabase("knowledgegraph");
            MongoCollection<Document> collection = mongoDatabase.getCollection("originalWorkflowInfo");
            //多条件查询
            FindIterable<Document> findIterable = collection.find(new BasicDBObject("stateId", stateId));
            MongoCursor<Document> mongoCursor = findIterable.iterator();
            if (mongoCursor.hasNext()){
                Document d = mongoCursor.next();
                jsonArray = JSONObject.parseObject(d.toJson()).getJSONArray("data");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return jsonArray;
    }

    public static boolean saveCompareInfo2Mongo(JSONArray data, String stateId){
        try {
            //连接到mongodb服务
            MongoClient mongoClient = new MongoClient(globalvalue.mongosapi, globalvalue.mongosPort);
            //连接到数据库
            MongoDatabase mongoDatabase = mongoClient.getDatabase("knowledgegraph");
            MongoCollection<Document> collection = mongoDatabase.getCollection("compareInfo");
            Map info = new HashMap();
            info.put("data", data);
            info.put("stateId", stateId);
            //插入文档
            Document document = new Document(info);
            collection.insertOne(document);
            System.out.println("文档插入成功");
            mongoClient.close();
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static JSONArray getCompareInfoByStateId(String stateId){
        JSONArray jsonArray = null;
        try {
            MongoClient mongoClient = new MongoClient(globalvalue.mongosapi, globalvalue.mongosPort);//连接到数据库
            MongoDatabase mongoDatabase = mongoClient.getDatabase("knowledgegraph");
            MongoCollection<Document> collection = mongoDatabase.getCollection("compareInfo");
            //多条件查询
            FindIterable<Document> findIterable = collection.find(new BasicDBObject("stateId", stateId));
            MongoCursor<Document> mongoCursor = findIterable.iterator();
            if (mongoCursor.hasNext()){
                Document d = mongoCursor.next();
                jsonArray = JSONObject.parseObject(d.toJson()).getJSONArray("data");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return jsonArray;
    }

    public static List<String> getAllStateId(){
        List<String> ls = new ArrayList<>();
        try {
            MongoClient mongoClient = new MongoClient(globalvalue.mongosapi, globalvalue.mongosPort);//连接到数据库
            MongoDatabase mongoDatabase = mongoClient.getDatabase("knowledgegraph");
            MongoCollection<Document> collection = mongoDatabase.getCollection("originalWorkflowInfo");
            FindIterable<Document> findIterable = collection.find();
            MongoCursor<Document> mongoCursor = findIterable.iterator();
            while (mongoCursor.hasNext()){
                Document d=mongoCursor.next();
                ls.add(JSONObject.parseObject(d.toJson()).getString("stateId"));
            }
            mongoClient.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return ls;

    }


    public static void main(String[] args) throws Exception{
        Date day=new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = df.format(day);
        saveOriginalWorkflowInfo2Mongo(getJsonInfo("/Users/jiang/Library/Containers/com.tencent.xinWeChat/Data/Library/Application Support/com.tencent.xinWeChat/2.0b4.0.9/4b6962f76687c3983d1dc38c8ecd88b9/Message/MessageTemp/82179e106359a79246b90256b693dac8/File/data(5).json"), time);
//        System.out.println(getOriginalWorkflowInfoByStateId(time));
        System.out.println(getAllStateId());
    }
}
