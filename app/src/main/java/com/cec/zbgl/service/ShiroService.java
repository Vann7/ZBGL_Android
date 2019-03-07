package com.cec.zbgl.service;

import com.cec.zbgl.model.User;

import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.crypto.hash.DefaultHashService;
import org.apache.shiro.crypto.hash.format.DefaultHashFormatFactory;
import org.apache.shiro.crypto.hash.format.Shiro1CryptFormat;

public class ShiroService {

    /**
     * 通过shiro进行密码加密
     * @param user
     * @return
     */
    public static User shiroPwd(User user) {
        DefaultPasswordService passwordService = new DefaultPasswordService();
        DefaultHashService hashService = new DefaultHashService();
        passwordService.setHashService(hashService);

        Shiro1CryptFormat hashFormat = new Shiro1CryptFormat();
        passwordService.setHashFormat(hashFormat);

        DefaultHashFormatFactory hashFormatFactory = new DefaultHashFormatFactory();
        passwordService.setHashFormatFactory(hashFormatFactory);

        String pwd = passwordService.encryptPassword(user.getPassword());
        user.setPassword(pwd);
        return user;
    }
}
