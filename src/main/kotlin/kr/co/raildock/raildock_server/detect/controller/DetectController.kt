package kr.co.raildock.raildock_server.detect.controller

import kr.co.raildock.raildock_server.detect.dto.DetectJobCreateResponse
import kr.co.raildock.raildock_server.detect.dto.DetectRequestDTO
import kr.co.raildock.raildock_server.detect.service.DetectService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/detect")
class DetectController(
    private val service: DetectService
) {
    @PostMapping("/job")
    fun createJob(
        @RequestPart("video") video: MultipartFile,
        @RequestPart("metadata") metadata: String,
    ): ResponseEntity<DetectJobCreateResponse> {
        val res = service.createJob(video, metadata)
        return ResponseEntity.accepted().body(res)
    }
}