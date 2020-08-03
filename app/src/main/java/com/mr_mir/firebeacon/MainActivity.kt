package com.mr_mir.firebeacon

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener,
    OnCompleteListener<InstanceIdResult> {

    private var context: Context = this
    private var token: String = ""
    private var notificationManager: NotificationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //FCM Token
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(this)

        createChannel()
        tvSend.setOnClickListener(this)

    }

    @SuppressLint("SetTextI18n")
    override fun onComplete(task: Task<InstanceIdResult>) {
        if(task.isSuccessful) {
            token = task.result?.token.toString()
//            tvSend.visibility = VISIBLE
            tvTokenMsg.text = "Successfully Token Received"
        } else {
//            tvSend.visibility = GONE
            tvTokenMsg.text = "Failed: ${task.exception?.message.toString()}"
        }
    }


    override fun onClick(v: View?) {
        when(v?.id){
            R.id.tvSend -> showNotification()
        }
    }

    //Create Notification Channel
    private fun createChannel() {
        //Notification Channel for Android Oreo (8.0 / API 26)+
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel: NotificationChannel =
                NotificationChannel(NotificationUtils.CHANNEL_ID, NotificationUtils.CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = NotificationUtils.CHANNEL_DESC
            notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        } else {
            notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
    }


    //Displays Notification
    private fun showNotification() {


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            //Notification Builder
            val builder: NotificationCompat.Builder =
                NotificationCompat.Builder(context, NotificationUtils.CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_insta_comment)
                    .setContentTitle("Hi !!!")
                    .setContentText(edittext.text.toString())
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            val notificationManagerCompat: NotificationManagerCompat
                    = NotificationManagerCompat.from(context)

            notificationManagerCompat.notify(1, builder.build())
        }

        else {


            val resultIntent = Intent(this, MainActivity::class.java)
            val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(this)
            stackBuilder.addParentStack(MainActivity::class.java)

            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent)
            val resultPendingIntent: PendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

            //Notification Builder
            val builder: NotificationCompat.Builder =
                NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_insta_comment)
                    .setContentTitle("Hi !!!")
                    .setContentText(edittext.text.toString())
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(resultPendingIntent)

            notificationManager?.notify(1, builder.build())
        }
    }
}