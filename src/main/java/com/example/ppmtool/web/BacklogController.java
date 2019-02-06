package com.example.ppmtool.web;

import com.example.ppmtool.domain.ProjectTask;
import com.example.ppmtool.exceptions.NotFoundExceptionResponse;
import com.example.ppmtool.services.MapValidationErrorService;
import com.example.ppmtool.services.ProjectTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/backlog")
@CrossOrigin
public class BacklogController {

    @Autowired
    private ProjectTaskService projectTaskService;

    @Autowired
    private MapValidationErrorService mapValidationErrorService;

    @PostMapping("/{backlogId}/tasks")
    public ResponseEntity<?> addPTtoBacklog(@Valid @RequestBody ProjectTask projectTask, BindingResult result, @PathVariable String backlogId) {

        ResponseEntity<?> errorMap = mapValidationErrorService.validateRequest(result);

        return errorMap == null ? new ResponseEntity<>(projectTaskService.addProjectTask(backlogId, projectTask), HttpStatus.CREATED)
                : errorMap;

    }

    @GetMapping("/{backlogId}/tasks")
    public ResponseEntity<?> getProjectBacklog(@PathVariable String backlogId) {

        Iterable<ProjectTask> backlogById = projectTaskService.findBacklogById(backlogId);
        return new ResponseEntity<>(backlogById, backlogById.iterator().hasNext() ? HttpStatus.OK : HttpStatus.NO_CONTENT );

    }

    @GetMapping("/{backlogId}/tasks/{taskSequence}")
    public ResponseEntity<?> getTaskBySequence(@PathVariable String backlogId, @PathVariable String taskSequence) {
        return new ResponseEntity<>(projectTaskService.findByProjectSequence(backlogId, taskSequence), HttpStatus.OK );
    }

    @PutMapping("/{backlogId}/tasks/{taskSequence}")
    public ResponseEntity<?> updateTask(@PathVariable String backlogId, @PathVariable String taskSequence, @Valid @RequestBody ProjectTask projectTask, BindingResult result) {
        ResponseEntity<?> errorMap = mapValidationErrorService.validateRequest(result);

        return errorMap == null ? new ResponseEntity<>(projectTaskService.updateProjectTask(projectTask, backlogId, taskSequence), HttpStatus.OK)
                : errorMap;
    }

    @DeleteMapping("/{backlogId}/tasks/{taskSequence}")
    public ResponseEntity<?> deleteTask(@PathVariable String backlogId, @PathVariable String taskSequence) {
        projectTaskService.deleteProjectTask(backlogId, taskSequence);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
