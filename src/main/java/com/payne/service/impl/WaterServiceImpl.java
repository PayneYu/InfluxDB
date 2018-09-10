package com.payne.service.impl;

import com.payne.service.WaterService;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
@Service
public class WaterServiceImpl implements WaterService {

    @Value("${spring.influx.url}")
    private String infulxUrl;

    public void insertFeet(){
        //写入数据
        InfluxDB influxDB = InfluxDBFactory.connect(infulxUrl);
        influxDB.setDatabase("NOAA_water_database");
        Point.Builder builder = Point.measurement("h2o_feet");
        Long currentTime = System.currentTimeMillis();
        builder.time(currentTime, TimeUnit.MILLISECONDS);
        builder.tag("location", "santa_monica");
        builder.tag("level description", "below 3 feet");
        builder.addField("water_level", 8.02);
        Point point = builder.build();
        influxDB.write(point);

    }
}
