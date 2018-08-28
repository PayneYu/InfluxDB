package com.payne.controller;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/database")
public class DatabaseController {
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${spring.influx.url}")
	private String infulxUrl;
	
	@GetMapping("/add")
	public Object createDatabase(){
		InfluxDB influxDB = InfluxDBFactory.connect(infulxUrl);
        String url=infulxUrl + "/query";
        MultiValueMap<String,String> postParameter=new LinkedMultiValueMap<String,String>();
        postParameter.add("q","CREATE DATABASE mydb1");
        Object result = restTemplate.postForObject(url,postParameter,Object.class);
        return result;
    }

}
