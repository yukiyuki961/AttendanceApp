package com.valjapan.kintai.adapter

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import com.valjapan.kintai.R
import com.valjapan.kintai.activity.MainActivity
import com.valjapan.kintai.activity.WorkDataDetailActivity
import io.realm.Realm
import java.text.SimpleDateFormat
import java.util.*

class TaskAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // SDKバージョンが26以上の場合、チャネルを設定する必要がある
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                "default",
                "退勤忘れを通知します",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Channel description"
            notificationManager.createNotificationChannel(channel)
        }

        // 通知の設定を行う
        val builder = NotificationCompat.Builder(context, "default")
        builder.setSmallIcon(R.mipmap.ic_launcher_foreground)
        builder.setLargeIcon(
            BitmapFactory.decodeResource(
                context.resources,
                R.mipmap.ic_launcher_foreground
            )
        )
        builder.setWhen(System.currentTimeMillis())
        builder.setDefaults(Notification.DEFAULT_ALL)
        builder.setAutoCancel(true)

        // EXTRA_TASKからTaskのidを取得して、idからTaskのインスタンスを取得する
        intent.getIntExtra(MainActivity.EXTRA_TASK, -1)
        val realm = Realm.getDefaultInstance()
        val task = realm?.where(WorkData::class.java)?.isNull("finishTime")?.findFirst()


        val identifier = if (realm?.where(WorkData::class.java)?.findAll()?.size != null) {
            realm!!.where(WorkData::class.java)!!.findAll()!!.size
        } else {
            0
        }

        val hour = SimpleDateFormat("HH:mm", Locale.JAPANESE)

        task?.let {
            // タスクの情報を設定する
            builder.setTicker("退勤時間の確認") // 5.0以降は表示されない
            builder.setContentTitle("お疲れ様です！")
            builder.setContentText("${hour.format(task.startTime)}開始の勤務はもう終わりましたか？")
            val stackBuilder = TaskStackBuilder.create(context)
            // 通知をタップしたらアプリを起動するようにする
            stackBuilder.addParentStack(MainActivity::class.java)
            stackBuilder.addNextIntent(Intent(context, MainActivity::class.java))
            stackBuilder.addNextIntent(
                Intent(context, WorkDataDetailActivity::class.java).putExtra(
                    "id",
                    it.id
                )
            )
            val pendingIntent =
                stackBuilder.getPendingIntent(identifier, PendingIntent.FLAG_UPDATE_CURRENT)
            builder.setContentIntent(pendingIntent)
            // 通知を表示する
            notificationManager.notify(
                identifier,
                builder.build()
            )
        }
        realm.close()
    }
}
