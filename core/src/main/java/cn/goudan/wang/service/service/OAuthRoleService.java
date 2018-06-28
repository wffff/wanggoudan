package cn.goudan.wang.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MOMO-PC on 2017/5/10.
 */
@Service
public class OAuthRoleService {

    private static final String GET_ROLES = "SELECT id, name, description FROM oauth_role ORDER BY id ASC";
    private static final String GET_COUNT = "SELECT COUNT(*) FROM oauth_role";
    private static final String GET_AUTHORITY_BY_UID_RID = "SELECT rid FROM oauth_authority WHERE UID=?";
    private static final String ADD_CLIENT_USER_AUTHORITY = "INSERT INTO oauth_authority (uid,rid) VALUES (?,?)";
    private static final String REMOVE_AUTHORITY = "DELETE FROM oauth_authority WHERE uid=? AND rid=?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<OAuthRole> getRoles() {
        return jdbcTemplate.query(GET_ROLES, (rs, rowNum) -> new OAuthRole(rs.getLong("id"), rs.getString("name"), rs.getString("description")));
    }
    public void addAuthority(OAuthUserDetails userDetails, String role) {
        Assert.notNull(userDetails, "no user id");
        Assert.notNull(role, "no role id");
        String[] strings=role.split(",");
        //传进来的角色
        List<String> stringList=new ArrayList<>();
        for (int i=0;i<strings.length;i++) {
            stringList.add(strings[i]);
        }
        //角色总数
        Integer count= jdbcTemplate.queryForObject(GET_COUNT,Integer.class);
        //已有角色
        List<Long> rids =  jdbcTemplate.queryForList(GET_AUTHORITY_BY_UID_RID, Long.class,userDetails.getId());
        for(Long i = 1l; i<=count; i++){
            //如果这个角色已有而且传入已有,什么都不做
            if(stringList.contains(String.valueOf(i))&&rids.contains(i)){
            //如果这个角色被传入,而并不是已有,就增加
            }else if(stringList.contains(String.valueOf(i))){
                Object[] fields = getFieldsForAuthority(userDetails, String.valueOf(i));
                addAuthorityCycle(fields);
            //如果这个角色没被传入,而且是已经有了的情况,就删除
            }else if (rids.contains(i)){
               jdbcTemplate.update(REMOVE_AUTHORITY, userDetails.getId(),i);
            }
        }

    }
    private void addAuthorityCycle(Object[] fields){
        try {
            jdbcTemplate.update(ADD_CLIENT_USER_AUTHORITY, fields);
        } catch (Exception e) {
            addAuthorityCycle(fields);
        }
    }

    private Object[] getFieldsForAuthority(OAuthUserDetails userDetails, String role) {
        return new Object[]{
                userDetails.getId()!= null ? userDetails.getId() : null,
                Long.valueOf(role)
        };
    }

    public List<Long> getAuthorityById(Long l) {
        List<Long> rids =  jdbcTemplate.queryForList(GET_AUTHORITY_BY_UID_RID, Long.class,l);
        return rids;
    }
}
