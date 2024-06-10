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
public class ResearcherJpaService implements ResearcherRepository {

    @Autowired
    private ResearcherJpaRepository researcherJpaRepository;

    @Autowired
    private ProjectJpaRepository projectJpaRepository;

    @Override
    public ArrayList<Researcher> getResearchers() {
        List<Researcher> researcherList = researcherJpaRepository.findAll();
        ArrayList<Researcher> researchers = new ArrayList<>(researcherList);
        return researchers;
    }

    @Override
    public Researcher getResearcherById(int researcherId) {
        try {
            Researcher researcher = researcherJpaRepository.findById(researcherId).get();
            return researcher;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public Researcher addResearcher(Researcher researcher) {
        List<Integer> projectIds = new ArrayList<>();
        for (Project project : researcher.getProjects()) {
            projectIds.add(project.getProjectId()); // getting projectIds info from the new researcher
        }
        List<Project> projects = projectJpaRepository.findAllById(projectIds); // getting all the projects info from new
                                                                               // projectIds
        researcher.setProjects(projects); // updating projects info with complete projects info in researcher

        for (Project project : projects) {
            project.getResearchers().add(researcher); // updating project table with new researcher
        }
        projectJpaRepository.saveAll(projects); // saving new project table info

        researcherJpaRepository.save(researcher); // saving new researcher into researcher table
        return researcher; // returning the researcher info for the user
    }

    @Override
    public Researcher updateResearcher(int researcherId, Researcher researcher) {
        try {
            Researcher newResearcher = researcherJpaRepository.findById(researcherId).get();
            if (researcher.getResearcherName() != null) {
                newResearcher.setResearcherName(researcher.getResearcherName());
            }
            if (researcher.getSpecialization() != null) {
                newResearcher.setSpecialization(researcher.getSpecialization());
            }
            if (researcher.getProjects() != null) {
                List<Project> oldProjects = newResearcher.getProjects(); // getting oldprojects info of new researcher
                                                                         // that is to be updated
                for (Project project : oldProjects) {
                    project.getResearchers().remove(newResearcher); // removing the researcher info from old projects
                }
                projectJpaRepository.saveAll(oldProjects); // updating project table after the removal of new researcher

                List<Integer> newProjectIds = new ArrayList<>();
                for (Project project : researcher.getProjects()) {
                    newProjectIds.add(project.getProjectId()); // getting the new projectids from researcher info that
                                                               // is to be updated
                }
                List<Project> newProjects = projectJpaRepository.findAllById(newProjectIds); // getting the new projects
                                                                                             // complete info using new
                                                                                             // project ids

                if (newProjectIds.size() != newProjects.size()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST); // checking for any bad requests from
                                                                               // client side
                }
                researcher.setProjects(newProjects); // updating the researcher info with new projects
                for (Project project : newProjects) {
                    project.getResearchers().add(newResearcher);//adding new researcher info to new projects
                }
                projectJpaRepository.saveAll(newProjects);//updating project table info
            }
            researcherJpaRepository.save(newResearcher);//saving the new researcher info in researcher table
            return newResearcher; //returning new researcher info that is updated to the client 
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public void deleteResearcher(int researcherId) {
        try {
            Researcher researcher = researcherJpaRepository.findById(researcherId).get();
            List<Project> projects = researcher.getProjects();
            for (Project project : projects) {
                project.getResearchers().remove(researcher);
            }
            projectJpaRepository.saveAll(projects);
            researcherJpaRepository.deleteById(researcherId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        throw new ResponseStatusException(HttpStatus.NO_CONTENT);
    }

    @Override
    public List<Project> getResearcherProjects(int researcherId) {
        try {
            Researcher researcher = researcherJpaRepository.findById(researcherId).get();
            return researcher.getProjects();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

}