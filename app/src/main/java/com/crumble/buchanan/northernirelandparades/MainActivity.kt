package com.crumble.buchanan.northernirelandparades

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
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
import java.util.*

class MainActivity : AppCompatActivity() {

    private var paradesList = mutableListOf<Parade>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = getString(R.string.app_name)

        advancedSearchButton.setOnClickListener {
            val intent = Intent(this, AdvancedSearchActivity::class.java)
            startActivity(intent)
        }

        upcomingParadesSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit (query: String): Boolean {
                paradesNormalSearch(false, query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText == "") {
                    paradesNormalSearch(true)
                }
                return false
            }
        })

        upcomingParadesRecyclerView.layoutManager = LinearLayoutManager(this)
        upcomingParadesRecyclerView.adapter = null

        val queue = Volley.newRequestQueue(this)
        val url = "https://www.paradescommission.org/Home.aspx?rss=UpcomingParades"

        fun makeParadesXMLRequest() {
            val stringRequest =
                StringRequest(Request.Method.GET, url, Response.Listener { response ->
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

    private fun paradesNormalSearch(reset: Boolean, query: String = "") {
        if (reset) {
            upcomingParadesRecyclerView.adapter = ParadeRecyclerViewAdapter(paradesList, false)
            return
        }

        val filteredParadesList = paradesList.filter { p -> p.title.toLowerCase(Locale.ROOT).contains(query) }
        upcomingParadesRecyclerView.adapter = ParadeRecyclerViewAdapter(filteredParadesList, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val mi = menuInflater
        mi.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_credits -> {
            val intent = Intent(this, CreditsActivity::class.java)
            startActivity(intent)
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun paradeXmlResponseReceived(response: String?) {
        val factory = XmlPullParserFactory.newInstance()
        val xpp = factory.newPullParser()

        xpp.setInput(StringReader(response))
        var eventType = xpp.eventType

        var currentTag = ""
        var currentParade: Parade? = null

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_DOCUMENT) {
                println("Started parsing")
            } else if (eventType == XmlPullParser.START_TAG) {
                currentTag = xpp.name
            } else if (eventType == XmlPullParser.END_TAG) {
                if (currentTag == "description") {
                    if (currentParade != null) {
                        paradesList.add(currentParade)
                    }
                }
                currentTag = ""
            } else if (eventType == XmlPullParser.TEXT) {
                if (currentTag == "title" && !xpp.text.contains("RSS")) {
                    currentParade = Parade(xpp.text)
                }
                if (currentTag == "paradedate") {
                    val currentLocalDate = LocalDate()
                    val paradeDate = DateTimeFormat.forPattern("dd/MM/yyyy").parseLocalDate(xpp.text.substring(0, 10) as String?)
                    val daysTillParade = Days.daysBetween(currentLocalDate, paradeDate).days
                    currentParade?.dateText = xpp.text.substring(0, 10)
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
        upcomingParadesRecyclerView.adapter = ParadeRecyclerViewAdapter(paradesList, false)
    }
}

class Parade(val title: String) {

    var advancedSearchOrigin = false

    var location = ""
    var fullLocationText = ""
    var timeText = ""
    var fullTimeText = ""
    var dateText = ""
    var fullDateText = ""
    var rawRouteText = ""
    var link = ""

}