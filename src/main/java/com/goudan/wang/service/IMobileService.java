package com.goudan.wang.service;


import com.goudan.wang.entity.MobileEntity;

import java.util.List;

/**
 * Created by Danny on 2018/2/13.
 */
public interface IMobileService {
    List<MobileEntity> findAll();

    List<MobileEntity> create(List<String> number);

    boolean del(List<Integer> id);
}
