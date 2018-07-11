package cn.goudan.wang.core.baseconfig.repository.impl;

import cn.goudan.wang.core.baseconfig.repository.IBaseNativeSqlRepository;
import cn.goudan.wang.core.baseconfig.utils.RegexUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Adam.yao on 2017/12/10.
 */

@Repository
public class BaseNativeSqlRepository<T> implements IBaseNativeSqlRepository<T> {

    @PersistenceUnit
    private EntityManagerFactory emf;

    @Override
    public List<T> listByNativeSql(String sql) {
        EntityManager em = emf.createEntityManager();
        Query query = em.createNativeQuery(sql);
        List<T> list = query.getResultList();
        em.close();
        return list;
    }

    @Override
    public List<T> listByNativeSql(String sql, Class<T> type) {
        EntityManager em = emf.createEntityManager();
        Query query = em.createNativeQuery(sql, type);
        List<T> list = query.getResultList();
        em.close();
        return list;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public List<T> listTreeRecursive(Integer id, boolean up, boolean self, boolean recursive, Class<T> originClass, Class<T> targetClass, String... columns) {
        String tableName = originClass.getAnnotation(Table.class).name();
        String columnsSelected = "";
        for (String column : columns) {
            if (RegexUtils.notNull(column)) {
                columnsSelected = column + ",";
            }
        }
        columnsSelected = columnsSelected.substring(0, columnsSelected.length() - 1);
        String sql;
        if (recursive) {
            String and;
            String includeSelf = "";
            if (id != null) {
                and = " AND id=" + id;
            } else {
                and = " AND parent_id is null";
            }
            //如果入参是向上递归则改条件为向上
            String forWard = tableName + ".parent_id = c.id";
            if (up) {
                forWard = tableName + ".id = c.parent_id";
            }
            //如果包含自己的id
            if (!self) {
                includeSelf += " where id!=" + id;
            }
            sql = "WITH RECURSIVE c AS ( SELECT * FROM " + tableName + " WHERE del=false " + and +
                    " UNION ALL" +
                    " SELECT " + tableName + ".* FROM " + tableName + ", c WHERE " + forWard +
                    " ) SELECT " + columnsSelected + " FROM c " + includeSelf + " ORDER BY id";
        } else {
            //不递归 取直接子列表
            String and;
            if (id != null) {
                and = " AND parent_id=" + id;
            } else {
                and = " AND parent_id is null";
            }
            sql = "SELECT " + columnsSelected + " FROM " + tableName + " where del=false " + and;
        }

        EntityManager em = emf.createEntityManager();
        Query query;
        //这里如果是Integer不能取对象类型
        if (targetClass == Integer.class) {
            query = em.createNativeQuery(sql);
        } else {
            query = em.createNativeQuery(sql, targetClass);
        }
        List<T> list = query.getResultList();
        em.close();
        return list;
    }
}
