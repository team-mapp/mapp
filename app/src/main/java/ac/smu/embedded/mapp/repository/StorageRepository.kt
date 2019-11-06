package ac.smu.embedded.mapp.repository

import ac.smu.embedded.mapp.model.UploadTaskInfo
import ac.smu.embedded.mapp.util.asLiveData
import android.net.Uri
import androidx.lifecycle.LiveData
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.UploadTask
import java.io.InputStream

interface StorageRepository<TTask> {

    /**
     * 스토리지의 원하는 위치에 파일을 업로드합니다. 업로드할때 [Uri]로 업로드합니다.
     * [Uri.fromFile]을 통해 파일 객체에서 [Uri]를 가져올 수 있습니다.
     *
     * @param filePath 스토리지 내 파일 경로
     * @param uri
     * @param contentType MIME 타입의 [String]을 지정합니다 (ex. "image/jpeg")
     * @return [UploadTaskInfo]를 포함하는 [LiveData]
     */
    fun put(
        filePath: String,
        uri: Uri,
        contentType: String? = null
    ): LiveData<UploadTaskInfo<TTask>>

    /**
     * 스토리지의 원하는 위치에 파일을 업로드합니다. 업로드할때 [ByteArray]로 업로드합니다.
     *
     * @param filePath 스토리지 내 파일 경로
     * @param bytes
     * @param contentType MIME 타입의 [String]을 지정합니다 (ex. "image/jpeg")
     * @return [UploadTaskInfo]를 포함하는 [LiveData]
     */
    fun put(
        filePath: String,
        bytes: ByteArray,
        contentType: String? = null
    ): LiveData<UploadTaskInfo<TTask>>

    /**
     * 스토리지의 원하는 위치에 파일을 업로드합니다. 업로드할때 [InputStream]으로 업로드합니다.
     *
     * @param filePath 스토리지 내 파일 경로
     * @param stream
     * @param contentType MIME 타입의 [String]을 지정합니다 (ex. "image/jpeg")
     * @return [UploadTaskInfo]를 포함하는 [LiveData]
     */
    fun put(
        filePath: String,
        stream: InputStream,
        contentType: String? = null
    ): LiveData<UploadTaskInfo<TTask>>

}

class StorageRepositoryImpl(private val storage: FirebaseStorage) : StorageRepository<UploadTask> {

    override fun put(
        filePath: String,
        uri: Uri,
        contentType: String?
    ): LiveData<UploadTaskInfo<UploadTask>> {
        val metadata = StorageMetadata.Builder()
            .setContentType(contentType)
            .build()

        return getChildReference(filePath).putFile(uri, metadata).asLiveData()
    }

    override fun put(
        filePath: String,
        bytes: ByteArray,
        contentType: String?
    ): LiveData<UploadTaskInfo<UploadTask>> {
        val metadata = StorageMetadata.Builder()
            .setContentType(contentType)
            .build()

        return getChildReference(filePath).putBytes(bytes, metadata).asLiveData()
    }

    override fun put(
        filePath: String,
        stream: InputStream,
        contentType: String?
    ): LiveData<UploadTaskInfo<UploadTask>> {
        val metadata = StorageMetadata.Builder()
            .setContentType(contentType)
            .build()

        return getChildReference(filePath).putStream(stream, metadata).asLiveData()
    }

    private fun getChildReference(filePath: String) = storage.reference.child(filePath)
}