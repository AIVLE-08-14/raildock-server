package kr.co.raildock.raildock_server.detect.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import kr.co.raildock.raildock_server.detect.dto.DetectCreateResponse
import kr.co.raildock.raildock_server.detect.dto.ProblemDetectionGetResponse
import kr.co.raildock.raildock_server.detect.dto.ProblemDetectionListResponse
import kr.co.raildock.raildock_server.detect.service.DetectService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@Tag(
    name = "Detect",
    description = "결함 탐지 관리"
)
@RestController
@RequestMapping("/api/detect")
class DetectController(
    private val service: DetectService,
) {
    @Operation(summary = "Problem Detection 생성(동영상 업로드)")
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun create(
        @Parameter(description = "파일이름/요청명", required = true)
        @RequestParam("name") name: String,

        @Parameter(description = "메타데이터", required = true)
        @RequestParam("metadata", required = true) metadata: MultipartFile,

        // ---Video Files ---
        @Parameter(description = "애자 영상 파일", required = false)
        @RequestParam("insulatorVideo", required = false) insulatorVideo: MultipartFile?,

        @Parameter(description = "철도 영상 파일", required = false)
        @RequestParam("railVideo", required = false) railVideo: MultipartFile?,

        @Parameter(description = "둥지 영상 파일", required = false)
        @RequestParam("nestVideo", required = false) nestVideo: MultipartFile?,
    ): ResponseEntity<DetectCreateResponse> {
        val res = service.create(
            name = name,
            metadata = metadata,
            insulatorVideo = insulatorVideo,
            railVideo = railVideo,
            nestVideo = nestVideo,
        )
        return ResponseEntity.accepted().body(res)
    }

    @Operation(summary = "Problem Detection 목록 조회")
    @GetMapping
    fun list(
        @RequestParam("page", defaultValue = "0") page: Int,
        @RequestParam("size", defaultValue = "20") size: Int
    ): ResponseEntity<ProblemDetectionListResponse>{
        return ResponseEntity.ok(service.listProblemDetections(page, size))
    }

    @Operation(summary = "Problem Detection 작업 상태 및 결과 조회")
    @GetMapping("/{problemDetectionId}")
    fun getProblemDetection(
        @PathVariable("problemDetectionId") id: Long
    ): ResponseEntity<ProblemDetectionGetResponse>{
        return ResponseEntity.ok(service.getProblemDetection(id))
    }
}