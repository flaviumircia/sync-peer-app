package com.syncpeer.syncpeerapp.onboarding.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OnboardingScreenActivityViewModel: ViewModel() {

    private val _progress = MutableLiveData<Int>()
    val progress : LiveData<Int> = _progress

    init {
        _progress.value = 0
    }

    fun incrementProgress(numberOfFragments: Int){
        val currentValue = _progress.value?: 0;
        _progress.value = currentValue + 100 / numberOfFragments
    }

    fun decrementProgress(numberOfFragments: Int) {
        val currentValue = _progress.value?: 0;
        _progress.value = currentValue - 100 / numberOfFragments
    }

}