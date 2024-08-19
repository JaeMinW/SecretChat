package com.example.dday

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val startButton = findViewById<Button>(R.id.startBtn)
        val endButton = findViewById<Button>(R.id.endBtn)

        var startDate = ""
        var endDate = ""

        val calendar_start = Calendar.getInstance()
        val calendar_end = Calendar.getInstance()

        startButton.setOnClickListener{

            val today = GregorianCalendar()
            val year = today.get(Calendar.YEAR)
            val month = today.get(Calendar.MONTH)
            val day = today.get(Calendar.DATE)

            val dlg = DatePickerDialog(this, object :  DatePickerDialog.OnDateSetListener{
                override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayofMonth: Int) {

                    //startDate = "${year} + ${month} + ${dayofMonth}"
                    startDate = year.toString() + (month+1).toString() + dayofMonth.toString()
                    Log.d("day", "day : " + startDate)

                    calendar_start.set(year, month+1, dayofMonth)

                }

            },year, month, day)
            dlg.show()
        }

        endButton.setOnClickListener{

            val today = GregorianCalendar()
            val year = today.get(Calendar.YEAR)
            val month = today.get(Calendar.MONTH)
            val day = today.get(Calendar.DATE)

            val dlg = DatePickerDialog(this, object :  DatePickerDialog.OnDateSetListener{
                override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayofMonth: Int) {

                    //startDate = "${year} + ${month} + ${dayofMonth}"
                    endDate = year.toString() + (month+1).toString() + dayofMonth.toString()
                    Log.d("day", "day : " + endDate)

                    calendar_end.set(year, month+1, dayofMonth)

                    //findViewById<TextView>(R.id.finalDate).setText((endDate.toInt() - startDate.toInt() + 1).toString())

                    val finalDate = TimeUnit.MILLISECONDS.toDays(calendar_end.timeInMillis - calendar_start.timeInMillis)

                    val textArea = findViewById<TextView>(R.id.textArea)

                    textArea.setText((finalDate+2).toString())
                }

            },year, month, day)
            dlg.show()
        }

    }
}