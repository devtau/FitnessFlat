package com.devtau.ironHeroes.enums

import android.content.ContentResolver
import android.net.Uri
import com.devtau.ironHeroes.BuildConfig
import com.devtau.ironHeroes.R

enum class ChannelStats(
        val id: String,
        val channelName: String,
        val description: String,
        val soundResId: Int?,
        val withVibration: Boolean
) {
    DEFAULT_SOUND(
            "ironHeroes_channel_id_1",
            "ironHeroesNotificationChannel 1",
            "channel for Iron Heroes notifications with default sound",
            null, true),
    CUSTOM_SOUND(
            "ironHeroes_channel_id_2",
            "ironHeroesNotificationChannel 2",
            "channel for Iron Heroes notifications with custom sound",
            R.raw.push_sound, true);


    companion object {
        fun getCustomNotificationSound(): Uri = Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://" +
                "${BuildConfig.APPLICATION_ID}/${ChannelStats.CUSTOM_SOUND.soundResId}")
    }
}