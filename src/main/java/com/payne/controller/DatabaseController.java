package com.payne.controller;

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
        String url=infulxUrl + "/query";
        //write?db=mydb1
        MultiValueMap<String,String> postParameter=new LinkedMultiValueMap<String,String>();
        postParameter.add("q","CREATE DATABASE mydb1");
        Object result = restTemplate.postForObject(url,postParameter,Object.class);
        return result;
    }

	@GetMapping("/query")
	public Object query(){
		String url="HTTP://localhost:8086/query";
		MultiValueMap<String,String> postParameter=new LinkedMultiValueMap<String,String>();
		postParameter.add("db","my_test_influx");
		postParameter.add("q","SELECT * FROM cpu");
		Object result = restTemplate.postForObject(url,postParameter,Object.class);
		return result;
}

}
