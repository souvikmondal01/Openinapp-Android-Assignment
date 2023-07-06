package com.kivous.openinapp_assignment.models

data class LinkInfo(
    val app: String,
    val created_at: String,
    val original_image: String,
    val smart_link: String,
    val total_clicks: Int
)
