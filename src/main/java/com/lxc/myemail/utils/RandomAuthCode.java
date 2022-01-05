package com.lxc.myemail.utils;

import com.lxc.myemail.model.AuthCode;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/***
 * 生成验证码工具类
 * expiredTime 过期时间 3分钟
 */


public class RandomAuthCode {
    private static Map<String ,AuthCode> codeMap = new ConcurrentHashMap<>();
    private static final long expiredTime=1000*10*60*3;
    public  String createCode(AuthCode authCode) {//生成六位验证码

        String code = String.valueOf(new Random().nextInt(899999) + 100000);

//放入验证码

        authCode.setCode(code);//创建时间

        authCode.setCreateTime(LocalDateTime.now());//放入集合key——手机号

        codeMap.put(authCode.getEmail(),authCode);
        return code;

    }//校验验证码

    public  boolean verifyCode(AuthCode authCode) {//清理验证码集合
        //System.out.println(codeMap.hashCode());
        cleanEmailMap();//如果集合内存在符合条件的记录，key=传入的手机号
        //System.out.println(codeMap.toString());
        if(codeMap.containsKey(authCode.getEmail())){
           // System.out.println(codeMap.get(authCode.getEmail()).getCode());
//
//            System.out.println(authCode.getCode());//如果集合内符合条件的的验证码与传入的验证码一直，则返回true

            return codeMap.get(authCode.getEmail()).getCode().equals(authCode.getCode());

        }
        return false;

    }//清空超过有效期的验证码

    public  void cleanEmailMap() {//如果集合不为空

        if(!codeMap.isEmpty()){//循环集合

            for (Map.Entry<String,AuthCode> codeEntry : codeMap.entrySet()) {//获取当前时间和创建时间的时间差

                Duration duration =Duration.between(codeEntry.getValue().getCreateTime(), LocalDateTime.now());//如果时间差大于有效时间—即当前时间-创建时间>有效时间，则验证码过期，从集合中删除

                if(duration.toMillis() >expiredTime){
                    codeMap.remove(codeEntry.getKey());

                }

            }

        }

    }
    public static void main(String[] args){
//       AuthCode authCode  = new AuthCode();
//        authCode.setEmail("17347064540@163.com");//手机号//System.out.println("----生成验证码------:"+CodeUtil.createCode(codeEntity));
//        RandomAuthCode.createCode(authCode);


        AuthCode code =  new AuthCode();
        code.setEmail("17347064540@163.com");
        code.setCode("458268");

//        System.out.println("----校验验证码------:"+authCode.getCode());
//        System.out.println("----校验验证码------:"+RandomAuthCode.verifyCode(code));

    }

}


