package com.crumble.buchanan.northernirelandparades

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.CalendarContract
import kotlinx.android.synthetic.main.activity_parade_details.*
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.util.*

class ParadeDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parade_details)

        val advancedSearchOrigin = intent.getBooleanExtra("advancedSearchOrigin", true)

        val paradeTitle = intent.getStringExtra("paradeTitle")
        val paradeLocation = intent.getStringExtra("paradeLocation")
        val paradeDate = DateTimeFormat.forPattern("dd/MM/yyyy")
            .parseDateTime(intent.getStringExtra("paradeDate"))
        val paradeDateText = paradeDate.toString(DateTimeFormat.forPattern("dd MMMM yyyy"))
        val paradeParadesCommissionWebsiteLink = intent.getStringExtra("paradeLink")

        var paradeOutwardStartTime: DateTime? = null
        var paradeOutwardStartTimeText = "none available"
        var paradeOutwardEndTime: DateTime? = null
        var paradeRouteText: String? = "none available"

        if (!advancedSearchOrigin) {
            paradeOutwardStartTime = DateTimeFormat.forPattern("HH:mm")
                .parseDateTime(intent.getStringExtra("paradeTime")?.substring(0, 5)?.replace(".", ":"))
            paradeOutwardStartTimeText =
                paradeOutwardStartTime.hourOfDay.toString() + ":" + paradeOutwardStartTime.minuteOfHour.toString()
                    .padStart(2, '0')
            paradeOutwardEndTime = DateTimeFormat.forPattern("HH:mm")
                .parseDateTime(intent.getStringExtra("paradeTime")?.substring(9))
            val paradeOutwardEndTimeText =
                paradeOutwardEndTime.hourOfDay.toString() + ":" + paradeOutwardEndTime.minuteOfHour.toString()
                    .padStart(2, '0')
            paradeRouteText = intent.getStringExtra("paradeRawRoute")?.replace(",", ", ")
        }

        title = ("Parade in $paradeLocation")
        paradeTitleTextView.text = paradeTitle
        paradeLocationTextView.text = "Location: $paradeLocation"
        paradeDateTextView.text = "Date: $paradeDateText"
        paradeTimeTextView.text = "Starting time: $paradeOutwardStartTimeText"
        paradeRouteTextView.text = "Route: $paradeRouteText"

        viewParadeOnParadesCommissionWebsiteButton.setOnClickListener {
            val uri = Uri.parse(paradeParadesCommissionWebsiteLink)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        addToCalendarButton.setOnClickListener {
            val startMillis: Long = Calendar.getInstance().run {
                println(paradeDate.year)
                println(paradeDate.monthOfYear)
                println(paradeDate.dayOfMonth)
                if (advancedSearchOrigin) {
                    set(paradeDate.year, paradeDate.monthOfYear - 1, paradeDate.dayOfMonth)
                } else if (!advancedSearchOrigin) {
                    set(paradeDate.year, paradeDate.monthOfYear - 1, paradeDate.dayOfMonth, paradeOutwardStartTime!!.hourOfDay, paradeOutwardStartTime.minuteOfHour)
                }
                timeInMillis
            }
            val endMillis: Long = Calendar.getInstance().run {
                if (advancedSearchOrigin) {
                    set(paradeDate.year, paradeDate.monthOfYear - 1, paradeDate.dayOfMonth)
                } else if (!advancedSearchOrigin) {
                    set(paradeDate.year, paradeDate.monthOfYear - 1, paradeDate.dayOfMonth, paradeOutwardEndTime!!.hourOfDay, paradeOutwardEndTime.minuteOfHour)
                }
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