package kr.co.raildock.raildock_server.problem.service

import kr.co.raildock.raildock_server.problem.dto.*
import kr.co.raildock.raildock_server.problem.repository.ProblemRepository
import kr.co.raildock.raildock_server.common.exception.BusinessException
import kr.co.raildock.raildock_server.problem.entity.ProblemEntity
import kr.co.raildock.raildock_server.problem.enum.ProblemStatus
import kr.co.raildock.raildock_server.problem.exception.ProblemErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ProblemServiceImpl(
    private val problemRepository: ProblemRepository
) : ProblemService {

    /* =========================
       결함 목록 조회 (Summary)
    ========================= */
    @Transactional(readOnly = true)
    override fun getProblems(): List<ProblemSummaryDto> =
        problemRepository.findAllSummaries()

    /* =========================
       결함 상세 조회 (Detail)
    ========================= */
    @Transactional(readOnly = true)
    override fun getProblemDetail(problemId: UUID): ProblemDetailDto {
        val problem = problemRepository.findById(problemId)
            .orElseThrow {
                BusinessException(ProblemErrorCode.PROBLEM_NOT_FOUND)
            }

        return ProblemDetailDto(
            id = problem.id!!,
            detectionId = problem.detectionId,
            problemNum = problem.problemNum,

            status = problem.status,
            severity = problem.severity,
            severityReason = problem.severityReason,
            reference = problem.reference,
            recommendedActions = problem.recommendedActions,

            problemType = problem.problemType,
            railType = problem.railType,
            component = problem.component,

            latitude = problem.latitude,
            longitude = problem.longitude,
            region = problem.region,

            weather = problem.weather,
            temperature = problem.temperature,
            humidity = problem.humidity,

            detectedTime = problem.detectedTime,

            managerId = problem.managerId,

            sourceImageId = problem.sourceImageId,
            boundingBoxJsonId = problem.boundingBoxJsonId
        )
    }

    /* =========================
       결함 생성
    ========================= */
    @Transactional
    override fun createProblem(request: ProblemCreateRequest): UUID {
        val problem = problemRepository.save(
            ProblemEntity(
                detectionId = request.detectionId,
                problemNum = request.problemNum,

                problemType = request.problemType,
                railType = request.railType,
                component = request.component,

                severity = request.severity,
                severityReason = request.severityReason,
                recommendedActions = request.recommendedActions,

                latitude = request.latitude,
                longitude = request.longitude,
                region = request.region,

                weather = request.weather,
                temperature = request.temperature,
                humidity = request.humidity,

                detectedTime = request.detectedTime,

                sourceImageId = request.sourceImageId,
                boundingBoxJsonId = request.boundingBoxJsonId
            )
        )

        return problem.id!!
    }

    /* =========================
       결함 상태 변경 (워크플로우)
    ========================= */
    @Transactional
    override fun updateProblemStatus(
        problemId: UUID,
        status: ProblemStatus
    ) {
        val problem = problemRepository.findById(problemId)
            .orElseThrow {
                BusinessException(ProblemErrorCode.PROBLEM_NOT_FOUND)
            }

        problem.status = status
    }

    /* =========================
       결함 내용 수정 (관리자 보정)
    ========================= */
    @Transactional
    override fun updateProblemContent(
        problemId: UUID,
        request: ProblemContentUpdateRequest
    ) {
        val problem = problemRepository.findById(problemId)
            .orElseThrow {
                BusinessException(ProblemErrorCode.PROBLEM_NOT_FOUND)
            }

        request.severity?.let { problem.severity = it }
        request.severityReason?.let { problem.severityReason = it }
        request.reference?.let { problem.reference = it }

        request.managerId?.let { problem.managerId = it }

        request.problemType?.let { problem.problemType = it }
        request.component?.let { problem.component = it }

        request.railType?.let { problem.railType = it }
        request.region?.let { problem.region = it }

        request.weather?.let { problem.weather = it }
        request.temperature?.let { problem.temperature = it }
        request.humidity?.let { problem.humidity = it }
    }

    /* =========================
   결함 담당자 변경
========================= */
    @Transactional
    override fun updateProblemManager(
        problemId: UUID,
        request: ProblemManagerUpdateRequest
    ) {
        val problem = problemRepository.findById(problemId)
            .orElseThrow {
                BusinessException(ProblemErrorCode.PROBLEM_NOT_FOUND)
            }

        problem.managerId = request.managerId
    }


    /* =========================
       결함 삭제
    ========================= */
    @Transactional
    override fun deleteProblem(problemId: UUID) {
        val problem = problemRepository.findById(problemId)
            .orElseThrow {
                BusinessException(ProblemErrorCode.PROBLEM_NOT_FOUND)
            }

        problemRepository.delete(problem)
    }
}
