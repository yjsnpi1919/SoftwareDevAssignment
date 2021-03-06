package com.example.demo.controller;

import com.example.demo.entity.InnerUser;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.example.demo.utils.Date;
import com.example.demo.utils.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired(required = false)
    public UserService userService;

    @PostMapping("/login")
    public String Login(HttpServletRequest request,String code) {
        String session;
        System.out.println("start");
        User user = userService.login(code);
        System.out.println("login");
        if(user.getOpenid()!=null){
            System.out.println("1");
            userService.checkUser(user.getOpenid());
            System.out.println("2");
            session = Session.addUser(user.getOpenid());
            System.out.println("3");
            //检测openid是否存在
            //首次登录则向数据库中写入openid
            //生成session并返回
            return session;
        }
        else{
            return null;
        }
    }
    @PostMapping("/test")
    public InnerUser test(HttpServletRequest request){
        InnerUser user = userService.selectAimUser("001");
        user.setUserDateString(Date.dateFormat.format(user.getUserDate()));
        return user;
    }
    @PostMapping("/getUserInfo")
    public InnerUser getUserInfo(HttpServletRequest request,String openid){
        InnerUser user = userService.selectAimUser(openid);
        if(user!=null)
            user.setUserDateString(Date.dateFormat.format(user.getUserDate()));
        return user;
    }
    @PostMapping("/resetData")
    public InnerUser resetData(HttpServletRequest request,InnerUser user){
        userService.updateUserInfo(user);
        return user;
    }

}
