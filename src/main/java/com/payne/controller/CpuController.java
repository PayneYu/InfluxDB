package com.payne.controller;

import com.payne.entity.Cpu;
import com.payne.service.CpuService;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;
import org.influxdb.dto.BoundParameterQuery;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBResultMapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/cpu")
public class CpuController {

    @Value("${spring.influx.url}")
    private String infulxUrl;

    @Autowired
    private CpuService cpuService;

    @GetMapping("/count")
    public Object count() {
        InfluxDB influxDB = InfluxDBFactory.connect(infulxUrl);
        Query query = new Query("SELECT MEAN(*) FROM cpu", "my_test_influx");
        QueryResult queryResult = influxDB.query(query);
        List<QueryResult.Result> results = queryResult.getResults();
        for (QueryResult.Result result : results) {
            for (QueryResult.Series ser : result.getSeries()) {
                List<List<Object>> values = ser.getValues();
                return values.get(0).get(1);
            }
        }
        return 0;
    }

    @GetMapping("/query")
    public List<Cpu> query(){
        InfluxDB influxDB = InfluxDBFactory.connect(infulxUrl);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
        Date startDate = new Date();
        System.out.println("start time:"+format.format(startDate));
        Query query = new Query("SELECT * FROM cpu", "my_test_influx");
        QueryResult queryResult = influxDB.query(query);
        //借助InfluxDBResultMapper类进行结果集到model的转换
        InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
        List<Cpu> list = resultMapper.toPOJO(queryResult, Cpu.class);
        return list;
    }
    @GetMapping("/query/{region}")
    public List<Cpu> query(@PathVariable String region){
        return queryByRegion(region);
    }

    @GetMapping("/insert/{region}")
    public List<Cpu> insert(@PathVariable String region){
        InfluxDB influxDB = InfluxDBFactory.connect(infulxUrl);
        cpuService.insertData(region);
        List<Cpu> list = queryByRegion(region);
        return list;
    }

    private List<Cpu> queryByRegion(String region){
        //普通查询
        InfluxDB influxDB = InfluxDBFactory.connect(infulxUrl);
        Query query = BoundParameterQuery.QueryBuilder.newQuery("SELECT * FROM cpu WHERE region = $region")
                .forDatabase("my_test_influx")
                .bind("region", region)
                .create();
        QueryResult queryResult = influxDB.query(query);
        List<Cpu> list = new ArrayList<Cpu>();
        for (QueryResult.Result result : queryResult.getResults()) {
            List<QueryResult.Series> series= result.getSeries();
            for (QueryResult.Series serie : result.getSeries()) {
                List<List<Object>> values = serie.getValues();
                List<String> columns = serie.getColumns();
                list.addAll(getQueryData(columns, values));
            }
        }
        return list;
    }

    private List<Cpu> getQueryData(List<String> columns, List<List<Object>> values){
        List<Cpu> list = new ArrayList<Cpu>();
        for (List<Object> objList : values) {
            Cpu ou = new Cpu();
            BeanWrapperImpl bean = new BeanWrapperImpl(ou);
            for(int i=1;i< objList.size(); i++){
                String propertyName = columns.get(i);//字段名
                Object value = objList.get(i);//相应字段值
                bean.setPropertyValue(propertyName, value);
            }
            list.add(ou);
        }
        return list;
    }
    @GetMapping("/test")
    public void test() throws IllegalAccessException {
        Cpu cpu = new Cpu();
        cpu.setHappydevop(true);
        cpu.setHostname("Payne");
        cpu.setIdle(78.0);
        cpu.setRegion("BEIJINF");
        cpu.setTime(Instant.now().toString());
        test(cpu);
    }

    private void test(Object obj) throws IllegalAccessException {
        InfluxDB influxDB = InfluxDBFactory.connect(infulxUrl);
        influxDB.setDatabase("my_test_influx");
        influxDB.setRetentionPolicy("2_hours");//数据保存测试，by default 用默认策略
        Class clz = obj.getClass();
        String name = ((Measurement)clz.getAnnotation(Measurement.class)).name();
        Point.Builder builder = Point.measurement(name);
        System.out.println("name:"+name);
        Field[] fields = clz.getDeclaredFields();
        Map<String, Object> fieldMaps = new TreeMap();
        for (Field field : fields){
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            Column column =  getColumnAnnotation(field);
            if(column!=null){
                Object value = field.get(obj);
                if(value==null)continue;
                System.out.println("column name:" + column.name() + "=====Tag Field:" + column.tag());
                if ("time".equals(column.name())) {
                    Instant timeT = Instant.parse((String)value);
                    builder.time(timeT.toEpochMilli(), TimeUnit.MILLISECONDS);
                } else {
                    if (column.tag()) {
                        builder.tag(column.name(), String.valueOf(value));
                    } else {
                        fieldMaps.put(column.name(),value);
                       // setFieldValue(builder,column.name(), value);
                    }
                }
           }
        }
        builder.fields(fieldMaps);
        Point point = builder.build();
        influxDB.write(point);
    }
    private void setFieldValue(Point.Builder builder,String name,Object value){
        if (value instanceof String) {
            builder.addField(name,(String)value);
        } else if (value instanceof Long) {
            builder.addField(name,(Long)value);
        } else if (value instanceof Double) {
            builder.addField(name,(double)value);
        }
    }

    private Column getColumnAnnotation(Field field){
        Annotation[] annotations=field.getAnnotations();
        for (Annotation annotation : annotations) {
           if(annotation.annotationType().equals(Column.class)){
               return field.getAnnotation(Column.class);
            }
        }
        return null;
    }



}
