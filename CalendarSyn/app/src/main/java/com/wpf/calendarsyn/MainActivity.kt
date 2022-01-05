package com.wpf.calendarsyn


import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.api.services.calendar.Calendar
import com.wpf.calendarsyn.android.CalendarSampleActivity

/*
* 该demo主要是测试获取Google、ZOOM、Outlook里面的日历
*
* Google
* https://developers.google.cn/calendar/api/concepts/events-calendars
* https://console.cloud.google.com/apis/library/calendar-json.googleapis.com?project=ankerwork&supportedpurview=project
* https://developers.google.com/calendar/api/v3/reference/calendars/get?hl=zh_CN
*
* outlook
* https://office.live.com/start/Calendar.aspx?ui=en%2DUS&rs=US
* https://developer.microsoft.com/en-us/graph/get-started/android
*
* zoom
* https://blog.csdn.net/ytlzq0228/article/details/121754522
* https://marketplace.zoom.us/docs/sdk/sdk-reference/android-reference
* */
class MainActivity : AppCompatActivity() {
    private var activity:MainActivity ?= null
    private var getGoogleCalendar:TextView ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        activity = this
        getGoogleCalendar = findViewById(R.id.getGoogleCalendar)
        getGoogleCalendar?.setOnClickListener {

//           CalendarQuickstart.main2(activity)

            var intent = Intent(activity, CalendarSampleActivity::class.java)
            activity?.startActivity(intent)

        }
    }
}
