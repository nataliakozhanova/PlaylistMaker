package com.practicum.playlistmaker.sharing.domain.impl

import android.content.Intent
import com.practicum.playlistmaker.sharing.domain.api.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.models.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val linkApp : String,
    private val linkTerms : String,
    private val emailData : EmailData
) : SharingInteractor {
    override fun shareApp() : Intent {
        return externalNavigator.shareLink(linkApp)
    }

    override fun openTerms() : Intent {
        return externalNavigator.openLink(linkTerms)
    }

    override fun openSupport() : Intent {
        return externalNavigator.openEmail(emailData)
    }
}