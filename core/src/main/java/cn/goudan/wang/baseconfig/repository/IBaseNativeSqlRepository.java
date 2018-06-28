package cn.goudan.wang.baseconfig.repository;

import java.util.List;

/**
 * Created by Adam.yao on 2017/12/10.
 */

public interface IBaseNativeSqlRepository<T> {

    List<T> listByNativeSql(String sql);

    List<T> listByNativeSql(String sql, Class<T> type);

    /**
     * 递归树查询
     * @param id    当前id
     * @param up    是否向上检索
     * @param self  是否包含自已
     * @param recursive     是否多层递归
     * @param originClass   源类型
     * @param targetClass   目标类型
     * @param columns       目标字段列表
     * @return
     */
    @SuppressWarnings("Duplicates")
    List<T> listTreeRecursive(Integer id, boolean up, boolean self, boolean recursive, Class<T> originClass, Class<T> targetClass, String... columns);
}
