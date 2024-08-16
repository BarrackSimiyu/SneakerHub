package com.example.sneakerhub

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class CartBroadcastReceiver(private val callback: () -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        callback()
    }
}
