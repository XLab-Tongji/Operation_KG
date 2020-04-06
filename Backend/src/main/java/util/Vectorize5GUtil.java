package util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Vectorize5GUtil {
    public static void readFile(String txtName, String csvName) {
        int i = 0,j=0;
        ArrayList<Object>transactions=new ArrayList<>();
        ArrayList<Object>patterns=new ArrayList();
        Integer checkNum=-1;
        try {
            //CSV部分
            BufferedReader csvReader = new BufferedReader(new FileReader(csvName));//换成你的文件名
            csvReader.readLine();
            String csvLine,txtLine;
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
            }
            //txt部分
            BufferedReader br = new BufferedReader(new FileReader(txtName));
            while((txtLine = br.readLine()) != null){
                Map<String,Object>transaction=new HashMap<>();
                //name
                transaction.put("","transaction"+(j+1));
                //content
                ArrayList<Object>content=new ArrayList<>();
                String items[] = txtLine.split(" ");
                for(String item:items){
                    Integer num=Integer.parseInt(item);
                    if(num>=1000){
                        checkNum=num/1000-1;
                        if(!content.contains(patterns.get(checkNum))) {
                            content.add(patterns.get(checkNum));
                        }
                        checkNum=num%1000-1;

                    }
                    else if(num<=0){
                        checkNum=(num*-1)/1000-1;
                    }
                    else{
                        checkNum=num-1;
                    }
                    if(!content.contains(patterns.get(checkNum))&&checkNum!=-1) {
                        content.add(patterns.get(checkNum));
                    }
            }
                transaction.put("content",content);
                transaction.put("children",j+1);
                transactions.add(transaction);
                j++;
        }
            Map<String,Object>transaction= (Map<String, Object>) transactions.get(transactions.size()-1);
            transaction.remove("children");
    } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.print("ok");
    }
    public static void main(String[] args){
        readFile("E:\\git\\new2vectorize.txt","E:\\git\\5G.log_templates.csv");
    }
}
