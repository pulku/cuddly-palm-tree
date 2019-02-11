package com.example.ppmtool.services;

import com.example.ppmtool.domain.Backlog;
import com.example.ppmtool.domain.Project;
import com.example.ppmtool.domain.ProjectTask;
import com.example.ppmtool.exceptions.NotFoundException;
import com.example.ppmtool.repositories.BacklogRepository;
import com.example.ppmtool.repositories.ProjectRepository;
import com.example.ppmtool.repositories.ProjectTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectTaskService {

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private ProjectRepository projectRepository;

    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask) {

        Backlog backlog = backlogRepository.findByProjectIdentifier(projectIdentifier);
        if (backlog == null) {
            throw new NotFoundException("Project not found");
        }

        projectTask.setBacklog(backlog);
        Integer backlogSequence = backlog.getPTSequence();
        backlogSequence++;
        backlog.setPTSequence(backlogSequence);
        backlogRepository.save(backlog);
        projectTask.setProjectSequence(projectIdentifier + "-" + backlogSequence );
        projectTask.setProjectIdentifier(projectIdentifier);
        if (projectTask.getPriority() == null || projectTask.getPriority() == 0 ) {
            projectTask.setPriority(3);
        }
        if (projectTask.getStatus() == null) {
            projectTask.setStatus("TO DO");
        }
        return projectTaskRepository.save(projectTask);

    }

    public Iterable<ProjectTask> findBacklogById(String backlogId) {
        Project project = projectRepository.findByProjectIdentifier(backlogId);

        if(project==null){
            throw new NotFoundException("Project with ID: '"+backlogId+"' does not exist");
        }
        return projectTaskRepository.findByProjectIdentifierOrderByPriority(backlogId);
    }

    public ProjectTask findByProjectSequence(String backlogId, String sequence) {
        ProjectTask projectTask = projectTaskRepository.findByProjectSequenceAndProjectIdentifier(sequence, backlogId);
        if (projectTask == null ) {
            throw new NotFoundException("Task is not found");
        }
        return projectTask;
    }

    public ProjectTask updateProjectTask(ProjectTask updatedProjectTask, String backlogId, String sequence) {
        ProjectTask projectTask = projectTaskRepository.findByProjectSequenceAndProjectIdentifier(sequence, backlogId);
        if (projectTask == null ){
            throw new NotFoundException("Task is not found");
        }
        updatedProjectTask.setId(projectTask.getId());
        updatedProjectTask.setProjectIdentifier(backlogId);
        updatedProjectTask.setProjectSequence(sequence);
        projectTaskRepository.save(updatedProjectTask);
        return updatedProjectTask;

    }

    public void deleteProjectTask(String backlogId, String sequence) {
        ProjectTask projectTask = findByProjectSequence(backlogId, sequence);
        projectTaskRepository.deleteById(projectTask.getId());
    }
}
