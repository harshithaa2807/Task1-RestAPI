# Task1-RestAPI
Java REST API for task management

## Overview
This project implements a Java backend with a REST API for creating, executing, searching, and deleting "task" objects. Task objects represent shell commands that can be run. Each task contains:

- `id` (String): Task ID  
- `name` (String): Task name  
- `owner` (String): Task owner  
- `command` (String): Shell command to execute  
- `taskExecutions` (List<TaskExecution>): Execution details  

`TaskExecution` contains:
- `startTime` (Date): Start time of execution  
- `endTime` (Date): End time of execution  
- `output` (String): Command output  

---
## Prerequisites

- Java 17+  
- Maven 3.x  
- MongoDB running on `localhost:27017`  

---

## How to Compile & Run

1. Start MongoDB: Ensure `mongod` is running.  
2. Open terminal in the project root:  
```powershell
cd "your file path"

## Compile and run the project

mvn clean install
mvn spring-boot:run

Application will start at: http://localhost:8080/

###**API Endpoints**
## REST API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| PUT    | /tasks   | Create or update a task |
| GET    | /tasks   | Get all tasks |
| GET    | /tasks/{id} | Get task by ID |
| GET    | /tasks/search?name=xyz | Search tasks by name |
| PUT    | /tasks/{id}/execute | Execute task command |
| DELETE | /tasks/{id} | Delete task by ID |

1. Create task
$body = @{
    id = "123"
    name = "Print Hello"
    owner = "Harshitha R"
    command = "echo Hello World!"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/tasks" -Method Put -ContentType "application/json" -Body $body

2. Get All Tasks
Invoke-RestMethod -Uri "http://localhost:8080/tasks" -Method Get

3. Get Task by ID
Invoke-RestMethod -Uri "http://localhost:8080/tasks/123" -Method Get

4. Search Task by Name
Invoke-RestMethod -Uri "http://localhost:8080/tasks/search?name=Print" -Method Get

5. execute Task
Invoke-RestMethod -Uri "http://localhost:8080/tasks/123/execute" -Method Put

6. Delete Task
Invoke-RestMethod -Uri "http://localhost:8080/tasks/123" -Method Delete


