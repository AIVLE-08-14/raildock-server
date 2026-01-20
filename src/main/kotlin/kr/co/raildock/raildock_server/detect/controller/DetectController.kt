package kr.co.raildock.raildock_server.detect.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import kr.co.raildock.raildock_server.detect.dto.DetectJobCreateResponse
import kr.co.raildock.raildock_server.detect.dto.DetectJobGetResponse
import kr.co.raildock.raildock_server.detect.service.DetectService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/detect")
class DetectController(
    private val service: DetectService
) {
    @Operation(summary = "결함 탐지 Job 생성 - 동영상 업로드")
    @PostMapping(
        value = ["/jobs"],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
    )
    fun createJob(
        @Parameter(description = "동영상 파일", required = true)
        @RequestPart("video") video: MultipartFile,

        @Parameter(description = "메타데이터 JSON 문자열", required = true)
        @RequestPart("metadata") metadata: String,
    ): ResponseEntity<DetectJobCreateResponse> {
        val res = service.createJob(video, metadata)
        return ResponseEntity.accepted().body(res)
    }

    @GetMapping("/jobs/{jobId}")
    fun getJob(
        @PathVariable jobId: Long
    ): ResponseEntity<DetectJobGetResponse> {
        val res = service.getJob(jobId)
        return ResponseEntity.ok(res)
    }
}