/*
 * *
 *  * Created by Tyler Simmonds.
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 28/08/20 19:41
 *
 */

package uk.mrs.saralarm.ui.settings.support

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_custom_messages.view.*
import uk.mrs.saralarm.R


class CustomMessagesFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {


        //get preferences for application
        val customMessageArray: ArrayList<String>
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val json = sharedPrefs.getString("customMessageJSON", "")
        if (json.isNullOrBlank()) {
            customMessageArray = ArrayList()
            customMessageArray.add("")
        } else {
            val token: TypeToken<List<String>> = object : TypeToken<List<String>>() {}
            customMessageArray = Gson().fromJson(json, token.type)
        }

        // set up the RecyclerView
        val root = inflater.inflate(R.layout.fragment_custom_messages, container, false)
        // set up the RecyclerView
        root.customMessageRecyclerView.layoutManager = LinearLayoutManager(context)
        adapter = RecyclerViewAdapter(context, customMessageArray)
        root.customMessageRecyclerView.adapter = adapter

        val callback = DragAdapter(
                adapter, requireContext(),
                ItemTouchHelper.UP.or(ItemTouchHelper.DOWN), ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT)
        )
        val helper = ItemTouchHelper(callback)
        helper.attachToRecyclerView(root.customMessageRecyclerView)

        root.custom_message_fab.setOnClickListener { adapter.addItem() }
        return root
    }

    override fun onPause() {
        adapter.saveData()
        Log.d(javaClass.name, "onPause: ")
        super.onPause()
    }

    companion object {
        private lateinit var adapter: RecyclerViewAdapter
    }
}