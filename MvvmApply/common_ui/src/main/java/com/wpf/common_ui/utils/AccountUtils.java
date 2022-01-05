//
// * Copyright © 2015-2018 Anker Innovations Technology Limited All Rights Reserved.
// * The program and materials is not free. Without our permission, any use, including but not
// limited to reproduction, retransmission, communication, display, mirror, download,
// modification, is expressly prohibited. Otherwise, it will be pursued for legal liability.

//
package com.wpf.common_ui.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.wpf.common_ui.utils.AccountBaseUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * 账号功能相关的工具类
 */
public class AccountUtils extends AccountBaseUtil {
    private static final String EMAIL_REGEX = "^[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,14}$";
    private static final String PSD_REGEX = "^[A-Za-z`~!()@#$%^&*-_=+\\|[{]}/?.>,\'\";:]{8,20}$";
    private static final String NICKNAME_REGEX = "^[a-z0-9A-Z_-]{1,32}$";
//    private static final String PDF_NAME_REGEX = "[^\\s\\\\/:\\*\\?\\\"<>\\|](\\x20|[^\\s\\\\/:\\*\\?\\\"<>\\|])*[^\\s\\\\/:\\*\\?\\\"<>\\|\\.]$";
    private static final String PDF_NAME_REGEX = "[`~!@#$%^&*+=|{}':;',\\[\\].<>/?~！@#￥%……&*+|{}【】‘；：”“'。，、？～]";


    public static final String ACCOUNT_SIGN_UP_SUCCESS = "account_sign_up_success";
    public static final String KEY_ACCOUNT_SIGNUP_SUCCESS_EMAIL = "key_account_signup_success_email";

    private static final String KEY_ACCOUNT_TOKEN_EXPIRES_AT = "key_account_token_expires_at";

    private static final String KEY_ACCOUNT_AVATAR = "key_account_avatar";

    private static final String KEY_ACCOUNT_HAS_SKIPED = "key_account_has_skiped";

    //是否强制登录
    public final static String KEY_ACCOUNT_FORCE_LOGIN = "key_account_force_login";


    //登陆界面传递给忘记密码页面的Email
    public static final String ACCOUNT_SIGN_UP_TO_FORGOT_EMAIL = "account_sign_up_to_forgot_email";

    /**
     * judge email is valid with regex
     */
    public static Boolean isEmailValidWithRegex(String email) {
        return Pattern.matches(EMAIL_REGEX, email);
    }

    /**
     * judge psd is valid with regex
     */
    public static Boolean isPsdValidWithRegex(String psd) {
        return Pattern.matches(PSD_REGEX, psd);
    }

    /**
     * judge nick name is valid with regex
     */
    public static Boolean isNickNameValid(String nickName) {
        return true;
    }

    /**
     * judge pdf name is valid with regex
     */
    public static Boolean isPDFNameValid(String name) {
        return Pattern.matches(PDF_NAME_REGEX, name);
    }

    public static String stringFilter(String str)throws PatternSyntaxException {
        String regEx=PDF_NAME_REGEX;
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("");
    }
    /**
     * func to set psd visible
     */
    public static void setPsdVisible(EditText editText) {
        editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    /**
     * func to set psd invisible
     */
    public static void setPsdInvisible(EditText editText) {
        editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
    }

    public static void hideSoftKeyBoard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static Boolean isEqualAccountPsd(Context context, String psd) {
        return getAccountPassword(context).equals(psd);
    }

    public static void setLoginStatus(Context context, boolean isExpired) {
        if (isExpired) {
            setAccountToken(context, "");
        }
    }

    public static void setAccountToken(Context context, String token) {
        SharedPreferences sp = context.getSharedPreferences(ACCOUNT_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_ACCOUNT_TOKEN, token);
        editor.commit();
    }

    /**
     * 存储用户登录成功的邮箱和密码
     *
     * @param context
     * @param email
     * @param psd
     */
    public static void saveLoginAccountInfo(Context context, String email, String psd) {
        SharedPreferences sp = context.getSharedPreferences(ACCOUNT_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (email != null) {
            editor.putString(KEY_ACCOUNT_EMAIL, email);
        }
        if (psd != null) {
            editor.putString(KEY_ACCOUNT_PASSWORD, psd);
        }
        editor.commit();
    }

    /**
     * 存储用户注册成功的邮箱和密码
     *
     * @param context
     * @param email
     * @param psd
     */
    public static void saveAccountInfo(Context context, String email, String psd, String token) {
        SharedPreferences sp = context.getSharedPreferences(ACCOUNT_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (email != null) {
            editor.putString(KEY_ACCOUNT_EMAIL, email);
        }
        if (psd != null) {
            editor.putString(KEY_ACCOUNT_PASSWORD, psd);
        }
        if (token != null) {
            editor.putString(KEY_ACCOUNT_TOKEN, token);
        }
        editor.commit();
    }

    public static void saveNickNameInfo(Context context, String nickName) {
        SharedPreferences sp = context.getSharedPreferences(ACCOUNT_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (nickName != null) {
            editor.putString(KEY_ACCOUNT_NICK_NAME, nickName);
        }
        editor.commit();
    }

    public static void savePsdInfo(Context context, String newPsd) {
        SharedPreferences sp = context.getSharedPreferences(ACCOUNT_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (newPsd != null) {
            editor.putString(KEY_ACCOUNT_PASSWORD, newPsd);
        }
        editor.commit();
    }

    public static boolean getKeyAccountHasSkiped(Context context) {
        return SPUtil.getBoolean(context, KEY_ACCOUNT_HAS_SKIPED, false);
    }

    public static void setKeyAccountHasSkiped(Context context) {
        SPUtil.putBoolean(context, KEY_ACCOUNT_HAS_SKIPED, true);
    }
}
