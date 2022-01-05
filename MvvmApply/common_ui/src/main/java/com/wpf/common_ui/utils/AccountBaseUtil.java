package com.wpf.common_ui.utils;

import android.content.Context;
import android.text.TextUtils;

import com.anker.common.utils.MD5Util;

/**
 * Create by Arrietty on 2020/3/9
 */
public class AccountBaseUtil {
    //账号的文件
    public static final String ACCOUNT_FILE_NAME = "account_file_name";
    public static final String KEY_ACCOUNT_EMAIL = "key_account_email";
    public static final String KEY_ACCOUNT_PASSWORD = "key_account_password";
    public static final String KEY_ACCOUNT_TOKEN = "key_account_token";
    public static final String KEY_ACCOUNT_UID = "key_account_uid";
    public static final String KEY_ACCOUNT_NICK_NAME = "key_account_nick_name";

    /**
     * 是否有账号信息
     *
     * @return
     */
    public static boolean isLogin(Context context) {
        String email = getAccountEmail(context);
        String psw = getAccountPassword(context);
        String token = getAccountToken(context);
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(psw) || TextUtils.isEmpty(token)) {
            return false;
        }
        return true;
    }

    public static String getAccountEmail(Context context) {
        return SPUtil.getString(context, ACCOUNT_FILE_NAME, KEY_ACCOUNT_EMAIL, "");
    }

    public static String getAccountPassword(Context context) {
        return SPUtil.getString(context, ACCOUNT_FILE_NAME, KEY_ACCOUNT_PASSWORD, "");
    }

    public static String getAccountNickName(Context context) {
        return SPUtil.getString(context, ACCOUNT_FILE_NAME, KEY_ACCOUNT_NICK_NAME, "");
    }

    public static String getAccountToken(Context context) {
        return SPUtil.getString(context, ACCOUNT_FILE_NAME, KEY_ACCOUNT_TOKEN, "");
    }

    public static String getAccountUid(Context context) {
        return SPUtil.getString(context, ACCOUNT_FILE_NAME, KEY_ACCOUNT_UID, "");
    }

    public static String getUserToken(Context context) {
        String accountToken = AccountUtils.getAccountToken(context);
        if (TextUtils.isEmpty(accountToken)) {
            String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
            accountToken = MD5Util.md5Encrypt(timestamp + "AUF77ACC2", false);
        }
        return accountToken;
    }
}
