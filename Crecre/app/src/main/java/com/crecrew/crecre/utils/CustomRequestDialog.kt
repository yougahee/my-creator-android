package com.crecrew.crecre.utils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.crecrew.crecre.R
import kotlinx.android.synthetic.main.custom_dialog_request_act.*
import kotlinx.android.synthetic.main.custom_dialog_search_alarm.*
import kotlinx.android.synthetic.main.custom_dialog_search_alarm.btn_bottom_box_search_custom_dialog
import kotlinx.android.synthetic.main.custom_dialog_search_alarm.tv_complete_search_custom_dialog

class CustomRequestDialog (context: Context,
                         private val completeContext : String,
                         private val mContent: String,
                           private val eContent : String,
                         private val mResponseClickListener: View.OnClickListener?, private val responseText: String) : Dialog(context, android.R.style.Theme_Translucent_NoTitleBar) {

    private val clickedState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val lpWindow = WindowManager.LayoutParams()
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        lpWindow.dimAmount = 0.55f
        window!!.attributes = lpWindow

        setContentView(R.layout.custom_dialog_request_act)

        tv_request_alarm_title_dialog.text = completeContext
        tv_des_request_custom_dialog.text = mContent
        tv2_des_request_custom_dialog.text = eContent
        tv_complete_request_custom_dialog.text = responseText


        if(mResponseClickListener != null){
            btn_bottom_box_search_custom_dialog.setOnClickListener(mResponseClickListener)
        }
    }


}

