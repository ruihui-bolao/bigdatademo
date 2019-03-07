package com.hui.spark.firstdemo;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import scala.Tuple2;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 2018/11/14 15:50
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   spark 读取统计数据
 */
public class SparkReader implements Serializable {

    public static JavaSparkContext sparkContext;
    public long yuzhi = 5;
    public String pre = "C:\\Users\\Administrator\\Desktop\\华侨\\结果\\";


    public SparkReader() {
        SparkConf conf = new SparkConf();
        conf.setMaster("local").setAppName("SparkMapTest");
        sparkContext = new JavaSparkContext(conf);
    }

    /**
     * 用来统计平均停留时间
     */
    public void countAverageTime(final String inputPath) throws Exception {
        int i = inputPath.lastIndexOf("\\");
        String fileName = inputPath.substring(i + 1, inputPath.length());
        String day = fileName.substring(9, fileName.length() - 4);
        String outPath = pre + "avergeTime.csv";
        JavaRDD<String> javaRDD = sparkContext.textFile(inputPath);
        JavaPairRDD<String, Iterable<String>> group = javaRDD.mapPartitionsToPair(new PairFlatMapFunction<Iterator<String>, String, String>() {
            @Override
            public Iterator<Tuple2<String, String>> call(Iterator<String> iterator) throws Exception {
                LinkedList<Tuple2<String, String>> list = new LinkedList<Tuple2<String, String>>();
                while (iterator.hasNext()) {
                    final String next = iterator.next();
                    String[] split = next.split(",");
                    String phomeMac = split[0];
                    String wifiMac = split[1];
                    String time = split[2];
                    list.add(new Tuple2<String, String>(phomeMac + "_" + wifiMac, time));
                }
                return list.iterator();
            }
        }).groupByKey();

        Map<String, Iterable<Long>> collectAsMap = group.mapPartitionsToPair(new PairFlatMapFunction<Iterator<Tuple2<String, Iterable<String>>>, String, Long>() {
            @Override
            public Iterator<Tuple2<String, Long>> call(Iterator<Tuple2<String, Iterable<String>>> tuple2Iterator) throws Exception {
                LinkedList<Tuple2<String, Long>> list = new LinkedList<Tuple2<String, Long>>();
                while (tuple2Iterator.hasNext()) {
                    Tuple2<String, Iterable<String>> next = tuple2Iterator.next();
                    String key = next._1();
                    Iterable<String> iterable = next._2();
                    List timeList = IteratorUtils.toList(iterable.iterator());
                    if (timeList.size() <= 1) {
                        continue;
                    }
                    long timeSum = countTimeSum(timeList);
                    list.add(new Tuple2<String, Long>(key, timeSum));
                }
                return list.iterator();
            }
        }).mapPartitionsToPair(new PairFlatMapFunction<Iterator<Tuple2<String, Long>>, String, Long>() {
            @Override
            public Iterator<Tuple2<String, Long>> call(Iterator<Tuple2<String, Long>> tuple2Iterator) throws Exception {
                LinkedList<Tuple2<String, Long>> list = new LinkedList<Tuple2<String, Long>>();
                while (tuple2Iterator.hasNext()) {
                    Tuple2<String, Long> next = tuple2Iterator.next();
                    String s = next._1();
                    String[] split = s.split("_");
                    String wifiMac = split[1];
                    list.add(new Tuple2<String, Long>(wifiMac, next._2()));
                }
                return list.iterator();
            }
        }).groupByKey().collectAsMap();

        HashMap<String, Long> resMap = new HashMap<String, Long>();
        for (Map.Entry<String, Iterable<Long>> entry : collectAsMap.entrySet()) {
            Iterator<Long> iterator = entry.getValue().iterator();
            List list = IteratorUtils.toList(iterator);
            long sum = 0;
            for (Object o : list) {
                sum += (Long) o;
            }
            long averge = sum / list.size();
            resMap.put(entry.getKey(), averge);
        }

        for (Map.Entry<String, Long> entry : resMap.entrySet()) {
            String wifiMac = entry.getKey();
            Long time = entry.getValue();
            String tempStr = day + "," + wifiMac + "," + time + "\n";
            FileUtils.writeStringToFile(new File(outPath), tempStr, "GBK", true);
        }
        System.out.println(resMap);
    }

