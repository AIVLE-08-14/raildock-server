package kr.co.raildock.raildock_server.problem.service

import kr.co.raildock.raildock_server.common.enum.ModelType
import kr.co.raildock.raildock_server.problem.dto.*
import kr.co.raildock.raildock_server.problem.enum.ProblemStatus
import java.util.UUID

interface ProblemService {

    /** 문제(결함) 목록 조회 */
    fun getProblems(): List<ProblemSummaryDto>

    /** 문제(결함) 상세 조회 */
    fun getProblemDetail(problemId: UUID): ProblemDetailDto

    /** 문제(결함) 생성 */
    fun createProblem(request: ProblemCreateRequest): UUID

    /** 문제 상태 변경 */
    fun updateProblemStatus(
        problemId: UUID,
        status: ProblemStatus
    )

    /** 문제 내용 수정 (관리자 보정) */
    fun updateProblemContent(
        problemId: UUID,
        request: ProblemContentUpdateRequest
    )

    /** 담당자 변경 */
    fun updateProblemManager(
        problemId: UUID,
        request: ProblemManagerUpdateRequest
    )

    /** 문제 삭제 */
    fun deleteProblem(problemId: UUID)

    /** Feedback용 Json 변경 메소드 */
    fun updateBoundingBoxJson(problemId: UUID, jsonFileId: Long)

    fun getProblemModel(problemId: UUID): ModelType

    fun getSourceImageNumber(problemId: UUID): Long

    fun getProblemSummariesByDetectionId(detectionId: Long): List<ProblemModelSummaryDto>
}
