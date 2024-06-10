/*
 * You can use the following import statements
 *
 * import org.springframework.beans.factory.annotation.Autowired;
 * import org.springframework.http.HttpStatus;
 * import org.springframework.stereotype.Service;
 * import org.springframework.web.server.ResponseStatusException;
 * 
 * import java.util.*;
 *
 */

// Write your code here
package com.example.findmyproject.service;

import java.util.*;
import com.example.findmyproject.model.*;
import com.example.findmyproject.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProjectJpaService implements ProjectRepository {

    @Autowired
    private ProjectJpaRepository projectJpaRepository;

    @Autowired
    private ResearcherJpaRepository researcherJpaRepository;

    @Override
    public ArrayList<Project> getProjects() {
        List<Project> projectList = projectJpaRepository.findAll();
        ArrayList<Project> projects = new ArrayList<>(projectList);
        return projects;
    }

    @Override
    public Project getProjectById(int projectId) {
        try {
            Project project = projectJpaRepository.findById(projectId).get();
            return project;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public Project addProject(Project project) {
        List<Integer> researcherIds = new ArrayList<>();
        for (Researcher researcher : project.getResearchers()) {
            researcherIds.add(researcher.getResearcherId());
        }
        List<Researcher> researchers = researcherJpaRepository.findAllById(researcherIds);
        project.setResearchers(researchers);
        for (Researcher researcher : researchers) {
            researcher.getProjects().add(project);
        }
        researcherJpaRepository.saveAll(researchers);
        projectJpaRepository.save(project);
        return project;
    }

    @Override
    public Project updateProject(int projectId, Project project) {
        try {
            Project newProject = projectJpaRepository.findById(projectId).get();
            if (project.getProjectName() != null) {
                newProject.setProjectName(project.getProjectName());
            }
            if (project.getBudget() != 0) {
                newProject.setBudget(project.getBudget());
            }
            if (project.getResearchers() != null) {
                List<Researcher> oldResearchers = newProject.getResearchers();
                for (Researcher researcher : oldResearchers) {
                    researcher.getProjects().remove(newProject);
                }
                researcherJpaRepository.saveAll(oldResearchers);

                List<Integer> newResearchersIds = new ArrayList<>();
                for (Researcher researcher : project.getResearchers()) {
                    newResearchersIds.add(researcher.getResearcherId());
                }
                List<Researcher> newResearchers = researcherJpaRepository.findAllById(newResearchersIds);

                if (newResearchers.size() != newResearchersIds.size()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
                }
                newProject.setResearchers(newResearchers);
                for (Researcher researcher : newResearchers) {
                    researcher.getProjects().add(newProject);
                }
                researcherJpaRepository.saveAll(newResearchers);
            }
            projectJpaRepository.save(newProject);
            return newProject;
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public void deleteProject(int projectId) {
        try {
            Project project = projectJpaRepository.findById(projectId).get();
            List<Researcher> researchers = project.getResearchers();
            for (Researcher researcher : researchers) {
                researcher.getProjects().remove(project);
            }
            researcherJpaRepository.saveAll(researchers);
            projectJpaRepository.deleteById(projectId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        throw new ResponseStatusException(HttpStatus.NO_CONTENT);           
    }

    @Override
    public List<Researcher> getProjectResearchers(int projectId) {
        try {
            Project project = projectJpaRepository.findById(projectId).get();
            return project.getResearchers();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}