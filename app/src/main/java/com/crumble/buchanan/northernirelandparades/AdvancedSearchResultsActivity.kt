package com.crumble.buchanan.northernirelandparades

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_advanced_search_results.*
import org.joda.time.Days
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.jsoup.Jsoup

class AdvancedSearchResultsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advanced_search_results)

        title = getString(R.string.advanced_search_results_action_bar_title)

        advancesSearchResultsNoItemsFoundTextView.visibility = View.GONE

        val advancedSearchFilters = intent.getIntArrayExtra("advancedSearchFilters")

        var url = "https://www.paradescommission.org/searchresults.aspx?pg=1"

        if (advancedSearchFilters != null) {
            for ((i, filterInt) in advancedSearchFilters.withIndex()) {
                val urlArgTag = resources.getStringArray(R.array.advanced_search_url_args)[i]
                url += "&$urlArgTag=$filterInt"
            }
        }

        advancedSearchResultsProgressBar.visibility = View.VISIBLE

        Thread {
            makeAdvancedSearchXMLRequest(url)
        }.start()
    }

    private fun makeAdvancedSearchXMLRequest(url: String) {
        val responseDocument = Jsoup.connect(url).get()

        val mainTable = responseDocument.getElementsByTag("table")[0]
        val mainTableBody = mainTable.getElementsByTag("tbody")[0]
        val mainTableContents = mainTableBody.children()

        val pageParadeList = mutableListOf<Parade>()
        for (element in mainTableContents) {
            if (element.getElementsByTag("th").size > 0) {
                println("Skipping table header...")
                continue
            }
            val parade = Parade(element.children()[1].children()[0].attr("title"))
            parade.location = element.children()[2].children()[0].attr("title")
            parade.fullLocationText = "Location: " + parade.location
            val currentLocalDate = LocalDate()
            var paradeDate: LocalDate
            var daysTillParade: Int
            try {
                parade.dateText = DateTimeFormat.forPattern("dd MMMM yyyy").parseDateTime(element.children()[3].children()[0].html()).toString(DateTimeFormat.forPattern("dd/MM/yyyy"))
                paradeDate = DateTimeFormat.forPattern("dd/MM/yyyy").parseLocalDate(parade.dateText)
                daysTillParade = Days.daysBetween(currentLocalDate, paradeDate).days
                parade.fullDateText = "Date: " + parade.dateText + " (" + daysTillParade + " days from today)"
            } catch (e: Exception) {
                daysTillParade = 0
                parade.dateText = currentLocalDate.toString(DateTimeFormat.forPattern("dd/MM/yyyy"))
                parade.fullDateText = "Date: none available"
            }
            parade.link = "https://www.paradescommission.org" + element.children()[1].children()[0].attr("href")

            parade.advancedSearchOrigin = true

            if (daysTillParade >= 0) {
                pageParadeList.add(0, parade)
            }
        }

        println("Initialising RecyclerView with data...")
        runOnUiThread {
            advancedSearchResultsRecyclerView.layoutManager = LinearLayoutManager(this)
            advancedSearchResultsRecyclerView.adapter = ParadeRecyclerViewAdapter(pageParadeList, true)
            if ((advancedSearchResultsRecyclerView.adapter as ParadeRecyclerViewAdapter).itemCount == 0) {
                advancesSearchResultsNoItemsFoundTextView.visibility = View.VISIBLE
            }
            advancedSearchResultsRecyclerView.invalidate()
            advancedSearchResultsProgressBar.visibility = View.GONE
        }
    }
}