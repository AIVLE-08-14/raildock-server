package kr.co.raildock.raildock_server.detect.service

import com.fasterxml.jackson.databind.ObjectMapper
import kr.co.raildock.raildock_server.common.enum.ModelType
import kr.co.raildock.raildock_server.detect.domain.ProblemDetectionEntity
import kr.co.raildock.raildock_server.detect.dto.LlmReportItem
import kr.co.raildock.raildock_server.detect.dto.LlmReportRoot
import kr.co.raildock.raildock_server.file.service.FileService
import kr.co.raildock.raildock_server.problem.dto.ProblemCreateRequest
import kr.co.raildock.raildock_server.problem.enum.RailType
import kr.co.raildock.raildock_server.problem.enum.Severity
import kr.co.raildock.raildock_server.problem.service.ProblemService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class ProblemImportService(
    private val fileService: FileService,
    private val objectMapper: ObjectMapper,
    private val problemService: ProblemService,
) {

    @Transactional
    fun importFromReportJson(
        pd: ProblemDetectionEntity,
        modelType: ModelType,
        reportJsonBytes: ByteArray,
    ) {
        val root = objectMapper.readValue(reportJsonBytes, LlmReportRoot::class.java)

        root.reports.forEach { report ->
            val req = toCreateRequest(
                pd = pd,
                modelType = modelType,
                root = root,
                report = report
            )
            problemService.createProblem(req)
        }
    }

    // 매핑
    private fun toCreateRequest(
        pd: ProblemDetectionEntity,
        modelType: ModelType,
        root: LlmReportRoot,
        report: LlmReportItem,
    ): ProblemCreateRequest {
        val det0 = report.vision_result.detections.firstOrNull()
            ?: throw IllegalStateException("detections empty (index=${report.index})")

        val doc = report.document_sections ?: emptyMap()
        val meta = root.metadata

        val problemNum = doc["일련번호"] ?: "${root.report_id}-${report.index}"

        val imageFile = report.image_file
            ?: throw IllegalStateException("image_file is null (index=${report.index})")

        // 모델별 폴더명 고정
        val modelFolder = when (modelType) {
            ModelType.INSULATOR -> "insulator"
            ModelType.RAIL -> "rail"
            ModelType.NEST -> "nest"
        }

        // DetectWorker 저장 규칙과 반드시 일치해야 함
        val sourceImagePath = "$modelFolder/origin/$imageFile"
        val bboxJsonPath = "$modelFolder/json/${imageFile.substringBeforeLast('.')}.json"

        val sourceImageId = fileService.findFileId(
            parentId = pd.id!!,
            originalFilename = sourceImagePath
        ) ?: throw IllegalStateException("source image not found: $sourceImagePath")

        val boundingBoxJsonId = fileService.findFileId(
            parentId = pd.id!!,
            originalFilename = bboxJsonPath
        ) ?: throw IllegalStateException("bbox json not found: $bboxJsonPath")

        val lat = meta?.latitude ?: throw IllegalStateException("metadata.latitude is null")
        val lng = meta.longitude ?: throw IllegalStateException("metadata.longitude is null")

        return ProblemCreateRequest(
            detectionId = pd.id!!,
            problemNum = problemNum,
            model = modelType,

            problemType = det0.detail ?: "UNKNOWN",
            problemStatus = doc["결함정보"] ?: "",

            railType = toRailType(det0.rail_type ?: doc["철도분류"]),
            component = det0.cls_name ?: (doc["탐지대상"] ?: "UNKNOWN"),
            reference = doc["참조 규정"],

            severity = toSeverity(doc["위험도평가"]),
            severityReason = doc["위험도등급 판정근거"],
            recommendedActions = doc["권장 조치내용"] ?: "",

            latitude = lat,
            longitude = lng,
            region = meta.region_name,

            weather = meta.weather,
            temperature = meta.temperature,
            humidity = meta.humidity,

            detectedTime = parseDateTime(meta.datetime) ?: parseCreatedAt(root.created_at),

            sourceImageId = sourceImageId,
            boundingBoxJsonId = boundingBoxJsonId
        )
    }

    private fun toSeverity(text: String?): Severity {
        val upper = (text ?: "").uppercase()
        return when {
            upper.contains("S") -> Severity.S
            upper.contains("X2") -> Severity.X2
            upper.contains("X1") -> Severity.X1
            upper.contains("O") -> Severity.O
            upper.contains("E") -> Severity.E
            else -> Severity.O
        }
    }

    private fun toRailType(raw: String?): RailType {
        val s = (raw ?: "").trim()
        return when {
            s.contains("고속") -> RailType.HIGH_SPEED
            s.contains("일반") -> RailType.GENERAL
            s.contains("공통") || s.contains("둥지") -> RailType.COMMON
            else -> RailType.GENERAL
        }
    }

    private fun parseCreatedAt(createdAt: String): LocalDateTime {
        // 예: "2026-02-05T03:02:07.354411"
        return try {
            LocalDateTime.parse(createdAt)
        } catch (_: Exception) {
            LocalDateTime.parse(createdAt, DateTimeFormatter.ISO_DATE_TIME)
        }
    }

    private fun parseDateTime(dt: String?): LocalDateTime? {
        if (dt.isNullOrBlank()) return null
        return try {
            LocalDateTime.parse(dt)
        } catch (_: Exception) {
            runCatching { LocalDateTime.parse(dt, DateTimeFormatter.ISO_DATE_TIME) }.getOrNull()
        }
    }
}