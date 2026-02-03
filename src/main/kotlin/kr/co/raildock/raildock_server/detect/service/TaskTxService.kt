package kr.co.raildock.raildock_server.detect.service

import kr.co.raildock.raildock_server.detect.domain.ProblemDetectionEntity
import kr.co.raildock.raildock_server.detect.domain.TaskStatus
import kr.co.raildock.raildock_server.detect.repository.ProblemDetectionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class TaskTxService(
    private val repo: ProblemDetectionRepository
) {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun markVideoRunning(id: Long): Boolean =
        repo.tryStartVideo(id, TaskStatus.PENDING, TaskStatus.RUNNING) == 1

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun markLlmRunning(id: Long): Boolean =
        repo.tryStartLlm(id, TaskStatus.PENDING, TaskStatus.RUNNING) == 1

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun completeVideo(id: Long, apply: (ProblemDetectionEntity) -> Unit) {
        val pd = repo.findById(id).orElseThrow()
        apply(pd)
        pd.videoTaskStatus = TaskStatus.COMPLETED
        pd.taskErrorMessage = null
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun failVideo(id: Long, msg: String) {
        val pd = repo.findById(id).orElseThrow()
        pd.videoTaskStatus = TaskStatus.FAILED
        pd.taskErrorMessage = msg.take(500)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun completeLlm(id: Long, apply: (ProblemDetectionEntity) -> Unit) {
        val pd = repo.findById(id).orElseThrow()
        apply(pd)
        pd.llmTaskStatus = TaskStatus.COMPLETED
        pd.taskErrorMessage = null
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun failLlm(id: Long, msg: String) {
        val pd = repo.findById(id).orElseThrow()
        pd.llmTaskStatus = TaskStatus.FAILED
        pd.taskErrorMessage = msg.take(500)
    }
}