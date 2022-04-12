package com.wpf.unittest.junit;

import com.wpf.unittest.DateUtil;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

/**
 * Author: feipeng.wang
 * Time:   2022/4/11
 * Description : This is description.
 */
public class DateUtilTest extends TestCase {
    private long timeStamp = 1508054402000L;
//    private String dateString = "2017-10-15 16:00:02";
    private String dateString = "2017-10-15";
    private Date date;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        System.out.print("Before \n");
        date = new Date();
        date.setTime(timeStamp);
    }

    @After
    public void tearDown() throws Exception {
        System.out.print("After \n");

    }

    @Test
    public void testStampToDate() {
        System.out.print("test StampToDate \n");
        long time = 1508054402000L;
        assertNotSame("预期时间", DateUtil.stampToDate(time));
    }
    @Test
    public void testDateToStamp() throws ParseException {
        System.out.print("test  dateToStamp \n");
        DateUtil.dateToStamp(dateString);
//        assertEquals("预期时间",DateUtil.dateToStamp(dateString));
    }
}