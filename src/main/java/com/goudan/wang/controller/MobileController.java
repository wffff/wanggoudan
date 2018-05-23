package com.goudan.wang.controller;

import com.goudan.wang.baseconfig.ReturnMessage;
import com.goudan.wang.entity.MobileEntity;
import com.goudan.wang.service.IMobileService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Danny on 2018/2/13.
 */

@Controller
public class MobileController {
    @Resource
    private IMobileService iMobileService;

    @RequestMapping("/")
    public String index(Model model) {
        return "index";
    }

    @RequestMapping("/login")
    public String login(Model model) {
        return "login";
    }

    @RequestMapping("/chouqian")
    public String chouqian(Model model) {
        List<MobileEntity> list=findAll().getData();
        List<String> mobileList=new ArrayList<>();
        for (MobileEntity m:list){
            mobileList.add(m.getNumber());
        }
        model.addAttribute("mobile",mobileList);
        return "chouqian";
    }

    @ResponseBody
    @RequestMapping("mobile/findAll")
    public ReturnMessage<List<MobileEntity>> findAll() {
        List<MobileEntity> list = iMobileService.findAll();
        return ReturnMessage.success(list.size(), list);
    }

    @ResponseBody
    @RequestMapping("mobile/create")
    public ReturnMessage<List<MobileEntity>> create(@RequestParam("number") List<String> number) {
        List<MobileEntity> list = iMobileService.create(number);
        return ReturnMessage.success(list.size(), list);
    }

    @ResponseBody
    @RequestMapping("mobile/del")
    public ReturnMessage del(@RequestParam("id") List<Integer> id) {
        iMobileService.del(id);
        return ReturnMessage.success("删除成功");
    }

}