    /**
     * 用来统计总的停留时间
     *
     * @param list
     * @return
     */
    public long countTimeSum(List list) throws Exception {
        long sumTime = 0;
        String startTIme = "";
        String endTime = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                startTIme = String.valueOf(list.get(i));
            } else {
                String start = String.valueOf(list.get(i - 1));
                String end = String.valueOf(list.get(i));
                Date startDate = sdf.parse(start);
                Date endDate = sdf.parse(end);
                long l = endDate.getTime() - startDate.getTime();
                if (yuzhi * 60 * 1000 > l) {
                    endTime = String.valueOf(list.get(i));
                } else {
                    endTime = String.valueOf(list.get(i - 1));
                    long error = sdf.parse(endTime).getTime() - sdf.parse(startTIme).getTime();
                    sumTime += error;
                    startTIme = String.valueOf(list.get(i));
                }

                if (i == list.size() - 1) {
                    endTime = String.valueOf(list.get(i));
                    sumTime += sdf.parse(endTime).getTime() - sdf.parse(startTIme).getTime();
                }
            }
        }
        return sumTime;
    }


    /**
     * 用来计算游客趋势
     *
     * @param inputPath
     */
    public void countTrend(String inputPath) {
        int i = inputPath.lastIndexOf("\\");
        String fileName = inputPath.substring(i + 1, inputPath.length());
        String day = fileName.substring(9, fileName.length() - 4);
        String outPath = pre + "Trend.csv";
        JavaRDD<String> javaRDD = sparkContext.textFile(inputPath);
        JavaPairRDD<String, Iterable<String>> group = javaRDD.mapPartitionsToPair(new PairFlatMapFunction<Iterator<String>, String, String>() {
            @Override
            public Iterator<Tuple2<String, String>> call(Iterator<String> iterator) throws Exception {
                LinkedList<Tuple2<String, String>> list = new LinkedList<Tuple2<String, String>>();
                while (iterator.hasNext()) {
                    String next = iterator.next();
                    String[] split = next.split(",");
                    String phomeMac = split[0];
                    String wifiMac = split[1];
                    String time = split[2];
                    list.add(new Tuple2<String, String>(phomeMac, wifiMac + "_" + time));
                }
                return list.iterator();
            }
        }).groupByKey();

/*        Map<String, Iterable<String>> map = group.collectAsMap();
        String tempPath = "C:\\Users\\Administrator\\Desktop\\华侨\\结果\\temp.txt";
        for (Map.Entry<String, Iterable<String>> entry : map.entrySet()) {
            String phoneMac = entry.getKey();
            String string = entry.getValue().toString();
            String tempStr = phoneMac + "----" + string;
            try {
                FileUtils.writeStringToFile(new File(tempPath), tempStr + "\n", "UTF-8",true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(map.size());*/

        Map<String, Iterable<String>> collect = group.flatMapToPair(new PairFlatMapFunction<Tuple2<String, Iterable<String>>, String, String>() {
            @Override
            public Iterator<Tuple2<String, String>> call(Tuple2<String, Iterable<String>> stringIterableTuple2) throws Exception {
                LinkedList<Tuple2<String, String>> list = new LinkedList<Tuple2<String, String>>();
                Iterator<String> iterator = stringIterableTuple2._2().iterator();
                List trendList = IteratorUtils.toList(iterator);
                List<String> threndCreate = threndCreate(trendList);
                if (!threndCreate.isEmpty()) {
                    for (String s : threndCreate) {
                        list.add(new Tuple2<String, String>(s, stringIterableTuple2._1()));
                    }
                }
                return list.iterator();
            }
        }).groupByKey().collectAsMap();

        for (Map.Entry<String, Iterable<String>> entry : collect.entrySet()) {
            String key = entry.getKey();
            String[] split = key.split("_");
            String fromAdds = split[0];
            String toAdds = split[1];
            Iterator<String> iterator = entry.getValue().iterator();
            List list = IteratorUtils.toList(iterator);
            int size = list.size();
            String tempStr = day + "," + fromAdds + "," + toAdds + "," + size;
            try {
                FileUtils.writeStringToFile(new File(outPath), tempStr + "\n", "GBK", true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 用来分析趋势
     *
     * @param list
     * @return
     */
    public List<String> threndCreate(List list) throws Exception {
        TreeMap<Long, String> treeMap = new TreeMap<Long, String>(new Comparator<Long>() {
            @Override
            public int compare(Long o1, Long o2) {
                return o1.compareTo(o2);
            }
        });

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Object o : list) {
            String value = String.valueOf(o);
            String[] split = value.split("_");
            String wifiMac = split[0];
            String dataTime = split[1];
            Date date = sdf.parse(dataTime);
            long time = date.getTime();
            treeMap.put(time, wifiMac);
        }

        Object[] objects = treeMap.values().toArray();
        LinkedList<String> resList = new LinkedList<String>();
        for (int i = 0; i < objects.length; i++) {
            if (i == 0) {
                continue;
            }
            String startValue = String.valueOf(objects[i - 1]);
            String endValue = String.valueOf(objects[i]);
            if (!StringUtils.equalsIgnoreCase(startValue, endValue)) {
                resList.add(startValue + "_" + endValue);
            }
        }
        return resList;
    }


    /**
     * 用来统计时间段的旅游情况
     */
    public void countTimeRange(String inputPath) {
        int i = inputPath.lastIndexOf("\\");
        String fileName = inputPath.substring(i + 1, inputPath.length());
        String day = fileName.substring(9, fileName.length() - 4);
        String outPath = pre + "TimeRange.csv";
        JavaRDD<String> javaRDD = sparkContext.textFile(inputPath);
        JavaPairRDD<String, Iterable<String>> group = javaRDD.mapPartitionsToPair(new PairFlatMapFunction<Iterator<String>, String, String>() {
            @Override
            public Iterator<Tuple2<String, String>> call(Iterator<String> iterator) throws Exception {
                LinkedList<Tuple2<String, String>> list = new LinkedList<Tuple2<String, String>>();
                while (iterator.hasNext()) {
                    String next = iterator.next();
                    String[] split = next.split(",");
                    String phomeMac = split[0];
                    String wifiMac = split[1];
                    String time = split[2];
                    String timeRange = findTimeRange(time);
                    list.add(new Tuple2<String, String>(wifiMac + "_" + timeRange, phomeMac));
                }
                return list.iterator();
            }
        }).groupByKey();

        Map<String, Iterable<String>> map = group.collectAsMap();
        for (Map.Entry<String, Iterable<String>> entry : map.entrySet()) {
            String key = entry.getKey();
            String[] split = key.split("_");
            String wifiMac = split[0];
            String timeRange = split[1];
            Iterator<String> iterator = entry.getValue().iterator();
            List list = IteratorUtils.toList(iterator);
            int size = list.size();
            String temp = day + "," + wifiMac + "," + timeRange + "," + size;
            try {
                FileUtils.writeStringToFile(new File(outPath), temp + "\n", "GBK", true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 用来查找时间段
     */
    public String findTimeRange(String time) {
        String[] split = time.split(" ");
        String dayTime = split[1];
        String[] split1 = dayTime.split(":");
        String hour = split1[0];
        int start = Integer.parseInt(hour);
        int end = start + 1;
        String startRes = "";
        String endRes = "";
        if (start < 10) {
            startRes = "0" + String.valueOf(start);
        } else {
            startRes = String.valueOf(start);
        }

        if (end < 10) {
            endRes = "0" + String.valueOf(end);
        } else {
            endRes = String.valueOf(end);
        }
        String res = startRes + ":00" + "~" + endRes + ":00";
        return res;
    }


    /**
     * 用来计算总的游客数
     *
     * @param inputPath
     */
    public void countTotalNum(String inputPath) {
        int i = inputPath.lastIndexOf("\\");
        String fileName = inputPath.substring(i + 1, inputPath.length());
        String day = fileName.substring(9, fileName.length() - 4);
        String outPath = pre + "totalNum.csv";
        JavaRDD<String> javaRDD = sparkContext.textFile(inputPath);
        Map<String, Iterable<String>> map = javaRDD.mapPartitionsToPair(new PairFlatMapFunction<Iterator<String>, String, String>() {
            @Override
            public Iterator<Tuple2<String, String>> call(Iterator<String> iterator) throws Exception {
                LinkedList<Tuple2<String, String>> list = new LinkedList<Tuple2<String, String>>();
                while (iterator.hasNext()) {
                    String next = iterator.next();
                    String[] split = next.split(",");
                    String phomeMac = split[0];
                    String wifiMac = split[1];
                    list.add(new Tuple2<String, String>(wifiMac, phomeMac));
                }
                return list.iterator();
            }
        }).groupByKey().collectAsMap();
        for (Map.Entry<String, Iterable<String>> entry : map.entrySet()) {
            String wifiMac = entry.getKey();
            Iterable<String> value = entry.getValue();
            int size = IteratorUtils.toList(value.iterator()).size();
            String tempStr = day + "," + wifiMac + "," + size + "\n";
            try {
                FileUtils.writeStringToFile(new File(outPath), tempStr, "GBK", true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) throws Exception {
        SparkReader sparkReader = new SparkReader();
        String inputPath = "C:\\Users\\Administrator\\Desktop\\华侨\\input";
        File file = new File(inputPath);
        File[] files = file.listFiles();
        for (File file1 : files) {
            String absolutePath = file1.getAbsolutePath();
//            sparkReader.countAverageTime(absolutePath);
//            sparkReader.countTrend(absolutePath);
            sparkReader.countTimeRange(absolutePath);
//            sparkReader.countTotalNum(absolutePath);
        }

//        String inputPath = "C:\\Users\\Administrator\\Desktop\\华侨\\结果\\test.txt";
//        SparkReader sparkReader = new SparkReader();
//        List<String> lines = FileUtils.readLines(new File(inputPath), "UTF-8");
//        for (String line : lines) {
//            String[] split = line.split(",");
//            List<String> list = Arrays.asList(split);
//            List<String> strings = sparkReader.threndCreate(list);
//            System.out.println(strings);
//        }
    }

}
