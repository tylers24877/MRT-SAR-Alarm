/*
 * *
 *  * Created by Tyler Simmonds.
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 28/08/20 19:41
 *
 */

package uk.mrs.saralarm.ui.settings.support

import android.content.Context
import android.util.AttributeSet
import androidx.navigation.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import uk.mrs.saralarm.R

class CustomMessagesPreference @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = R.attr.preferenceStyle,
)
    : Preference(context, attrs, defStyleAttr) {


    init {
        widgetLayoutResource = R.layout.custom_messages_preference
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        with(holder.itemView) {
            setOnPreferenceClickListener {
                findNavController().navigate(R.id.action_navigation_setting_to_customMessagesFragment)
                true
            }
            // do the view initialization here...
            //textView.text = "Another Text"
        }
    }
}