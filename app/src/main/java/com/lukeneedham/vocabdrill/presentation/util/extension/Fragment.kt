package com.lukeneedham.vocabdrill.presentation.util.extension

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.annotation.IdRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.findNavController
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.util.extension.TAG

/** Sets the target fragment to this, and shows */
fun Fragment.showDialog(dialog: DialogFragment) {
    dialog.setTargetFragment(this, 0)
    dialog.show(parentFragmentManager, dialog.TAG)
}

/**
 * Navigates only if this is safely possible; when this Fragment is still the current destination.
 */
fun Fragment.navigateSafe(
    @IdRes resId: Int,
    args: Bundle? = null,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) {
    if (mayNavigate()) findNavController().navigate(
        resId, args,
        navOptions, navigatorExtras
    )
}

/**
 * Navigates only if this is safely possible; when this Fragment is still the current destination.
 */
fun Fragment.navigateSafe(
    deepLink: Uri,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) {
    if (mayNavigate()) findNavController().navigate(deepLink, navOptions, navigatorExtras)
}

/**
 * Navigates only if this is safely possible; when this Fragment is still the current destination.
 */
fun Fragment.navigateSafe(directions: NavDirections, navOptions: NavOptions? = null) {
    if (mayNavigate()) findNavController().navigate(directions, navOptions)
}

/**
 * Navigates only if this is safely possible; when this Fragment is still the current destination.
 */
fun Fragment.navigateSafe(
    directions: NavDirections,
    navigatorExtras: Navigator.Extras
) {
    if (mayNavigate()) findNavController().navigate(directions, navigatorExtras)
}

fun Fragment.popBackStackSafe() { if (mayNavigate()) findNavController().popBackStack() }

/**
 * Returns true if the navigation controller is still pointing at 'this' fragment, or false if it already navigated away.
 */
fun Fragment.mayNavigate(): Boolean {

    val navController = findNavController()
    val destinationIdInNavController = navController.currentDestination?.id

    // add tag_navigation_destination_id to your ids.xml so that it's unique:
    val destinationIdOfThisFragment = view?.getTag(R.id.tag_navigation_destination_id) ?: destinationIdInNavController

    // check that the navigation graph is still in 'this' fragment, if not then the app already navigated:
    if (destinationIdInNavController == destinationIdOfThisFragment) {
        view?.setTag(R.id.tag_navigation_destination_id, destinationIdOfThisFragment)
        return true
    } else {
        Log.d("FragmentExtensions", "May not navigate: current destination is not the current fragment.")
        return false
    }
}
