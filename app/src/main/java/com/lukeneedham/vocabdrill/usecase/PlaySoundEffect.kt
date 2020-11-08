package com.lukeneedham.vocabdrill.usecase

import android.content.Context
import android.media.SoundPool
import com.lukeneedham.vocabdrill.presentation.audio.SoundEffect

class PlaySoundEffect(
    context: Context
) {
    private val soundPool = SoundPool.Builder().setMaxStreams(1).build()

    private val effectToAudioIdMap = SoundEffect.values().map {
        it to soundPool.load(context, it.rawResId, 1)
    }.toMap()

    operator fun invoke(effect: SoundEffect) {
        val soundId = effectToAudioIdMap.getValue(effect)
        soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
    }
}
