package com.lxc.myemail.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {
    public static String filterHtml(String content){
        String regEx_script="<script[^>]*?>[\\s\\S]*?<\\/script>"; //定义script的正则表达式
        String regEx_style="<style[^>]*?>[\\s\\S]*?<\\/style>"; //定义style的正则表达式
        String regEx_html="<[^>]+>"; //定义HTML标签的正则表达式


        Pattern p_script=Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE);
        Matcher m_script=p_script.matcher(content);
        content=m_script.replaceAll(""); //过滤script标签
        // System.out.println(content);

        Pattern p_style=Pattern.compile(regEx_style,Pattern.CASE_INSENSITIVE);
        Matcher m_style=p_style.matcher(content);
        content=m_style.replaceAll(""); //过滤style标签
        // System.out.println(content);

        Pattern p_html=Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE);
        Matcher m_html=p_html.matcher(content);
        content=m_html.replaceAll(""); //过滤html标签

        Pattern p_space=Pattern.compile("[\\r\\n\\s]",Pattern.CASE_INSENSITIVE);
        Matcher m_space=p_space.matcher(content);
        content=m_space.replaceAll(""); //过滤回车换行和空格
        content= content.length()>20?content.substring(0,17)+"...":content;
        return content;
    }
}
