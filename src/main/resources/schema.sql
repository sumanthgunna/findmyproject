create table if not exists researcher (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255),
    specialization VARCHAR(255)
);

create table if not exists project (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255), 
    budget INT 
);

create table if not exists researcher_project (
    projectId INT,
    researcherId INT,
    PRIMARY KEY (projectId, researcherId),
    FOREIGN KEY (projectId) REFERENCES project(id),
    FOREIGN KEY (researcherId) REFERENCES researcher(id)
);