package ac.smu.embedded.findingtaste.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    private val _toastText = MutableLiveData<String>()
    val toastText: LiveData<String> = _toastText

    fun showToast(text: String) {
        _toastText.value = text
    }
}