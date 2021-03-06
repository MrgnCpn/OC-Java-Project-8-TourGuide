package com.tripmaster.gpsutil;

import com.jsoniter.output.JsonStream;
import gpsUtil.GpsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class GpsUtilController {
    @Autowired
    private GpsUtil gpsUtil;

    @GetMapping("/getUserLocation")
    public String getUserLocation(@RequestParam(required = true) String userId) {
        if (!StringUtils.isEmpty(userId)) {
            return JsonStream.serialize(gpsUtil.getUserLocation(UUID.fromString(userId)));
        }
        return null;
    }

    @GetMapping("/getAttractions")
    public String getAttractions() {
        return JsonStream.serialize(gpsUtil.getAttractions());
    }
}
