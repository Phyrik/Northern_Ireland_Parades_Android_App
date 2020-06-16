package com.crumble.buchanan.northernirelandparades

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.parade_row.view.*

class ParadeRecyclerViewAdapter(private val paradesList: List<Parade>, private val advancedSearchOrigin: Boolean): RecyclerView.Adapter<ParadeViewHolder>() {

    override fun getItemCount(): Int {
        return paradesList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParadeViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.parade_row, parent, false)
        return ParadeViewHolder(cellForRow, advancedSearchOrigin)
    }

    override fun onBindViewHolder(holder: ParadeViewHolder, position: Int) {
        holder.paradeObject = paradesList[position]

        val paradeTitle = paradesList[position].title
        val paradeLocation = paradesList[position].fullLocationText
        val paradeFullTime = paradesList[position].fullTimeText
        val paradeFullDate = paradesList[position].fullDateText
        holder.view.paradeTitleTextView.text = paradeTitle
        holder.view.paradeLocationTextView.text = paradeLocation
        holder.view.paradeTimeTextView.text = paradeFullTime
        holder.view.paradeDateTextView.text = paradeFullDate
    }
}

class ParadeViewHolder(val view: View, private val advancedSearchOrigin: Boolean): RecyclerView.ViewHolder(view) {

    var paradeObject: Parade? = null

    init {
        if (!advancedSearchOrigin) {
            view.setOnClickListener {
                val intent = Intent(view.context, ParadeDetailsActivity::class.java)
                intent.putExtra("paradeTitle", paradeObject?.title)
                intent.putExtra("paradeLocation", paradeObject?.location)
                intent.putExtra("paradeTime", paradeObject?.timeText)
                intent.putExtra("paradeDate", paradeObject?.dateText)
                intent.putExtra("paradeRawRoute", paradeObject?.rawRouteText)
                intent.putExtra("paradeLink", paradeObject?.link)

                intent.putExtra("advancedSearchOrigin", advancedSearchOrigin)

                view.context.startActivity(intent)
            }
        }

        if (advancedSearchOrigin) {
            view.setOnClickListener {
                val intent = Intent(view.context, ParadeDetailsActivity::class.java)
                intent.putExtra("paradeTitle", paradeObject?.title)
                intent.putExtra("paradeLocation", paradeObject?.location)
                intent.putExtra("paradeDate", paradeObject?.dateText)
                intent.putExtra("paradeLink", paradeObject?.link)

                intent.putExtra("advancedSearchOrigin", advancedSearchOrigin)

                println("Advanced search parade clicked")

                view.context.startActivity(intent)
            }
        }
    }
}