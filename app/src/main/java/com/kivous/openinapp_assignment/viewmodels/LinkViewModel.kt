package com.kivous.openinapp_assignment.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kivous.openinapp_assignment.models.LinkInfoModel
import com.kivous.openinapp_assignment.repositories.LinkRepository
import com.kivous.openinapp_assignment.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class LinkViewModel
@Inject constructor(
    app: Application,
    private val linkRepository: LinkRepository, private val tokenManager: TokenManager
) : AndroidViewModel(app) {

    var linkInfo = MutableLiveData<LinkInfoModel>()
    var greeting = MutableLiveData<String>()
    var totalClick = MutableLiveData<String>()
    var todayClick = MutableLiveData<String>()
    var totalLinks = MutableLiveData<String>()
    var topLocation = MutableLiveData<String>()
    var topSource = MutableLiveData<String>()

    fun fetchLinkInfo() = viewModelScope.launch {
        val response =
            linkRepository.getLinkInfo(tokenManager.getToken().toString())

        linkInfo.value = response.body()

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

    }


    fun displayGreeting() {
        val date = Date()
        val cal: Calendar = Calendar.getInstance()
        cal.time = date
        val hour: Int = cal.get(Calendar.HOUR_OF_DAY)
        greeting.value = when (hour) {
            in 12..16 -> {
                "Good Afternoon"
            }

            in 17..20 -> {
                "Good Evening"
            }

            in 21..23 -> {
                "Good Night"
            }

            else -> {
                "Good Morning"
            }
        }

    }


}