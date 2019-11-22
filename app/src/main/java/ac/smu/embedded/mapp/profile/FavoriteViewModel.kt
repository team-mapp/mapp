package ac.smu.embedded.mapp.profile

import ac.smu.embedded.mapp.repository.FavoriteRepository
import ac.smu.embedded.mapp.repository.StorageRepository
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.storage.UploadTask

class FavoriteViewModel (
    private val favoriteRepository: FavoriteRepository) : ViewModel() {

    fun loadFavorites(userId:String) =
        favoriteRepository.loadFavorites(userId)

}