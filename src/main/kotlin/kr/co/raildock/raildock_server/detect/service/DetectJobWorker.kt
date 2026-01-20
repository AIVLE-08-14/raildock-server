package kr.co.raildock.raildock_server.detect.service

import kr.co.raildock.raildock_server.detect.domain.DetectJobStatus
import kr.co.raildock.raildock_server.detect.repository.DetectJobRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Component
class DetectJobWorker(
    private val jobRepository: DetectJobRepository,
    private val fastApiClient: FastApiClient,
    private val defectRepository: DetectDefectRepository
) {

    @Scheduled(fixedDelay = 5000)
    fun poll(){
        val jobs = jobRepository.findTop10ByStatusOrderByCreatedAtAsc(DetectJobStatus.PENDING)
        jobs.forEach{ job ->
            processOne(job.id!!)
        }
    }
    fun processOne(jobId: Long) {
        try {
            markRunning(jobId)
            val result = fastApiClient.infer(jobId)
            saveResult(jobId, result)
            markCompleted(jobId)
        } catch (e: Exception){
            markFailed(jobId, e.massage ?: "Unknown error")
        }
    }
    @Transactional
    fun markRunning(jobId: Long) {
        val job = jobRepository.findById(jobId).orElseThrow()
        if (job.status != DetectJobStatus.PENDING) return
        job.status = DetectJobStatus.RUNNING
        job.startedAt = OffsetDateTime.now()
    }

    @Transactional
    fun markCompleted(jobId: Long){
        val job = jobRepository.findById(jobId).orElseThrow()
        job.status = DetectJobStatus.COMPLETED
        job.completedAt = OffsetDateTime.now()
    }

    @Transactional
    fun markFailed(jobId: Long, msg: String){
        val job = jobRepository.findById(jobId).orElseThrow()
        job.status = DetectJobStatus.FAILED
        job.errorMessage = msg.take(2000)
        job.completedAt = OffsetDateTime.now()
    }

    @Transactional
    fun saveResult(jobId: Long, result: InferResponse){

    }

}