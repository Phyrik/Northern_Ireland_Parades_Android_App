package com.crumble.buchanan.northernirelandparades

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.CalendarContract
import kotlinx.android.synthetic.main.activity_parade_details.*
import org.joda.time.format.DateTimeFormat
import java.util.*

class ParadeDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parade_details)

        val paradeTitle = intent.getStringExtra("paradeTitle")
        val paradeLocation = intent.getStringExtra("paradeLocation")
        val paradeOutwardStartTime = DateTimeFormat.forPattern("HH:mm").parseDateTime(intent.getStringExtra("paradeTime")?.substring(0, 5)?.replace(".", ":"))
        val paradeOutwardStartTimeText = paradeOutwardStartTime.hourOfDay.toString() + ":" + paradeOutwardStartTime.minuteOfHour.toString().padStart(2, '0')
        val paradeOutwardEndTime = DateTimeFormat.forPattern("HH:mm").parseDateTime(intent.getStringExtra("paradeTime")?.substring(9))
        val paradeOutwardEndTimeText = paradeOutwardEndTime.hourOfDay.toString() + ":" + paradeOutwardEndTime.minuteOfHour.toString().padStart(2, '0')
        val paradeDate = DateTimeFormat.forPattern("dd/MM/yyyy").parseDateTime(intent.getStringExtra("paradeDate"))
        val paradeDateText = paradeDate.toString(DateTimeFormat.forPattern("dd MMMM yyyy"))
        val paradeRouteText = intent.getStringExtra("paradeRawRoute")?.replace(",", ", ")
        val paradeParadesCommissionWebsiteLink = intent.getStringExtra("paradeLink")

        title = ("Parade in $paradeLocation")
        paradeTitleTextView.text = paradeTitle
        paradeLocationTextView.text = "Location: $paradeLocation"
        paradeTimeTextView.text = "Starting time: $paradeOutwardStartTimeText"
        paradeDateTextView.text = "Date: $paradeDateText"
        paradeRouteTextView.text = "Route: $paradeRouteText"
        viewParadeOnParadesCommissionWebsiteButton.setOnClickListener {
            val uri = Uri.parse(paradeParadesCommissionWebsiteLink)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        addToCalendarButton.setOnClickListener {
            val calID: Long = 3
            val startMillis: Long = Calendar.getInstance().run {
                println(paradeDate.year)
                println(paradeDate.monthOfYear)
                println(paradeDate.dayOfMonth)
                set(paradeDate.year, paradeDate.monthOfYear - 1, paradeDate.dayOfMonth, paradeOutwardStartTime.hourOfDay, paradeOutwardStartTime.minuteOfHour)
                timeInMillis
            }
            val endMillis: Long = Calendar.getInstance().run {
                set(paradeDate.year, paradeDate.monthOfYear - 1, paradeDate.dayOfMonth, paradeOutwardEndTime.hourOfDay, paradeOutwardEndTime.minuteOfHour)
                timeInMillis
            }

            val intent = Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
                .putExtra(CalendarContract.Events.TITLE, "$paradeTitle Parade")
                .putExtra(CalendarContract.Events.DESCRIPTION, "Route: $paradeRouteText")
                .putExtra(CalendarContract.Events.EVENT_LOCATION, paradeLocation)
            startActivity(intent)
        }
    }
}