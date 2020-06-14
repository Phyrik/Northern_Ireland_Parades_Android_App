package com.crumble.buchanan.northernirelandparades

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import org.joda.time.Days
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = getString(R.string.app_name)

        upcomingParadesRecyclerView.layoutManager = LinearLayoutManager(this)
        upcomingParadesRecyclerView.adapter = null

        val queue = Volley.newRequestQueue(this)
        val url = "https://www.paradescommission.org/Home.aspx?rss=UpcomingParades"

        fun makeParadesXMLRequest() {
            val stringRequest =
                StringRequest(Request.Method.GET, url, Response.Listener<String> { response ->
                    paradeXmlResponseReceived(response)
                }, Response.ErrorListener { error ->
                    println(error)
                    println("Retrying XML request...")
                    makeParadesXMLRequest()
                })
            queue.add(stringRequest)
            queue.start()
        }

        makeParadesXMLRequest()
    }

    private fun paradeXmlResponseReceived(response: String?) {
        val factory = XmlPullParserFactory.newInstance()
        val xpp = factory.newPullParser()

        xpp.setInput(StringReader(response))
        var eventType = xpp.eventType

        var currentTag = ""
        var paradesList = mutableListOf<Parade>()
        var currentParade: Parade? = null

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_DOCUMENT) {
                println("Start document");
            } else if (eventType == XmlPullParser.START_TAG) {
                println("Start tag " + xpp.name);
                currentTag = xpp.name
            } else if (eventType == XmlPullParser.END_TAG) {
                println("End tag " + xpp.name);
                if (currentTag == "description") {
                    if (currentParade != null) {
                        paradesList.add(currentParade)
                    }
                }
                currentTag = ""
            } else if (eventType == XmlPullParser.TEXT) {
                println("Text " + xpp.text);
                if (currentTag == "title" && !xpp.text.contains("RSS")) {
                    currentParade = Parade(xpp.text)
                }
                if (currentTag == "paradedate") {
                    val currentLocalDate = LocalDate()
                    val paradeDate = DateTimeFormat.forPattern("dd/MM/yyyy").parseLocalDate(xpp.text.substring(0, 10) as String?)
                    val daysTillParade = Days.daysBetween(currentLocalDate, paradeDate).days
                    currentParade?.dateText = xpp.text.substring(0, 10) as String
                    currentParade?.fullDateText = "Date: " + xpp.text.substring(0, 10) + " (" + daysTillParade + " days from today)"
                }
                if (currentTag == "outwardroute") {
                    currentParade?.rawRouteText = xpp.text
                }
                if (currentTag == "outwardtimes") {
                    currentParade?.timeText = xpp.text
                    currentParade?.fullTimeText = "Parade outward time: " + xpp.text
                }
                if (currentTag == "description" && !xpp.text.contains("Upcoming Parades")) {
                    currentParade?.location = xpp.text.split("<br />")[0].trim().substring(10)
                    currentParade?.fullLocationText = xpp.text.split("<br />")[0].trim()
                }
                if (currentTag == "link") {
                    currentParade?.link = xpp.text
                }
            }
            eventType = xpp.next()
        }

        println("Initialising RecyclerView with data...")
        upcomingParadesRecyclerView.adapter = MainAdapter(paradesList)
    }
}

class Parade(val title: String) {

    var location = ""
    var fullLocationText = ""
    var timeText = ""
    var fullTimeText = ""
    var dateText = ""
    var fullDateText = ""
    var rawRouteText = ""
    var link = ""

}