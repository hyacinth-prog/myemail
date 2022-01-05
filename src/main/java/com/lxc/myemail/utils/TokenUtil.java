package com.lxc.myemail.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.lxc.myemail.model.User;

import java.util.*;

public class TokenUtil {
    private static final long EXPIRE_TIME= 10*60*60*1000;
    private static final String TOKEN_SECRET="txdy";  //密钥盐

    /**
     * 签名生成
     * @param user
     * @return
     */
    public static String sign(User user){
        Map<String,User> map = new Map<String, User>() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean containsKey(Object key) {
                return false;
            }

            @Override
            public boolean containsValue(Object value) {
                return false;
            }

            @Override
            public User get(Object key) {
                return null;
            }

            @Override
            public User put(String key, User value) {
                return null;
            }

            @Override
            public User remove(Object key) {
                return null;
            }

            @Override
            public void putAll(Map<? extends String, ? extends User> m) {

            }

            @Override
            public void clear() {

            }

            @Override
            public Set<String> keySet() {
                return null;
            }

            @Override
            public Collection<User> values() {
                return null;
            }

            @Override
            public Set<Entry<String, User>> entrySet() {
                return null;
            }
        };

        String token = null;
        try {
            Date expiresAt = new Date(System.currentTimeMillis() + EXPIRE_TIME);
            map.put("user",user);
            token = JWT.create()
                    .withIssuer("auth0")
                    .withClaim("username", user.getUsername())
//                    .withClaim("user", map)
                    .withExpiresAt(expiresAt)
                    // 使用了HMAC256加密算法。
                    .sign(Algorithm.HMAC256(TOKEN_SECRET));

        } catch (Exception e){
            e.printStackTrace();
        }

        return token;
    }

    /**
     * 签名验证
     * @param token
     * @return
     */
    public static boolean verify(String token){
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).withIssuer("auth0").build();
            DecodedJWT jwt = verifier.verify(token);
            System.out.println("认证通过：");
            System.out.println("username: " + jwt.getClaim("username").asString());//jwt.getClaim("username").asString()
            System.out.println("过期时间：      " + jwt.getExpiresAt());
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public static String getUsername(String token){
        String username=null;
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).withIssuer("auth0").build();
            DecodedJWT jwt = verifier.verify(token);
            username =jwt.getClaim("username").asString();

        } catch (Exception e){
            System.out.println(e.toString());
        }
        return username;
    }
}
