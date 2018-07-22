package com.bamilo.modernbamilo.product.descspec.spec

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.bamilo.modernbamilo.R
import com.bamilo.modernbamilo.product.descspec.spec.pojo.SpecificationRow
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter

class SpecificationTableAdapter(private val mSpecificationRows: ArrayList<SpecificationRow>) : BaseAdapter(), StickyListHeadersAdapter {

    override fun getHeaderId(position: Int) = position.toLong()

    override fun getItemId(position: Int) = position.toLong()

    override fun getItem(position: Int) = mSpecificationRows[position].content

    override fun getCount() = mSpecificationRows.size

    override fun getHeaderView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = LayoutInflater.from(parent?.context!!).inflate(R.layout.row_header_spec, parent, false)
        val titleTextView = view.findViewById(R.id.rowHeaderSpec_xeiTextView_title) as TextView
        titleTextView.text = mSpecificationRows[position].headerTitle
        return view
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val viewLinearLayout = LayoutInflater.from(parent?.context!!).inflate(R.layout.row_spec_item, parent, false) as LinearLayout

        for (tuple in mSpecificationRows[position].content) {
            val tupleView = LayoutInflater.from(parent?.context!!).inflate(R.layout.row_spec_item_tuple, parent, false)

            val titleTextView = tupleView.findViewById(R.id.rowSpecItemTuple_xeiTextView_title) as TextView
            titleTextView.text = tuple.title

            val valueTextView = tupleView.findViewById(R.id.rowSpecItemTuple_xeiTextView_value) as TextView
            valueTextView.text = tuple.value

            viewLinearLayout.addView(tupleView)
            viewLinearLayout.addView(LayoutInflater.from(parent?.context!!).inflate(R.layout.row_spec_item_divider, parent, false))
        }

        // because of touch feedback
        viewLinearLayout.setOnClickListener(null)
        return viewLinearLayout
    }

}
