package com.goudan.wang.repository;

import com.goudan.wang.entity.MobileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Danny on 2018/2/13.
 */
public interface IMobileRepository extends JpaRepository<MobileEntity, Integer> {
}
