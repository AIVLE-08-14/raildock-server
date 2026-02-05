package kr.co.raildock.raildock_server.problem.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kr.co.raildock.raildock_server.problem.dto.*
import kr.co.raildock.raildock_server.problem.enum.ProblemStatus
import kr.co.raildock.raildock_server.problem.service.ProblemService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@Tag(
    name = "Problem",
    description = "문제(결함) 조회 및 관리"
)
@RestController
@RequestMapping("/api/problems")
class ProblemController(
    private val problemService: ProblemService
) {

    @GetMapping
    @Operation(summary = "문제(결함) 전체 간단조회")
    fun list(): ResponseEntity<List<ProblemSummaryDto>> =
        ResponseEntity.ok(problemService.getProblems())
    
    @GetMapping("/{id}")
    @Operation(summary = "문제(결함) 상세조회")
    fun detail(
        @PathVariable id: UUID
    ): ResponseEntity<ProblemDetailDto> =
        ResponseEntity.ok(problemService.getProblemDetail(id))

    @PostMapping
    @Operation(summary = "문제(결함) 임시 생성용")
    fun create(
        @RequestBody request: ProblemCreateRequest
    ): ResponseEntity<UUID> =
        ResponseEntity.ok(problemService.createProblem(request))

    @PatchMapping("/{id}/status")
    @Operation(summary = "문제(결함) 상태(status) 변경")
    fun updateStatus(
        @PathVariable id: UUID,
        @RequestParam status: ProblemStatus
    ): ResponseEntity<Void> {
        problemService.updateProblemStatus(id, status)
        return ResponseEntity.noContent().build()
    }

    @PatchMapping("/{id}")
    @Operation(summary = "문제(결함) 내용 수정")
    fun updateContent(
        @PathVariable id: UUID,
        @RequestBody request: ProblemContentUpdateRequest
    ): ResponseEntity<Void> {
        problemService.updateProblemContent(id, request)
        return ResponseEntity.noContent().build()
    }

    @PatchMapping("/{id}/manager")
    @Operation(summary = "문제(결함) 담당자(manager) 변경")
    fun manager(
        @PathVariable id: UUID,
        @RequestBody request: ProblemManagerUpdateRequest
    ): ResponseEntity<Void> {
        problemService.updateProblemManager(id, request)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "문제(결함) 삭제")
    fun delete(
        @PathVariable id: UUID
    ): ResponseEntity<Void> {
        problemService.deleteProblem(id)
        return ResponseEntity.noContent().build()
    }
}
