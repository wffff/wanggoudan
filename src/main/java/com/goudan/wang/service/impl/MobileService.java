package com.goudan.wang.service.impl;

import com.goudan.wang.entity.MobileEntity;
import com.goudan.wang.repository.IMobileRepository;
import com.goudan.wang.service.IMobileService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Danny on 2018/2/13.
 */
@Service
public class MobileService implements IMobileService {
    @Resource
    private IMobileRepository iMobileRepository;

    @Override
    public List<MobileEntity> findAll() {
        return iMobileRepository.findAll();
    }

    @Override
    public List<MobileEntity> create(List<String> number) {
        MobileEntity mobileEntity;
        List<MobileEntity> list=new ArrayList<>();
        for (String number1:number){
            mobileEntity=new MobileEntity();
            mobileEntity.setNumber(number1);
            list.add(mobileEntity);
        }
        return iMobileRepository.saveAll(list);
    }

    @Override
    public boolean del(List<Integer> id) {
        List<MobileEntity> list=iMobileRepository.findAllById(id);
        iMobileRepository.deleteAll(list);
        return true;
    }
}
