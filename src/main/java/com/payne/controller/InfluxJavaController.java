package com.payne.controller;

import com.payne.entity.H2oFeet;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/database")
public class InfluxJavaController {
	
	@Value("${spring.influx.url}")
	private String infulxUrl;
	
	@GetMapping("/query/{table}")
	public List query(@PathVariable String table){
		InfluxDB influxDB = InfluxDBFactory.connect(infulxUrl);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
		Date startDate = new Date();
		System.out.println("start time:"+format.format(startDate));
		Query query = new Query("SELECT * FROM "+table, "NOAA_water_database");
		List list = queryDB(influxDB,table);
		Date endDate = new Date();
		System.out.println("end time:"+format.format(endDate));
		System.out.println("total time:"+(endDate.getTime()-startDate.getTime())+"ms");
		return list;
    }

    private List queryDB(InfluxDB influxDB,String tableName){
		Query query = new Query("SELECT * FROM "+tableName, "NOAA_water_database");
		QueryResult result = influxDB.query(query);
		return result.getResults();
	}

	private List<H2oFeet> oudataIntoModel(List<String> columns, List<List<Object>> values){
		List<H2oFeet> list = new ArrayList<H2oFeet>();
		for (List<Object> objList : values) {
			H2oFeet ou = new H2oFeet();
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

}
