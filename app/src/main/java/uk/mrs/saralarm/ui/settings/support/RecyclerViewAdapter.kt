/*
 * *
 *  * Created by Tyler Simmonds.
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 28/08/20 19:41
 *
 */

package uk.mrs.saralarm.ui.settings.support

import android.animation.ObjectAnimator
import android.content.Context
import android.content.SharedPreferences
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.EditText
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.recycler_view_custom_message_row.view.*
import uk.mrs.saralarm.R


class RecyclerViewAdapter internal constructor(context: Context?, data: ArrayList<String>) :
        RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {
    private val mData: ArrayList<String> = data
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private var mContext: Context? = context

    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = mInflater.inflate(R.layout.recycler_view_custom_message_row, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the TextView in each row
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (mData[holder.adapterPosition].isBlank()) {
            holder.myTextView.setText("")
            holder.myTextView.hint = "Type here..."
        } else {
            holder.myTextView.setText(mData[holder.adapterPosition])
        }

        holder.myTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                    s: CharSequence, start: Int, count: Int,
                    after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                //avoid triggering event when text is empty
                if (s.isNotEmpty()) {
                    mData[holder.adapterPosition] = holder.myTextView.text.toString()
                }
            }
        }
        )
    }

    // total number of rows
    override fun getItemCount(): Int {
        return mData.size
    }

    /**
     * Function called to swap dragged items
     */
    fun swapItems(fromPosition: Int, toPosition: Int) {

        val original = mData[fromPosition]
        mData.removeAt(fromPosition)
        mData.add(toPosition, original)
        notifyItemMoved(fromPosition, toPosition)
    }

    fun removeItems(adapterPosition: Int) {
        mData.removeAt(adapterPosition)
        notifyItemRemoved(adapterPosition)
    }

    fun addItem() {
        mData.add("")
        notifyDataSetChanged()
    }

    fun saveData() {
        mData.removeAll(listOf("", null))
        val sharedPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)
        val editor = sharedPrefs.edit()

        val json = Gson().toJson(mData)

        editor.putString("customMessageJSON", json)
        editor.apply()

    }

    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), ItemTouchViewHolder, View
    .OnClickListener {
        var myTextView: EditText = itemView.customMessageEditText

        override fun onItemSelected() {
            val animator: ObjectAnimator = ObjectAnimator.ofFloat(itemView.recycler_cardview, "cardElevation", dipToPixels(2f),
                    dipToPixels(10f))
            animator.interpolator = AccelerateInterpolator()
            animator.start()
        }

        override fun onItemClear() {
            val animator: ObjectAnimator = ObjectAnimator.ofFloat(itemView.recycler_cardview, "cardElevation", dipToPixels(10f),
                    dipToPixels(2f))
            animator.interpolator = AccelerateInterpolator()
            animator.start()
            //saveData()
        }

        private fun dipToPixels(dipValue: Float): Float {
            val metrics = mContext?.resources?.displayMetrics
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics)
        }

        override fun onClick(v: View?) {
        }
    }
}