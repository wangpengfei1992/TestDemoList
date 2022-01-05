package com.oceawing.blemanager;

import android.content.Context;
import android.util.Log;

import androidx.test.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Objects;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
//        Context appContext = InstrumentationRegistry.getTargetContext();
//
//        assertEquals("com.oceawing.blemanager.test", appContext.getPackageName());
        UUID uuid1 = UUID.fromString("0112F5DA-0000-1000-8000-00805F9B34FB");
        UUID uuid2 = UUID.fromString("0112f5da-0000-1000-8000-00805F9B34FB");

        Boolean b = Objects.equals(uuid1, uuid2);
        Log.d("haha", "xixixi   ====  " + b);
    }
}
