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

    @GetMapping
    fun list(): ResponseEntity<List<ProblemSummaryDto>> =
        ResponseEntity.ok(problemService.getProblems())

    @GetMapping("/{id}")
    fun detail(@PathVariable id: UUID): ResponseEntity<ProblemDetailDto> =
        ResponseEntity.ok(problemService.getProblemDetail(id))

    @PostMapping
    fun create(@RequestBody request: ProblemCreateRequest): ResponseEntity<UUID> =
        ResponseEntity.ok(problemService.createProblem(request))

    @PatchMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @RequestBody request: ProblemUpdateRequest
    ): ResponseEntity<Void> {
        problemService.updateProblem(id, request)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Void> {
        problemService.deleteProblem(id)
        return ResponseEntity.noContent().build()
    }
}