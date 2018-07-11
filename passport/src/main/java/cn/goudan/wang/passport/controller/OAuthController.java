package cn.goudan.wang.passport.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * Created by MOMO-PC on 2017/4/11.
 */
@Controller
@SessionAttributes("authorizationRequest")
public class OAuthController {
    /**
     * Oauth认证控制层
     *
     */
    /**
     * 认证登录
     */
    @RequestMapping(value = "/oauth/login", method = RequestMethod.GET)
    public ModelAndView login(ModelMap model) {
        return new ModelAndView("oauth/login", model);
    }

    /**
     * 认证失败返回错误信息
     * @return
     */
    @RequestMapping("/oauth/error")
    public String handleError(Map<String, Object> model) throws Exception {
        model.put("message", "There was a problem with the OAuth2 protocol");
        return "oauth/error";
    }
}
