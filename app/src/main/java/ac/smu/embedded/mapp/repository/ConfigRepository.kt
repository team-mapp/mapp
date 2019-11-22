package ac.smu.embedded.mapp.repository

import ac.smu.embedded.mapp.util.remoteConfigs
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.orhanobut.logger.Logger

interface ConfigLoaderRepository {

    fun getDataUpdatedObserver(): LiveData<Long>

    fun getBoolean(key: String): Boolean

    fun getDouble(key: String): Double

    fun getLong(key: String): Long

    fun getString(key: String): String

}

interface ConfigWriterRepository : ConfigLoaderRepository {

    fun writeBoolean(key: String, value: Boolean)

    fun writeDouble(key: String, value: Double)

    fun writeLong(key: String, value: Long)

    fun writeString(key: String, value: String)

}

class FirebaseConfigRepository(
    private val remoteConfig: FirebaseRemoteConfig
) : ConfigLoaderRepository {

    private val updatedObserver = MutableLiveData<Long>(System.currentTimeMillis())

    init {
        Logger.d("FirebaseConfigRepository initialized")
        remoteConfig.setDefaultsAsync(remoteConfigs)
        remoteConfig.fetchAndActivate().addOnCompleteListener {
            if (it.isSuccessful) {
                notifyDataSetUpdated()
            }
        }
    }

    override fun getDataUpdatedObserver(): LiveData<Long> = updatedObserver

    override fun getBoolean(key: String): Boolean = remoteConfig.getBoolean(key)

    override fun getDouble(key: String): Double = remoteConfig.getDouble(key)

    override fun getLong(key: String): Long = remoteConfig.getLong(key)

    override fun getString(key: String): String = remoteConfig.getString(key)

    private fun notifyDataSetUpdated() {
        Logger.d("FirebaseConfigRepository was data set updated")
        updatedObserver.value = System.currentTimeMillis()
    }
}

class LocalConfigRepository(
    private val sharedPreferences: SharedPreferences
) : ConfigWriterRepository {

    private val updatedObserver = MutableLiveData<Long>(System.currentTimeMillis())

    override fun getDataUpdatedObserver(): LiveData<Long> = updatedObserver

    override fun writeBoolean(key: String, value: Boolean) = sharedPreferences.edit {
        putBoolean(key, value)
        notifyDataSetUpdated()
    }

    override fun writeDouble(key: String, value: Double) = sharedPreferences.edit {
        putFloat(key, value.toFloat())
        notifyDataSetUpdated()
    }

    override fun writeLong(key: String, value: Long) = sharedPreferences.edit {
        putLong(key, value)
        notifyDataSetUpdated()
    }

    override fun writeString(key: String, value: String) = sharedPreferences.edit {
        putString(key, value)
        notifyDataSetUpdated()
    }

    override fun getBoolean(key: String): Boolean = sharedPreferences.getBoolean(key, false)

    override fun getDouble(key: String): Double = sharedPreferences.getFloat(key, 0f).toDouble()

    override fun getLong(key: String): Long = sharedPreferences.getLong(key, 0L)

    override fun getString(key: String): String = sharedPreferences.getString(key, "")!!

    private fun notifyDataSetUpdated() {
        Logger.d("LocalConfigRepository was data set updated")
        updatedObserver.value = System.currentTimeMillis()
    }
}