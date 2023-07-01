package com.kivous.openinapp_assignment.repositories

import com.kivous.openinapp_assignment.network.LinkAPI
import javax.inject.Inject

class LinkRepository @Inject constructor(private val api: LinkAPI) {

    suspend fun getLinkInfo(token: String) = api.getLinkInfo(token)

}