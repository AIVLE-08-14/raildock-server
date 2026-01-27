package kr.co.raildock.raildock_server.problem.service

import kr.co.raildock.raildock_server.problem.dto.*
import java.util.UUID

interface ProblemService {

    fun getProblems(): List<ProblemSummaryDto>

    fun getProblemDetail(problemId: UUID): ProblemDetailDto

    fun createProblem(request: ProblemCreateRequest): UUID

    fun updateProblem(
        problemId: UUID,
        request: ProblemUpdateRequest
    )

    fun deleteProblem(problemId: UUID)
}
