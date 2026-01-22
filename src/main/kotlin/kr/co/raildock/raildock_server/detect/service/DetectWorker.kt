package kr.co.raildock.raildock_server.detect.service

import kr.co.raildock.raildock_server.detect.domain.DetectJobStatus
import kr.co.raildock.raildock_server.detect.dto.InferRequest
import kr.co.raildock.raildock_server.detect.repository.DetectionVideoRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Component
class DetectWorker(
    private val videoRepo: DetectionVideoRepository,
    private val fastapi: FastApiClient
) {

    @Scheduled(fixedDelay = 2000)
    fun tick(){
        val targets = videoRepo.findTop10ByTaskStatusOrderByCreatedAtAsc(DetectJobStatus.PENDING)
        for (v in targets){
            runOne(v.id!!)
        }
    }

    fun runOne(videoId: Long){
        if (!start(videoId)) return

        try{
            val v = videoRepo.findById(videoId).orElseThrow()
            val pd = v.problemDetection

            val req = InferRequest(
                videoId = videoId,
                videoType = v.videoType.name,
                videoUrl = v.videoURL
            )
            val resp = fastapi.infer(req)

            // TODO: Defect 저장

            done(videoId)
        } catch(e: Exception){
            fail(videoId, e.message ?: "infer failed")
        }
    }

    @Transactional
    fun start(videoId: Long): Boolean{
        return videoRepo.tryStart(videoId) == 1
    }

    @Transactional
    fun done(videoId: Long){
        val v = videoRepo.findById(videoId).orElseThrow()
        v.taskStatus = DetectJobStatus.COMPLETED
        v.completedAt = OffsetDateTime.now()
    }

    @Transactional
    fun fail(videoId: Long, msg: String){
        val v = videoRepo.findById(videoId).orElseThrow()
        v.taskStatus = DetectJobStatus.FAILED
        v.completedAt = OffsetDateTime.now()
        v.errorMessage = msg.take(2000)
    }
}