package com.lukeneedham.vocabdrill.presentation.feature.vocabgroup.settings.changename

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.lukeneedham.vocabdrill.presentation.util.DisposingViewModel
import com.lukeneedham.vocabdrill.presentation.util.extension.toLiveData
import com.lukeneedham.vocabdrill.repository.VocabGroupRepository
import com.lukeneedham.vocabdrill.usecase.CheckValidVocabGroupName
import com.lukeneedham.vocabdrill.util.RxSchedulers
import com.lukeneedham.vocabdrill.util.extension.TAG
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign

class ChangeVocabGroupNameViewModel(
    val vocabGroupId: Long,
    private val vocabGroupRepository: VocabGroupRepository,
    private val checkValidVocabGroupName: CheckValidVocabGroupName
) : DisposingViewModel() {

    private val loadVocabGroupSingle = vocabGroupRepository.requireVocabGroupForId(vocabGroupId)
        .subscribeOn(RxSchedulers.database)
        .observeOn(RxSchedulers.main)

    private var name: String? = null

    private var checkValidityDisposable: Disposable? = null

    private val isValidNameMutableLiveData = MutableLiveData<Boolean>(false)
    val isValidNameLiveData = isValidNameMutableLiveData.toLiveData()

    private val vocabGroupMutableLiveData = MutableLiveData<VocabGroup>()
    val vocabGroupLiveData = vocabGroupMutableLiveData.toLiveData()

    init {
        disposables += loadVocabGroupSingle.subscribe { vocabGroup ->
            vocabGroupMutableLiveData.value = vocabGroup

            val name = name
            if (name != null) {
                checkValidity(name)
            }
        }
    }

    fun onNameChange(name: String) {
        this.name = name
        checkValidity(name)
    }

    @SuppressLint("CheckResult")
    fun updateLanguage() {
        val name = name
        if (name == null) {
            Log.e(TAG, "Cannot update language - name is null")
            return
        }

        fun update(vocabGroup: VocabGroup) {
            val newGroup = vocabGroup.copy(name = name)
            vocabGroupRepository.updateVocabGroup(newGroup)
                .subscribeOn(RxSchedulers.database)
                .observeOn(RxSchedulers.main)
                .subscribe()
        }

        val loadedGroup = vocabGroupLiveData.value
        if (loadedGroup != null) {
            update(loadedGroup)
        } else {
            loadVocabGroupSingle.subscribe { language ->
                update(language)
            }
        }
    }

    private fun checkValidity(name: String) {
        checkValidityDisposable?.dispose()

        if (name.isBlank()) {
            isValidNameMutableLiveData.value = false
            return
        }

        val loadedGroup = vocabGroupLiveData.value
        if (loadedGroup == null) {
            // We don't have the vocab group, so we can't check it's validity yet
            isValidNameMutableLiveData.value = false
            return
        }
        val disposable =
            checkValidVocabGroupName(loadedGroup.languageId, name).subscribe { isValid ->
                isValidNameMutableLiveData.value = isValid
            }
        checkValidityDisposable = disposable
        disposables += disposable
    }
}
