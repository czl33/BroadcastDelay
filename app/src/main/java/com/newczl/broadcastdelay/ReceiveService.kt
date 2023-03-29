package com.newczl.broadcastdelay

import android.R
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.text.format.DateUtils
import android.util.Log
import androidx.core.app.NotificationCompat
import java.util.*


/**
 *
 * @Author Czl
 * @Date 2023/3/29-11:58
 * @Email chenzhouliang@twh.com.cn
 */
class ReceiveService : Service() {
    companion object {
        const val SEND_BROADCAST = 0x1001;
        const val UPDATE_BROADCAST = 0x1002;
        const val TAG = "ReceiveService";
    }

    private val handler = Handler(Looper.getMainLooper()) {
        if (it.what == ReceiveActivity.SEND_BROADCAST) {
            sendBroadCast(Status.data)
            stopForeground(true)
            stopSelf()
        } else if (it.what == UPDATE_BROADCAST) {
            currentTimeLong -= it.arg1
            updateState()
        }
        return@Handler false
    }

    private val notificationManager: NotificationManager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
    private var mBuilder: NotificationCompat.Builder? = null
    private var currentTimeLong = -1L

    override fun onCreate() {
        super.onCreate()
        createNotification(this, "延时服务正常运行中", "延时服务正常运行中...")
        val random = Random().nextInt(Status.max + 1)
        sendUpdateTime(5000)
        if (Status.data.isNotEmpty()) {
            currentTimeLong = random * getTimeType(Status.type)
            handler.sendEmptyMessageDelayed(SEND_BROADCAST, currentTimeLong);
        } else {
            stopForeground(true)
            stopSelf()
        }

    }


    private fun sendUpdateTime(time: Int) {
        val obtainMessage = handler.obtainMessage()
        obtainMessage.what = UPDATE_BROADCAST
        obtainMessage.arg1 = time
        handler.sendMessageDelayed(obtainMessage, time.toLong())
    }

    private fun createNotification(context: Context, title: String, text: String) {
        val isSilent = true //是否静音
        val isOngoing = true //是否持续(为不消失的常驻通知)
        val channelName = "服务常驻通知"
        val channelId = "Service_Id"
        val category: String = Notification.CATEGORY_SERVICE
        val nfIntent = Intent(context, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(context, 0, nfIntent, PendingIntent.FLAG_IMMUTABLE)
        mBuilder = NotificationCompat.Builder(context, channelId)
            .setContentIntent(pendingIntent) //设置PendingIntent
            .setSmallIcon(R.mipmap.sym_def_app_icon) //设置状态栏内的小图标
            .setContentTitle(title) //设置标题
//            .setContentIntent(getPendingIntent())
            .setContentText(text) //设置内容
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) //设置通知公开可见
            .setOngoing(isOngoing) //设置持续(不消失的常驻通知)
            .setCategory(category) //设置类别
            .setPriority(NotificationCompat.PRIORITY_MAX) //优先级为：重要通知
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //安卓8.0以上系统要求通知设置Channel,否则会报错
            val notificationChannel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC //锁屏显示通知
            notificationManager.createNotificationChannel(notificationChannel)
            mBuilder?.setChannelId(channelId)
        }
        mBuilder?.let {
            startForeground(1, it.build());//创建一个通知，创建通知前记得获取开启通知权限
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private fun updateState() {
        mBuilder?.let {
            val lastTime = currentTimeLong / 1000
            if (lastTime <= 2) {
                it.setContentTitle("延时服务正常运行中");
                it.setContentText("即将运行！！！！！！！");
                notificationManager.notify(1, it.build());
                return
            }
            it.setContentTitle("延时服务正常运行中");
            it.setContentText("距离运行,还剩余${lastTime}秒,点击取消运行！");
            notificationManager.notify(1, it.build());
            sendUpdateTime(1000)
        }
    }

    private fun getTimeType(type: Int? = 0): Long {
        return when (type) {
            0 -> {
                DateUtils.MINUTE_IN_MILLIS
            }
            1 -> {
                DateUtils.SECOND_IN_MILLIS
            }
            else -> {
                DateUtils.HOUR_IN_MILLIS
            }
        }
    }

    private fun sendBroadCast(string: String) {
        Log.i(ReceiveActivity.TAG, "sendBroadCast :$string")
        val intent = Intent("com.jozein.xedgepro.PERFORM")
        intent.putExtra("data", string)
        this.sendBroadcast(intent)
    }

//    private fun getPendingIntent():PendingIntent{
//       return PendingIntent.getBroadcast(this);
//    }
}