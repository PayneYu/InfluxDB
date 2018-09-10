package com.payne.timer;

import com.payne.service.WaterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScheduledService {

    private static Logger LOGGER = LoggerFactory.getLogger(ScheduledService.class);

    @Autowired
    private WaterService waterService;

    //@Scheduled(cron = "0/5 * * * * *")
    public void scheduled(){
        LOGGER.debug("Start =====>>>>>使用cron  {}",System.currentTimeMillis());
        waterService.insertFeet();
        LOGGER.debug("End =====>>>>>使用cron  {}",System.currentTimeMillis());
    }
}
