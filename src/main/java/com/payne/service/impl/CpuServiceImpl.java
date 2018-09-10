package com.payne.service.impl;

import com.payne.service.CpuService;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CpuServiceImpl implements CpuService {

    @Value("${spring.influx.url}")
    private String infulxUrl;

    public void insertData(String region){
        //写入数据
        InfluxDB influxDB = InfluxDBFactory.connect(infulxUrl);
        influxDB.setRetentionPolicy("2_hours");//数据保存测试，by default 用默认策略
        influxDB.setDatabase("my_test_influx");
        Point.Builder builder = Point.measurement("cpu");
        Long currentTime = System.currentTimeMillis();
        builder.time(currentTime, TimeUnit.MILLISECONDS);
        builder.addField("idle", 90.0);
        builder.addField("hostname", "server1");
        builder.tag("region", region);
        builder.addField("happydevop", false);
        Point point = builder.build();
        influxDB.write(point);
    }
}
