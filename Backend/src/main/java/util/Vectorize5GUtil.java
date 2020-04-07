package util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Vectorize5GUtil {
    public static void readFile(String txtName, String csvName) {
        int i = 0,j=0;
        ArrayList<Object>transactions=new ArrayList<>();
        ArrayList<Object>patterns=new ArrayList();
        Integer checkNum=-1;
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
                ArrayList<Map>content=new ArrayList<>();
                String items[] = txtLine.split(" ");
                HashMap<Integer, Integer> id_count = new HashMap<>();
                int cycle_length = 0;
                int backFillNum = 0;
                int bacFillChildren = 0;
                boolean concurrency = false;
                for(String item:items){
                    Integer num=Integer.parseInt(item);
                    boolean backFill = true;
                    //循环
                    if (num < 0){
                        cycle_length = -num%1000;
                        backFillNum = cycle_length;
                        num = -num/1000;
                        backFill = false;
                    }
                    //并发
                    int[] branch = {0,0};
                    if(num>=1000){
                        branch[0] = num/1000;
                        branch[1] = num%1000;
                    }else {
                        branch[0] = num;
                    }
                    for (int k = 0; k < 2 && branch[k] != 0; k++) {
                        if (id_count.containsKey(branch[k])){
                            id_count.put(branch[k], id_count.get(branch[k])+1);
                        }else {
                            id_count.put(branch[k], 1);
                        }
                        Map<String, Object> pattern = new HashMap<>();
                        pattern.put("id", String.valueOf(branch[k]));
                        pattern.put("id_count", id_count.get(branch[k]));
                        pattern.put("type", "normal");
                        pattern.put("cycle_child", "");
                        pattern.put("attr", new ArrayList<>());
                        //两个连续的循环事件
                        if (backFill){
                            if (backFillNum > 1){
                                content.get(content.size()-1).put("cycle_child", pattern.get("id")+"_"+pattern.get("id_count"));
                                backFillNum--;
                            }else if (backFillNum == 1){
                                content.get(content.size()-1).put("cycle_child",
                                        content.get(content.size()-cycle_length).get("id")+"_"+content.get(content.size()-cycle_length).get("id_count"));
                                backFillNum--;
                            }
                        }
                        pattern.put("children", new ArrayList<>());
                        content.add(pattern);
                        if (content.size()>1 && !(backFill && backFillNum > 0)){
                            int size = 1;
                            if (concurrency){
                                size = 2;
                            }
                            for (int l = 0; l < size; l++) {
                                ((List)content.get(bacFillChildren+l).get("children")).add(content.size()-1);
                            }
                        }
                    }
                    if (branch[1] == 0){
                        concurrency = false;
                        bacFillChildren = content.size() - 1;
                    }else {
                        concurrency = true;
                        bacFillChildren = content.size() - 2;
                    }
                }
                transaction.put("content",content);
                transaction.put("children", new ArrayList<>());
                ((List)transaction.get("children")).add(j);
                transactions.add(transaction);
                j++;
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
        readFile("/Users/jiang/Library/Containers/com.tencent.xinWeChat/Data/Library/Application Support/com.tencent.xinWeChat/2.0b4.0.9/4b6962f76687c3983d1dc38c8ecd88b9/Message/MessageTemp/6050e5d84048da68509c52be5b230703/OpenData/21/695c01947a5fcfe4bccad52374a1facf.txt",
                "wqdq");
    }
}
