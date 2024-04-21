package com.pegasus.application.controllers;

import com.pegasus.application.dto.RunDto;
import com.pegasus.application.exeptions.UserException;
import com.pegasus.application.service.RunService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class RunController {

    private final RunService runService;
    @PostMapping(value = "/runs", produces = {"application/json"}, consumes = {"application/json"})
    @Operation(summary = "Create Run", description = "Creates a new run.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Run created successfully", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Bad request", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = UserException.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })    public ResponseEntity<RunDto> createRun(@RequestBody RunDto runDto){
        return ResponseEntity.ok()
                .body(runService.create(runDto));
    }

    @PatchMapping(value = "/runs", produces = {"application/json"}, consumes = { "application/json"})
    public ResponseEntity<RunDto> partialUpdateRun(@RequestBody RunDto runDto){
        return ResponseEntity.ok()
                .body(runService.partialUpdate(runDto));
    }

    @GetMapping(value = "/runs")
    public ResponseEntity<List<RunDto>> getAllRuns(){
        return ResponseEntity.ok()
                .body(runService.getAll());
    }

    @GetMapping("/runs/{id}")
    public ResponseEntity<RunDto> getRun(@PathVariable Long id){
        RunDto runDto = runService.getById(id);
        return ResponseEntity.ok().body(runDto);
    }

    @DeleteMapping("/runs/{id}")
    @Operation(summary = "Delete Run by id", description = "Deactivates the run and all the corresponding vendors/customers/service_providers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Run deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Run with this id was not found", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = UserException.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteRun(@PathVariable Long id){
        runService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
