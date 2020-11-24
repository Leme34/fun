package com.lsd.fun.modules.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by lsd
 * 2020-04-05 20:25
 */
@AllArgsConstructor
@Data
public class LocationDto {
    //与用户的距离(m)
    private int distance;
    private double lat;
    private double lng;

}
