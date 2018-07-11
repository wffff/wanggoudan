package cn.goudan.wang.core.repository;

import cn.goudan.wang.core.entity.MobileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Danny on 2018/2/13.
 */
public interface IMobileRepository extends JpaRepository<MobileEntity, Integer> {
}
