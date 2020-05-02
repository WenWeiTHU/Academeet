package com.mobilecourse.backend;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

import java.io.*;
import java.lang.reflect.Type;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

class TestT {
    int xxx;
    int yyy;

    public TestT() {  }

    public TestT(String ss) {
        JSONObject jsonObject = JSONObject.parseObject(ss);
        this.xxx = jsonObject.getIntValue("xxx");
        this.yyy = jsonObject.getIntValue("yyy");
    }

    public ArrayList<Test1> fun1(int p) {
        System.out.println("note that " + p);
        ArrayList<Test1> rlt = new ArrayList<>();
        rlt.add(new Test1());
        rlt.add(new Test1(5,7));
        return rlt;
    }

    public ArrayList<Test2> fun2(int p) {
        System.out.println("this is " + p);
        ArrayList<Test2> rlt = new ArrayList<>();
        rlt.add(new Test2());
        rlt.add(new Test2("Hello", 2));
        return rlt;
    }
}

public class CommonTest {
    public static void testCSV(String[] args) throws IOException {
        List<String> list = new ArrayList<>();
        list.add("aaa], \"");
        list.add("bbb");
        StringBuffer t = new StringBuffer();
        Character c = 1;
        Character x = 9;
        t.append(c);
        t.append(x);
        StringWriter writer = new StringWriter();
        CsvWriter csvWriter = new CsvWriter(writer, ',');
        try {
            for (String cs : list) {
                csvWriter.write(cs, false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        csvWriter.close();
        String ss = writer.toString();
        StringReader reader = new StringReader(ss);
        CsvReader csvReader = new CsvReader(reader, ',');
        while (csvReader.readRecord()) {
            for (String ts: csvReader.getValues()) {
                System.out.println(ts);
            }
        }
        csvReader.close();
    }

    // timestamp的toString可以直接存进数据库
    public static void testTimestamp(String[] args) {
//        System.out.println(LocalDateTime.now().toString());
        Timestamp now = new Timestamp(10);
        System.out.println(now.toString());
    }

    public static void testReflect() {
        String base = "com.mobilecourse.backend.";
        String[] ss = { base+"Test1", base+"Test2" };
        String[] func = {"fun1", "fun2"};
        Random random = new Random();
        int rand = random.nextInt(2);
        String name = ss[rand];
        Class<?> clazz;
        HashMap<String, String[]> methods = new HashMap<>();
        methods.put(ss[0], new String[]{"x", "y"});
        methods.put(ss[1], new String[]{"name", "id"});
        try {
            // 下面这块是拿到ArrayList之后该做的事情
            clazz = Class.forName(name);
            // 下面这块是的调用userMapper的部分
            TestT obb = new TestT();
            Class<?> claz2 = TestT.class;
            Method method = claz2.getMethod(func[rand], int.class);
            ArrayList<Object> arrayList = (ArrayList<Object>)method.invoke(obb, 3);
            JSONArray arr = new JSONArray();
            for (Object e: arrayList) {
                JSONObject obj = new JSONObject();
                for (String attr: methods.get(ss[rand])) {
                    String met = "get" + Character.toUpperCase(attr.charAt(0)) + attr.substring(1);
                    obj.put(attr, clazz.getMethod(met).invoke(e));
                }
                arr.add(obj);
            }
            System.out.println(arr.toJSONString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        testReflect();
//        LocalDateTime time = LocalDateTime.now();
        String ss = "{\n" +
                "  \"organization\": \"organization\",\n" +
                "  \"introduction\": \"introduction\",\n" +
                "  \"date\": \"2020-05-01\",\n" +
                "  \"chairs\": \"chair1, chair2\", \n" +
                "  \"place\": \"我是神仙卢本伟\",\n" +
                "  \"start_time\": \"2020-05-01 08:00:00\",\n" +
                "  \"end_time\": \"2020-05-01 10:00:00\",\n" +
                "  \"tags\": \"每天修仙像喝水, 为了节目有效果, 身经百挂开的多\"\n" +
                "}";
        JSONObject json = JSONObject.parseObject(ss);
        System.out.println(json.toJSONString());
    }
}