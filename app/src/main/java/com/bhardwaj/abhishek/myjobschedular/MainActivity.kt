package com.bhardwaj.abhishek.myjobschedular

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var jobScheduler: JobScheduler? = null
    private val JOB_ID = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val seekBarProgress =
            findViewById<View>(R.id.seekBarLabel) as TextView
        seekBar!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (i > 0) {
                    seekBarProgress.text = "$i s"
                } else {
                    seekBarProgress.setText(R.string.notset)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            jobScheduler =
                getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        }
        val bSchedule =
            findViewById<View>(R.id.scheduleButton) as Button
        val bCancelJobs =
            findViewById<View>(R.id.cancelJobButton) as Button
        bSchedule.setOnClickListener { scheduleJob() }
        bCancelJobs.setOnClickListener { cancelJobs() }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun scheduleJob() {
        val networkOptions = findViewById<RadioGroup>(R.id.networkOptions)
        var selectedNetworkId = networkOptions.checkedRadioButtonId
        var selectedOption = JobInfo.NETWORK_TYPE_NONE
        val seekBarInteger = seekBar!!.progress
        val seekBarSet = seekBarInteger > 0
        when (selectedNetworkId) {
            R.id.noNetwork -> selectedOption = JobInfo.NETWORK_TYPE_NONE
            R.id.anyNetwork -> selectedOption = JobInfo.NETWORK_TYPE_ANY
            R.id.wifiNetwork -> selectedNetworkId = JobInfo.NETWORK_TYPE_UNMETERED
        }
        val serviceName = ComponentName(packageName,MyJobSchedulerService::class.java.name)
        var builder = JobInfo.Builder(JOB_ID, serviceName)
        .setRequiredNetworkType(selectedOption)
        .setRequiresDeviceIdle(idleSwitch!!.isChecked)
        .setRequiresCharging(chargingSwitch!!.isChecked())
        if (seekBarSet) {
            builder.setOverrideDeadline(seekBarInteger * 1000.toLong())
        }
        val constraintSet = (selectedOption != JobInfo.NETWORK_TYPE_NONE
                || chargingSwitch!!.isChecked() || idleSwitch!!.isChecked || seekBarSet)
        if (constraintSet) {
            val jobInfo:JobInfo = builder.build()
            jobScheduler!!.schedule(jobInfo)
            Toast.makeText(
                this,
                "Job Scheduled, job will run" + "the constraint are met",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(this, "Please set at least one constraint", Toast.LENGTH_SHORT).show()
        }
    }

    fun cancelJobs() {
        if (jobScheduler != null) {
            jobScheduler!!.cancelAll()
            jobScheduler = null
            Toast.makeText(this, "Jobs cancelled", Toast.LENGTH_SHORT).show()
        }
    }


}
