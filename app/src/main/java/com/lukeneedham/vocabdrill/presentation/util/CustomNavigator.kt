package com.lukeneedham.vocabdrill.presentation.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.FragmentManager

fun FragmentManager.autoTarget() {
    fragmentFactory = TargetFragmentFactory { primaryNavigationFragment }
}

class TargetFragmentFactory(private val getCurrentFragment: () -> Fragment?) : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        val fragment = super.instantiate(classLoader, className)
        val currentFragment = getCurrentFragment()
        fragment.setTargetFragment(currentFragment, REQUEST_CODE)
        return fragment
    }

    companion object {
        const val REQUEST_CODE = 0
    }
}
