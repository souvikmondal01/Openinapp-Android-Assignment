package com.kivous.openinapp_assignment.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kivous.openinapp_assignment.container.BaseApplication
import com.kivous.openinapp_assignment.models.LinkInfoModel
import com.kivous.openinapp_assignment.repositories.LinkRepository
import com.kivous.openinapp_assignment.utils.Resource
import com.kivous.openinapp_assignment.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.IOException
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class LinkViewModel
@Inject constructor(
    app: Application,
    private val linkRepository: LinkRepository, private val tokenManager: TokenManager
) : AndroidViewModel(app) {
    var linkInfo = MutableLiveData<Resource<LinkInfoModel>>()
    var greeting = MutableLiveData<String>()
    var totalClick = MutableLiveData<String>()
    var todayClick = MutableLiveData<String>()
    var totalLinks = MutableLiveData<String>()
    var topLocation = MutableLiveData<String>()
    var topSource = MutableLiveData<String>()

    fun fetchLinkInfo() = viewModelScope.launch {
        linkInfo.value = Resource.Loading()
        try {
            if (hasInternetConnection()) {
                val response =
                    linkRepository.getLinkInfo(tokenManager.getToken().toString())
                linkInfo.value = response.body()?.let { Resource.Success(it) }

                totalClick.value = response.body()?.total_clicks.toString()
                todayClick.value = response.body()?.today_clicks.toString()
                totalLinks.value = response.body()?.total_links.toString()
                if (response.body()?.top_location.toString() == "") {
                    topLocation.value = "N/A"
                } else {
                    topLocation.value = response.body()?.top_location.toString()
                }

                if (response.body()?.top_source.toString() == "") {
                    topSource.value = "N/A"
                } else {
                    topSource.value = response.body()?.top_source.toString()
                }
            } else {
                linkInfo.value =
                    Resource.Error("Your device is not connected with internet, please connect.")
            }

        } catch (t: Throwable) {
            when (t) {
                is IOException -> linkInfo.value = Resource.Error("Network Failure")
                else -> linkInfo.value = Resource.Error("Conversion Error")
            }
        }

    }

    fun displayGreeting() {
        val date = Date()
        val cal: Calendar = Calendar.getInstance()
        cal.time = date
        val hour: Int = cal.get(Calendar.HOUR_OF_DAY)
        greeting.value = when (hour) {
            in 12..16 -> { "Good Afternoon" }
            in 17..20 -> { "Good Evening" }
            in 21..23 -> { "Good Night" }
            else -> { "Good Morning" }
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<BaseApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities =
            connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }


}