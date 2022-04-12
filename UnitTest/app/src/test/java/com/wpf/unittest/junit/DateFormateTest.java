package com.wpf.unittest.junit;

import com.wpf.unittest.DateUtil;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Author: feipeng.wang
 * Time:   2022/4/12
 * Description : 多样化参数
 */
@RunWith(Parameterized.class)
public class DateFormateTest extends TestCase {

    private String time;
    public DateFormateTest(String t){
        this.time = t;
    }
    @Parameterized.Parameters
    public static Collection primeNumber(){
        return Arrays.asList(new String[]{
                "2017-10-15",
                "2017-10-15 16:00:02",
                "2017-10-15 16时00分02秒"
        });
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
    }

    public void testDateToStamp() {
    }

    @Test
    public void testStampToDate() throws ParseException {
        DateUtil.dateToStamp(time);
    }
}