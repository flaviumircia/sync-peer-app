package com.syncpeer.syncpeerapp.onboarding.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.viewpager2.widget.ViewPager2

class OnboardingScreenActivityViewModel: ViewModel() {

    private val _progress = MutableLiveData<Int>()
    private lateinit var viewPager: ViewPager2
    val progress : LiveData<Int> = _progress

    init {
        _progress.value = 25
    }

    fun setViewPager(viewPager2: ViewPager2){
        viewPager = viewPager2
    }
    fun incrementProgress(numberOfFragments: Int){
        val currentValue = _progress.value?: 25;
        _progress.value = currentValue + 100 / numberOfFragments
    }

    fun decrementProgress(numberOfFragments: Int) {
        val currentValue = _progress.value?: 25;
        _progress.value = currentValue - 100 / numberOfFragments
    }

    fun changeFragmentOnButtonClick(){
        if(viewPager.currentItem < (viewPager.adapter?.itemCount?.minus(1) ?: 0))
            viewPager.currentItem++
    }

}