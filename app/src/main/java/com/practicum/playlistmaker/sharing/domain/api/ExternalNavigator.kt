package com.practicum.playlistmaker.sharing.domain.api

import android.content.Intent
import com.practicum.playlistmaker.sharing.domain.models.EmailData

interface ExternalNavigator {
    fun shareLink(link :String): Intent
    fun openLink(link: String): Intent
    fun openEmail(emailData : EmailData): Intent
}