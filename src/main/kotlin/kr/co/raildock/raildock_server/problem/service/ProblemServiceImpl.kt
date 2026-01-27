package kr.co.raildock.raildock_server.problem.service

import kr.co.raildock.raildock_server.problem.dto.*
import kr.co.raildock.raildock_server.problem.repository.ProblemRepository
import kr.co.raildock.raildock_server.common.exception.BusinessException
import kr.co.raildock.raildock_server.problem.entity.ProblemEntity
import kr.co.raildock.raildock_server.problem.exception.ProblemErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ProblemServiceImpl(
    private val problemRepository: ProblemRepository
) : ProblemService {

    /* =========================
       결함 목록 조회
    ========================= */
    @Transactional(readOnly = true)
    override fun getProblems(): List<ProblemSummaryDto> {
        return problemRepository.findAll().map {
            ProblemSummaryDto(
                id = it.id!!,
                problemType = it.problemType,
                severity = it.severity,
                status = it.status,
                createdTime = it.createdTime
            )
        }
    }

    /* =========================
       결함 상세 조회
    ========================= */
    @Transactional(readOnly = true)
    override fun getProblemDetail(problemId: UUID): ProblemDetailDto {
        val problem = problemRepository.findById(problemId)
            .orElseThrow {
                BusinessException(ProblemErrorCode.PROBLEM_NOT_FOUND)
            }

        return ProblemDetailDto(
            id = problem.id!!,
            createdTime = problem.createdTime,
            problemType = problem.problemType,
            severity = problem.severity,
            status = problem.status,
            latitude = problem.latitude,
            longitude = problem.longitude,
            managerId = problem.managerId,
            originalImageId = problem.originalImageId,
            bboxJsonId = problem.bboxJsonId,
            reportId = problem.reportId
        )
    }

    /* =========================
       결함 생성
    ========================= */
    @Transactional
    override fun createProblem(request: ProblemCreateRequest): UUID {
        val problem = problemRepository.save(
            ProblemEntity(
                problemType = request.problemType,
                severity = request.severity,
                latitude = request.latitude,
                longitude = request.longitude,
                originalImageId = request.originalImageId,
                bboxJsonId = request.bboxJsonId,
                reportId = request.reportId
            )
        )
        return problem.id!!
    }

    /* =========================
       결함 수정 (부분 수정)
    ========================= */
    @Transactional
    override fun updateProblem(
        problemId: UUID,
        request: ProblemUpdateRequest
    ) {
        val problem = problemRepository.findById(problemId)
            .orElseThrow {
                BusinessException(ProblemErrorCode.PROBLEM_NOT_FOUND)
            }

        request.severity?.let {
            problem.severity = it
        }

        request.status?.let {
            problem.status = it
        }

        request.managerId?.let {
            problem.managerId = it
        }
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
