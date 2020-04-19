package com.devtau.ironHeroes.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.devtau.ironHeroes.data.model.SpinnerItem
import com.devtau.ironHeroes.enums.HumanType

class IronSpinnerAdapter(
    context: Context,
    private val values: List<SpinnerItem>,
    private val withHeader: Boolean,
    private val headerText: String = "- -",
    private val listItemRes: Int = android.R.layout.simple_spinner_item,
    private val listItemDropdownRes: Int = android.R.layout.simple_spinner_dropdown_item
): ArrayAdapter<SpinnerItem>(context, listItemRes, values) {

    override fun getCount(): Int = values.size + if (withHeader) 1 else 0
    override fun getItem(position: Int): SpinnerItem? =
        if (withHeader && position == 0) {
            null
        } else {
            values[position - if (withHeader) 1 else 0]
        }

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = (convertView ?: LayoutInflater.from(context)
            .inflate(listItemRes, parent, false)) as TextView
        view.text = getItem(position)?.getFormattedName() ?: headerText
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(context)
            .inflate(listItemDropdownRes, parent, false) as TextView
        view.text = getItem(position)?.getFormattedName() ?: headerText
        return view
    }

    fun getItemPosition(itemId: Long?): Int {
        for ((i, next) in values.withIndex())
            if (next.id == itemId)
                return i + if (withHeader) 1 else 0
        return 0
    }


    interface ItemSelectedListener {
        fun onItemSelected(item: SpinnerItem?, humanType: HumanType? = null)
    }
}