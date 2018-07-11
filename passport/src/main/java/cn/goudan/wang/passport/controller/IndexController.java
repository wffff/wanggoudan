package cn.goudan.wang.passport.controller;

import cn.goudan.wang.passport.securityservice.OAuthUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by MOMO-PC on 2017/3/14.
 */
@Controller
public class IndexController {
    /**
     * 检索信息层用来完成注册修改密码登录保存个人信息的
     */
    @Autowired
    private OAuthUserDetailsService userDetailsService;
    /**
     *  进入首页
     */
    @RequestMapping(value = {"", "/"})
    public String index() {
        return "index";
    }
    /**
     * 用户登录
     */
    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String login() {
        return "login";
    }
    /**
     * 用户注册
     * @return
     */
    @RequestMapping(value = "register", method = RequestMethod.GET)
    public String register() {
        return "register";
    }
    /**
     * 忘记密码
     * @return
     */
    @RequestMapping(value = "forget", method = RequestMethod.GET)
    public String forget() {
        return "forget";
    }
    /**
     *
     * @param principal
     * @return
     */
    @ResponseBody
    @RequestMapping("me")
    public UserDetails me(Principal principal) {
        return userDetailsService.findUserByUsername(principal.getName());
    }
    /**
     * 密码提示信息获取和认证
     * 提示信息作为文本保存于map集合中，通过键值对的方法查找
     */
    @ResponseBody
    @RequestMapping("test")
    public Map<String, String> test() {
        Map<String, String> map = new HashMap<>();
        map.put("test1", "hi,test1");
        map.put("test2", "hi,test2");
        return map;
    }
    @ResponseBody
    @RequestMapping("test2")
    public Map<String, String> test2() {
        Map<String, String> map = new HashMap<>();
        map.put("test1", "hi,test1");
        map.put("test2", "hi,test2");
        return map;
    }
}
