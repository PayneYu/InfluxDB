package com.payne.entity;

import org.apache.commons.lang3.StringUtils;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Measurement(name = "cpu")
public class Cpu {
    @Column(name = "time")
    private String time;
    @Column(name = "hostname", tag = true)
    private String hostname;
    @Column(name = "region", tag = true)
    private String region;
    @Column(name = "idle")
    private Double idle;
    @Column(name = "happydevop")
    private Boolean happydevop;
    @Column(name = "uptimesecs")
    private Long uptimeSecs;

    private String localDateTime;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Double getIdle() {
        return idle;
    }

    public void setIdle(Double idle) {
        this.idle = idle;
    }

    public Boolean getHappydevop() {
        return happydevop;
    }

    public void setHappydevop(Boolean happydevop) {
        this.happydevop = happydevop;
    }

    public Long getUptimeSecs() {
        return uptimeSecs;
    }

    public void setUptimeSecs(Long uptimeSecs) {
        this.uptimeSecs = uptimeSecs;
    }

    public String getLocalDateTime() {
        if(StringUtils.isNotBlank(getTime())){
            LocalDateTime temp = LocalDateTime.ofInstant(Instant.parse(getTime()), ZoneId.systemDefault());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS");
            localDateTime = temp.format(formatter);
        }
        return localDateTime;
    }

    public void setLocalDateTime(String localDateTime) {
        this.localDateTime = localDateTime;
    }
}
