package com.syncpeer.syncpeerapp.onboarding

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.syncpeer.syncpeerapp.R
import com.syncpeer.syncpeerapp.onboarding.adapters.ViewPagerAdapter
import com.syncpeer.syncpeerapp.onboarding.adapters.setButtonIcon
import com.syncpeer.syncpeerapp.onboarding.adapters.setVisibility
import com.syncpeer.syncpeerapp.onboarding.fragments.OnboardingConnectFragment
import com.syncpeer.syncpeerapp.onboarding.fragments.OnboardingCreateAccountFragment
import com.syncpeer.syncpeerapp.onboarding.fragments.OnboardingSaveForLaterFragment
import com.syncpeer.syncpeerapp.onboarding.fragments.OnboardingScreenStartFragment
import com.syncpeer.syncpeerapp.onboarding.viewmodels.OnboardingScreenActivityViewModel
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator

class OnboardingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding_screen_start)

        // binding to views
        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        val wormDotsIndicator = findViewById<WormDotsIndicator>(R.id.worm_dots_indicator)
        val circularProgressIndicator =
            findViewById<CircularProgressIndicator>(R.id.circular_progress)
        // ViewModels
        val pagerAdapter = ViewPagerAdapter(this)
        val viewModel = ViewModelProvider(this)[OnboardingScreenActivityViewModel::class.java]

        // Logic
        pagerAdapter.addFragment(OnboardingScreenStartFragment())
        pagerAdapter.addFragment(OnboardingCreateAccountFragment())
        pagerAdapter.addFragment(OnboardingConnectFragment())
        pagerAdapter.addFragment(OnboardingSaveForLaterFragment())

        viewPager.adapter = pagerAdapter

        wormDotsIndicator.attachTo(viewPager)

        viewModel.progress.observe(this) { newValue ->
            animateProgressOfCircularProgressIndicator(circularProgressIndicator, newValue)
        }
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            private var currentPage = 0
            private val numberOfFragments = pagerAdapter.itemCount

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                isPageStateChanged(viewModel, position, currentPage, numberOfFragments)
                if (position==numberOfFragments-1){
                    setVisibility(findViewById(R.id.goText),false)
                    setButtonIcon(findViewById(R.id.goButton),R.drawable.check_icon)
                }
                else{
                    setButtonIcon(findViewById(R.id.goButton),0)
                    setVisibility(findViewById(R.id.goText),true)}
                currentPage = position
            }
        })


    }

    private fun animateProgressOfCircularProgressIndicator(
        circularProgressIndicator: CircularProgressIndicator,
        newValue: Int
    ) {

        val animator = ObjectAnimator.ofInt(circularProgressIndicator, "progress", newValue)
        animator.duration = 500
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()
    }

    private fun isPageStateChanged(
        viewModel: OnboardingScreenActivityViewModel,
        position: Int,
        currentPage: Int,
        numberOfFragments: Int
    ) {
        if (position > currentPage) {
            viewModel.incrementProgress(numberOfFragments)
        } else if (position < currentPage) {
            viewModel.decrementProgress(numberOfFragments)
        }
    }
}