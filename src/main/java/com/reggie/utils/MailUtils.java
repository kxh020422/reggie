package com.reggie.utils;

import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;

public class   MailUtils {
    public static void sendCode(String code, String receiveMail) {
        MailAccount fromAccount = new MailAccount();
        fromAccount.setHost("smtp.163.com");
        fromAccount.setSslEnable(true);
        fromAccount.setPort(465);
        fromAccount.setFrom("kxh020422@163.com");
        fromAccount.setUser("kxh020422@163.com");
        fromAccount.setPass("SYJPGHNJTLUBXNQQ");
        MailUtil.send(fromAccount, receiveMail, "验证码", code, false, null);
    }

}
