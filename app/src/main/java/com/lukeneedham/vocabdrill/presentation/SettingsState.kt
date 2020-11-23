package com.lukeneedham.vocabdrill.presentation

sealed class SettingsState {
    /** No work is happening, ready for user input */
    object Ready : SettingsState()

    /** Working is in progress, disable user input */
    object Working : SettingsState()

    /** This language no longer exists. Quit */
    object Invalid : SettingsState()
}
