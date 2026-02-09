package kr.co.raildock.raildock_server.detect.service

import kr.co.raildock.raildock_server.common.enum.ModelType
import kr.co.raildock.raildock_server.detect.domain.ProblemDetectionEntity
import kr.co.raildock.raildock_server.detect.domain.TaskStatus
import kr.co.raildock.raildock_server.detect.dto.DetectCreateResponse
import kr.co.raildock.raildock_server.detect.dto.DetectionProblemSummaryResponse
import kr.co.raildock.raildock_server.detect.dto.ProblemDetectionGetResponse
import kr.co.raildock.raildock_server.detect.dto.ProblemDetectionListItem
import kr.co.raildock.raildock_server.detect.dto.ProblemDetectionListResponse
import kr.co.raildock.raildock_server.detect.repository.ProblemDetectionRepository
import kr.co.raildock.raildock_server.file.enum.FileType
import kr.co.raildock.raildock_server.file.service.FileService
import kr.co.raildock.raildock_server.problem.service.ProblemService
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

@Service
class DetectService(
    private val detectRepo: ProblemDetectionRepository,
    private val fileService: FileService,
    private val problemService : ProblemService
) {
    @Transactional
    fun create(
        name: String,
        metadata: MultipartFile,
        insulatorVideo: MultipartFile?,
        railVideo: MultipartFile?,
        nestVideo: MultipartFile?
    ): DetectCreateResponse{

        // 1. 최소 영상 한개 이상 필수
        val hasAnyVideo = (insulatorVideo != null) || (railVideo != null) || (nestVideo != null)
        require(hasAnyVideo) { "At least one video is required" }

        // 2. Metadata 파일 필수
        val hasMetadata = !metadata.isEmpty
        require(hasMetadata) { "Metadata file is required" }

        // 3. ProblemDetectionEntity 우선 생성 및 저장
        val pd = detectRepo.save(
            ProblemDetectionEntity(
                name = name,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                videoTaskStatus = TaskStatus.PENDING,
                llmTaskStatus = TaskStatus.CREATED
            )
        )

        // 4. Metadata File 저장(필수)
        val meta = fileService.upload(metadata, FileType.JSON)
        pd.metadataFileId = meta.fileId

        // 5. Video Upload 및 Id 저장
        if (insulatorVideo != null && !insulatorVideo.isEmpty) {
            val uploaded = fileService.upload(insulatorVideo, FileType.VIDEO)
            pd.insulatorVideoFileId = uploaded.fileId
        }
        if (railVideo != null && !railVideo.isEmpty) {
            val uploaded = fileService.upload(railVideo, FileType.VIDEO)
            pd.railVideoFileId = uploaded.fileId
        }
        if (nestVideo != null && !nestVideo.isEmpty) {
            val uploaded = fileService.upload(nestVideo, FileType.VIDEO)
            pd.nestVideoFileId = uploaded.fileId
        }

        // Transaction 안이라 필요 없지만 일단 명시적 저장
        detectRepo.save(pd)

        // 6. 응답
        return DetectCreateResponse(
            detectionId = pd.id!!
        )

    }

    // 문제 탐지 상세 정보 조회
    fun getProblemDetection(id: Long): ProblemDetectionGetResponse {
        val pd = detectRepo.findById(id).orElseThrow {
            IllegalArgumentException("Problem detection with ID $id not found")
        }

        // 🔹 detectionId 기준 결함 요약 조회
        val problems = problemService.getProblemSummariesByDetectionId(id)

        val problemsSummary = DetectionProblemSummaryResponse(
            insulator = problems.filter { it.model == ModelType.INSULATOR },
            rail = problems.filter { it.model == ModelType.RAIL },
            nest = problems.filter { it.model == ModelType.NEST }
        )

        return ProblemDetectionGetResponse(
            id = pd.id!!,
            name = pd.name,
            createdAt = pd.createdAt.toString(),
            updatedAt = pd.updatedAt.toString(),

            metadataUrl = pd.metadataFileId?.let { fileService.getDownloadUrl(it) },
            insulatorVideoUrl = pd.insulatorVideoFileId?.let { fileService.getDownloadUrl(it) },
            railVideoUrl = pd.railVideoFileId?.let { fileService.getDownloadUrl(it) },
            nestVideoUrl = pd.nestVideoFileId?.let { fileService.getDownloadUrl(it) },

            videoTaskStatus = pd.videoTaskStatus.name,
            taskErrorMessage = pd.taskErrorMessage,
            videoResultZipUrl = pd.videoDetectedZipFileId?.let { fileService.getDownloadUrl(it) },
            llmTaskStatus = pd.llmTaskStatus.name,

            insulatorReportUrl = pd.insulatorReportFileId?.let { fileService.getDownloadUrl(it) },
            railReportUrl = pd.railReportFileId?.let { fileService.getDownloadUrl(it) },
            nestReportUrl = pd.nestReportFileId?.let { fileService.getDownloadUrl(it) },

            problems = problemsSummary
        )
    }

    // 문제 탐지 목록 조회
    fun listProblemDetections(page: Int, size: Int): ProblemDetectionListResponse {
        val pageable = PageRequest.of(page, size)
        val pdPage = detectRepo.findAllByOrderByCreatedAtDesc(pageable)
        val pds = pdPage.content

        val items = pds.map { pd ->

            ProblemDetectionListItem(
                id = pd.id!!,
                name = pd.name,
                createdAt = pd.createdAt.toString(),
                videoTaskStatus = pd.videoTaskStatus.name,
                llmTaskStatus = pd.llmTaskStatus.name
            )
        }

        return ProblemDetectionListResponse(items)
    }
}