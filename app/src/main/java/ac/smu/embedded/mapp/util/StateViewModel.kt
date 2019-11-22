package ac.smu.embedded.mapp.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class StateViewModel : ViewModel() {

    protected fun <T> useState(value: T? = null): LiveData<T> {
        return if (value != null) MutableLiveData(value)
        else MutableLiveData()
    }

    protected fun <T> setState(liveData: LiveData<T>, value: T) {
        (liveData as MutableLiveData).value = value
    }
}