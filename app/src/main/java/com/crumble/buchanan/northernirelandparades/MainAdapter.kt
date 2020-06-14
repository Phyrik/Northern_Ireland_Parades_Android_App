package com.crumble.buchanan.northernirelandparades

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.parade_row.view.*

class MainAdapter(private val paradesList: List<Parade>): RecyclerView.Adapter<CustomViewHolder>() {

    override fun getItemCount(): Int {
        return paradesList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent?.context)
        val cellForRow = layoutInflater.inflate(R.layout.parade_row, parent, false)
        return CustomViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
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

class CustomViewHolder(val view: View): RecyclerView.ViewHolder(view) {

    var paradeObject: Parade? = null

    init {
        view.setOnClickListener {
            val intent = Intent(view.context, ParadeDetailsActivity::class.java)
            intent.putExtra("paradeTitle", paradeObject?.title)
            intent.putExtra("paradeLocation", paradeObject?.location)
            intent.putExtra("paradeTime", paradeObject?.timeText)
            intent.putExtra("paradeDate", paradeObject?.dateText)
            intent.putExtra("paradeRawRoute", paradeObject?.rawRouteText)
            intent.putExtra("paradeLink", paradeObject?.link)

            view.context.startActivity(intent)
        }
    }
}