package ac.smu.embedded.mapp.model

typealias TransferListener<TTask> =
            (task: TTask, bytesTransferred: Long, totalByteCount: Long) -> Unit

enum class UploadTaskStatus {
    PROGRESS,
    SUCCESS,
    COMPLETE,
    PAUSED,
    ERROR
}

/**
 * 스토리지에 파일을 업로드할때의 태스크 정보를 담는 클래스입니다.
 *
 * 정상적으로 태스크가 수행될 경우 [UploadTaskStatus]는 아래의 순서대로 제공됩니다.
 * [UploadTaskStatus.PROGRESS] - [UploadTaskStatus.SUCCESS] - [UploadTaskStatus.COMPLETE]
 *
 * 이 데이터 클래스에 제공되는 task 객체를 활용하여 업로드 중 일시정지, 취소를 할 수 있습니다.
 * 업로드 도중 일시정지를 했을때 [UploadTaskStatus]는 아래의 순서대로 제공됩니다.
 *
 * [UploadTaskStatus.PROGRESS] - Pause - [UploadTaskStatus.PAUSED] - Resume - [UploadTaskStatus.PROGRESS] - [UploadTaskStatus.SUCCESS] - [UploadTaskStatus.COMPLETE]
 *
 * 업로드 도중 취소를 했을때는 [UploadTaskStatus.ERROR] 로 변경되며 error 프로퍼티의 값이 존재합니다.
 *
 * @property task 업로드를 수행중인 객체
 * @property status 업로드 상태
 * @property bytesTransferred 전송 중인 바이트 양
 * @property totalByteCount 전체 전송해야할 바이트 양
 * @property error 업로드 도중 발생한 오류
 */
data class UploadTaskInfo<TTask>(
    val task: TTask,
    val status: UploadTaskStatus,
    val bytesTransferred: Long,
    val totalByteCount: Long,
    val error: Throwable?
) {
    /**
     * [UploadTaskInfo]의 상태가 [UploadTaskStatus.PROGRESS] 일때 주어지는 콜백을 수행합니다
     *
     * @param transferListener
     * @return [UploadTaskInfo]
     */
    fun onProgress(transferListener: TransferListener<TTask>): UploadTaskInfo<TTask> {
        if (status == UploadTaskStatus.PROGRESS) transferListener(
            task, bytesTransferred, totalByteCount
        )
        return this
    }

    /**
     * [UploadTaskInfo]의 상태가 [UploadTaskStatus.SUCCESS] 일때 주어지는 콜백을 수행합니다
     *
     * @param transferListener
     * @return [UploadTaskInfo]
     */
    fun onSuccess(transferListener: TransferListener<TTask>): UploadTaskInfo<TTask> {
        if (status == UploadTaskStatus.SUCCESS) transferListener(
            task, bytesTransferred, totalByteCount
        )
        return this
    }

    /**
     * [UploadTaskInfo]의 상태가 [UploadTaskStatus.COMPLETE] 일때 주어지는 콜백을 수행합니다
     *
     * @param listener
     * @return [UploadTaskInfo]
     */
    fun onComplete(listener: () -> Unit): UploadTaskInfo<TTask> {
        if (status == UploadTaskStatus.COMPLETE) listener()
        return this
    }

    /**
     * [UploadTaskInfo]의 상태가 [UploadTaskStatus.PAUSED] 일때 주어지는 콜백을 수행합니다
     *
     * @param transferListener
     * @return [UploadTaskInfo]
     */
    fun onPaused(transferListener: TransferListener<TTask>): UploadTaskInfo<TTask> {
        if (status == UploadTaskStatus.PAUSED) transferListener(
            task, bytesTransferred, totalByteCount
        )
        return this
    }

    /**
     * [UploadTaskInfo]의 상태가 [UploadTaskStatus.ERROR] 일때 주어지는 콜백을 수행합니다
     *
     * @param listener
     * @return [UploadTaskInfo]
     */
    fun onError(listener: (Throwable) -> Unit): UploadTaskInfo<TTask> {
        if (status == UploadTaskStatus.ERROR) listener(error!!)
        return this
    }

    companion object {
        fun <TTask> progress(
            task: TTask,
            bytesTransferred: Long,
            totalByteCount: Long
        ): UploadTaskInfo<TTask> {
            return UploadTaskInfo(
                task,
                UploadTaskStatus.PROGRESS,
                bytesTransferred,
                totalByteCount,
                null
            )
        }

        fun <TTask> success(
            task: TTask,
            bytesTransferred: Long,
            totalByteCount: Long
        ): UploadTaskInfo<TTask> {
            return UploadTaskInfo(
                task,
                UploadTaskStatus.SUCCESS,
                bytesTransferred,
                totalByteCount,
                null
            )
        }

        fun <TTask> complete(task: TTask): UploadTaskInfo<TTask> {
            return UploadTaskInfo(task, UploadTaskStatus.COMPLETE, 0, 0, null)
        }

        fun <TTask> paused(
            task: TTask,
            bytesTransferred: Long,
            totalByteCount: Long
        ): UploadTaskInfo<TTask> {
            return UploadTaskInfo(
                task,
                UploadTaskStatus.PAUSED,
                bytesTransferred,
                totalByteCount,
                null
            )
        }

        fun <TTask> error(task: TTask, error: Throwable): UploadTaskInfo<TTask> {
            return UploadTaskInfo(task, UploadTaskStatus.ERROR, 0, 0, error)
        }
    }
}