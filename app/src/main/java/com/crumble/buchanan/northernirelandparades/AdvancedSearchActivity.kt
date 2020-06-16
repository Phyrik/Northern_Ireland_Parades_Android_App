package com.crumble.buchanan.northernirelandparades

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_advanced_search.*

class AdvancedSearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advanced_search)

        title = getString(R.string.advanced_search_action_bar_title)

        val advancedSearchFilters = IntArray(1)

        ArrayAdapter.createFromResource(this, R.array.org_types_array, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            organisationTypeSpinner.adapter = adapter
        }

        organisationTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                println("Set org type argument with a value of " + resources.getIntArray(R.array.org_types_array_int)[pos])
                advancedSearchFilters[0] = resources.getIntArray(R.array.org_types_array_int)[pos]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        advancedSearchGoButton.setOnClickListener {
            val intent = Intent(this, AdvancedSearchResultsActivity::class.java)

            intent.putExtra("advancedSearchFilters", advancedSearchFilters)

            startActivity(intent)
        }
    }
}