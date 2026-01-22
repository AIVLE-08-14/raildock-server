package kr.co.raildock.raildock_server.detect.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import kr.co.raildock.raildock_server.detect.dto.DetectCreateResponse
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
    /*
    최소 검증?
    영상이 하나도 없으면 400
    datetime 형식 체크?
    */

    @Operation(summary = "Problem Detection 생성(동영상 업로드)")
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun create(
        @Parameter(description = "파일이름/요청명", required = true)
        @RequestPart("name") name: String,

        @Parameter(description = "촬영 위치 및 구간", required = true)
        @RequestPart("section") section: String,

        @Parameter(description = "촬영 시각", required = true)
        @RequestPart("datetime") datetime: String,

        @Parameter(description = "방향", required = true)
        @RequestPart("direction") direction: String,

        @Parameter(description = "습도", required = false)
        @RequestPart("humidity", required = false) humidity: Int?,

        @Parameter(description = "온도", required = false)
        @RequestPart("temperature", required = false) temperature: Int?,

        @Parameter(description = "날씨", required = false)
        @RequestPart("weather", required = false) weather: String?,

        @Parameter(description = "메타데이터(임시저장용)", required = false)
        @RequestPart("metadata", required = false) metadata: MultipartFile?,

        // ---Video Files ---
        @Parameter(description = "애자 영상 파일", required = false)
        @RequestPart("insulatorVideo", required = false) insulatorVideo: MultipartFile?,

        @Parameter(description = "철도 영상 파일", required = false)
        @RequestPart("railVideo", required = false) railVideo: MultipartFile?,

        @Parameter(description = "둥지 영상 파일", required = false)
        @RequestPart("nestVideo", required = false) nestVideo: MultipartFile?,
    ): ResponseEntity<DetectCreateResponse> {
        val res = service.create(
            name = name,
            section = section,
            datetime = datetime,
            direction = direction,
            humidity = humidity,
            temperature = temperature,
            weather = weather,
            metadata = metadata,
            insulatorVideo = insulatorVideo,
            railVideo = railVideo,
            nestVideo = nestVideo,
        )
        return ResponseEntity.accepted().body(res)
    }
}