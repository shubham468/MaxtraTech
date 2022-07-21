package com.tech.maxtratech

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tech.umr.Repo.Repository

class MainViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            MainViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("Main ViewModel Not Found")
        }
    }
}