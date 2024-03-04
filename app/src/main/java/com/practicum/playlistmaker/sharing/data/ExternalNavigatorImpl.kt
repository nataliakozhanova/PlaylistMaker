package com.practicum.playlistmaker.sharing.data

import android.content.Intent
import android.net.Uri
import com.practicum.playlistmaker.sharing.domain.models.EmailData
import com.practicum.playlistmaker.sharing.domain.api.ExternalNavigator

class ExternalNavigatorImpl() : ExternalNavigator {

    override fun shareLink(link: String) : Intent {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.setType("text/plain")
        shareIntent.putExtra(Intent.EXTRA_TEXT, link)
        return (Intent.createChooser(shareIntent, ""))
    }

    override fun openLink(link: String) : Intent{
        val browseIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        return browseIntent
    }

    override fun openEmail(emailData: EmailData) : Intent {
        val writeIntent = Intent(Intent.ACTION_SENDTO)
        writeIntent.data = Uri.parse("mailto:" + emailData.email)
        writeIntent.putExtra(Intent.EXTRA_SUBJECT, emailData.subject)
        writeIntent.putExtra(Intent.EXTRA_TEXT, emailData.text)
        return writeIntent
    }
}