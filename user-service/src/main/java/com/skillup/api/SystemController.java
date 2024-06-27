package com.skillup.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

@Slf4j
@RestController
public class SystemController {
    @GetMapping(value = "/system/time")
    public String getTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getDefault());
        log.info("Current timezone is: {} ({})", df.getTimeZone().getID(), df.getTimeZone().getDisplayName());
        String currTime = df.format(System.currentTimeMillis());
        log.info("Current time is {}", currTime);
        return df.format(System.currentTimeMillis());
    }
}