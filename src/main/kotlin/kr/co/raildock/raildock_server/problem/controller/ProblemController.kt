package kr.co.raildock.raildock_server.problem.controller

import kr.co.raildock.raildock_server.problem.dto.*
import kr.co.raildock.raildock_server.problem.service.ProblemService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/problems")
class ProblemController(
    private val problemService: ProblemService
) {

    /* =========================
       결함 목록 조회
    ========================= */
    @GetMapping
    fun list(): ResponseEntity<List<ProblemSummaryDto>> =
        ResponseEntity.ok(problemService.getProblems())

    /* =========================
       결함 상세 조회
    ========================= */
    @GetMapping("/{id}")
    fun detail(
        @PathVariable id: UUID
    ): ResponseEntity<ProblemDetailDto> =
        ResponseEntity.ok(problemService.getProblemDetail(id))

    /* =========================
       결함 생성
    ========================= */
    @PostMapping
    fun create(
        @RequestBody request: ProblemCreateRequest
    ): ResponseEntity<UUID> =
        ResponseEntity.ok(problemService.createProblem(request))

    /* =========================
       결함 상태 변경 (워크플로우)
    ========================= */
    @PatchMapping("/{id}/status")
    fun updateStatus(
        @PathVariable id: UUID,
        @RequestBody request: ProblemStatusUpdateRequest
    ): ResponseEntity<Void> {
        problemService.updateProblemStatus(id, request)
        return ResponseEntity.noContent().build()
    }

    /* =========================
       결함 내용 수정 (관리자 보정)
    ========================= */
    @PatchMapping("/{id}")
    fun updateContent(
        @PathVariable id: UUID,
        @RequestBody request: ProblemContentUpdateRequest
    ): ResponseEntity<Void> {
        problemService.updateProblemContent(id, request)
        return ResponseEntity.noContent().build()
    }

    /* =========================
       결함 삭제
    ========================= */
    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: UUID
    ): ResponseEntity<Void> {
        problemService.deleteProblem(id)
        return ResponseEntity.noContent().build()
    }
}
