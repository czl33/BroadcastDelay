package com.newczl.broadcastdelay

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar

class MainActivity : AppCompatActivity(), OnRangeChangedListener {
    companion object{
        const val TAG = "MainActivity"
    }
    private val edit by lazy { findViewById<EditText>(R.id.input_data) }
    private val button by lazy { findViewById<Button>(R.id.button) }
    private val text by lazy { findViewById<TextView>(R.id.text) }
    private val range by lazy { findViewById<RangeSeekBar>(R.id.range_seek_bar) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        range.setRange(1f,30f)
        range.setProgress(Status.max.toFloat())
        range.setIndicatorTextDecimalFormat("0")
        range.setIndicatorTextStringFormat("%s分钟");
        range.setOnRangeChangedListener(this)
        button.setOnClickListener {
            sendBroadCast(edit.text.toString())
        }
        refreshView()
    }

    private fun refreshView() {
        if(Status.data.isNotEmpty()){
            text.setText("当前事件:${Status.data}")
        }
    }


    private fun sendBroadCast(string:String){
        val index = string.indexOf("data", ignoreCase = true) + 5
        Log.i(TAG,"index :$index")
        if(index!=-1 && (string.length - index) >= 5){
            Status.data = string.substring(index)
            Toast.makeText(this,"保存成功！",Toast.LENGTH_LONG).show()
            refreshView()
            finish();
        }else{
            Toast.makeText(this,"输入文本有误！",Toast.LENGTH_LONG).show()
            refreshView()
        }
//        Status.data = string
    }

    override fun onRangeChanged(
        view: RangeSeekBar,
        leftValue: Float,
        rightValue: Float,
        isFromUser: Boolean
    ) {
        if(isFromUser){
            Log.i(TAG,"leftValue:$leftValue")
        }

    }

    override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {

    }

    override fun onStopTrackingTouch(view: RangeSeekBar, isLeft: Boolean) {
        Status.max = view.rangeSeekBarState[0].value.toInt()
        Toast.makeText(this,"保存随机数成功！",Toast.LENGTH_SHORT).show()
    }
}