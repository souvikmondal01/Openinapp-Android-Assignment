package com.kivous.openinapp_assignment.utils

import android.app.Application
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
object Utils {
    // used to convert 2023-03-15T07:33:50.000Z to 15 March 2023
    fun convertTimestampToDateString(timestamp: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

        val date = inputFormat.parse(timestamp)

        return outputFormat.format(date!!)
    }

    // used to convert "2023-06-01" to "01 Jun"
    fun formatDate(dateStr: String): String {
        val date = LocalDate.parse(dateStr)
        val formatter = DateTimeFormatter.ofPattern("dd MMM", Locale.ENGLISH)
        return date.format(formatter)
    }

    fun convertObjectToHashmap(response: String): HashMap<String, Int> {
        val hashMap = HashMap<String, Int>()

        val pairs = response.trim().removeSurrounding("OverallUrlChart(", ")").split(", ")

        for (pair in pairs) {
            val (dateStr, valueStr) = pair.split("=")
            hashMap[formatDate(dateStr)] = valueStr.toInt()
        }

        return hashMap
    }

    // sort dates when "01 Nov" in format
    fun sortDates(dateList: List<String>): List<String> {
        val dateFormat = SimpleDateFormat("dd MMM", Locale.ENGLISH)

        val sortedDates = dateList.sortedBy { dateList ->
            dateFormat.parse(dateList)
        }
        val dateList: ArrayList<String> = arrayListOf()
        sortedDates.forEach {

            dateList.add(it)

        }
        return dateList

    }

    fun Fragment.toast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

}