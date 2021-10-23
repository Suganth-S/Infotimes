package com.suganth.infotimes.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.suganth.infotimes.NewsApplication
import com.suganth.infotimes.repository.NewsRepository

class NewsViewModelProviderFactory(val app: NewsApplication, val newsRepository: NewsRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NewsViewModel(app,newsRepository) as T
    }
}