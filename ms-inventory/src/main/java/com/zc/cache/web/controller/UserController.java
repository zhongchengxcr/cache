package com.zc.cache.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.zc.cache.dao.db.entity.User;
import com.zc.cache.dao.db.mapper.UserMapper;
import com.zc.cache.dao.redis.mapper.RedisDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 说明 . <br>
 * <p>
 * <p>
 * Copyright: Copyright (c) 2017/12/24 上午11:01
 * <p>
 * Company: xxx
 * <p>
 *
 * @author zhongcheng_m@yeah.net
 * @version 1.0.0
 */
@RestController
public class UserController {

    @SuppressWarnings("all")
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisDAO redisDAO;

    @GetMapping("/user")
    public User user() {
        return userMapper.selectById(1);
    }

    @GetMapping("/user/redis")
    public User cacheUser() {
        String userJson = redisDAO.get("user");


        redisDAO.set("cached_user_lisi", "{\"name\": \"lisi\", \"age\":28}");

        String userJSON = redisDAO.get("cached_user_lisi");
        JSONObject userJSONObject = JSONObject.parseObject(userJSON);

        User user = new User();
        user.setName(userJSONObject.getString("name"));
        user.setAge(userJSONObject.getInteger("age"));

        return user;
    }


}
