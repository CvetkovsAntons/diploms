package com.example.fitnessapp.Fragments

import android.app.Dialog
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.fitnessapp.R
import kotlinx.android.synthetic.main.activity_timer.*
import kotlinx.android.synthetic.main.fragment_timer_dialog.*
import kotlinx.android.synthetic.main.fragment_timer_dialog.timer_add
import kotlinx.android.synthetic.main.fragment_timer_dialog.timer_skip
import java.util.*
import java.util.concurrent.TimeUnit

class TimerDialogFragment : DialogFragment() {

    private lateinit var textView: TextView
    private lateinit var timer: CountDownTimer
    private lateinit var sDuration: String
    private var timeLeft : Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_timer_dialog, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        textView = view.findViewById(R.id.timer)

        val duration = TimeUnit.MINUTES.toMillis(1)

        timer_skip.setOnClickListener {
            timer.cancel()
            dismiss()
        }

        timer_add.setOnClickListener {
            timer.cancel()
            timeLeft += 5000
            startTimer(timeLeft)
        }

        startTimer(duration)
    }

    private fun startTimer(duration: Long) {
        timer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                sDuration = String.format(
                    Locale.ENGLISH, "%02d : %02d",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)))

                timeLeft = millisUntilFinished

                textView.text = sDuration
            }

            override fun onFinish() {
                timer.cancel()
                dismiss()
            }
        }.start()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        return dialog
    }
}