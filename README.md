# What is this 

A tool to assess the effort spent to develop software project and its readiness.

# DEMO site credentials

Credentials: `admin/admin`

### Analyzes and graphically represents

1. Commit count per developer per week
1. Churn (lines changed) per developer per week
1. Owned code per developer per week
1. Rework code done by developers


# Building project

## Build (with tests)

```shell
./gradlew clean syncNpm build
```

## Build and run (without tests)

```shell
./gradlew clean syncNpm build -x test bootRun
```

## Build monolith JAR

```shell
./gradlew clean syncNpm -x test bootJar
```

## Build docker

```shell
./gradlew clean syncNpm syncJar buildImage
```

# Running project

0. Ensure you have Java 11 installed
1. Start Neo4j database i.e. using docker (ready to use sample deployment is in [docker-compose.yml](neo4j-docker-compose/docker-compose.yml))
2. Do [Build and run (without tests)](#build-and-run-without-tests) to start application
3. Open UI at [http://localhost:8080](http://localhost:8080)
4. Login with `admin/admin` credentials
   
![login with admin/admin](docs/img/login.png)

5. Click on `Add Project Button` (Open `Projects` page from menu if necessary)
   
![Click on Add Project Button](docs/img/add_button.png)


5. Fill in new project to analyze details, click `Add` button when done

![Fill in new project to analyze details and then Add](docs/img/setup_new_project.png)

6. Start project analysis - click `Restart analysis button`

![Click `Restart analysis button`](docs/img/start_analysis.png)

7. Wait for analysis to be finished

![Click `Wait for analysis to be finished`](docs/img/finished_state.png)

8. Create new user from aliases - open `User mappings`

![Click `User mappings`](docs/img/open_user_mappings.png)

9. Create new user from aliases - Create new user

![Click `Create new user`](docs/img/new_user.png)

9. Create new user from aliases - merge aliases to user

![Click `Merge aliases to user`](docs/img/merge_aliases_to_user.png)

10. See the reports

![Click `Reports`](docs/img/browse_reports.png)


# Exclusion/Inclusion rule examples

## Inclusion:

```drools
package com.project_control.rules

import  com.valb3r.projectcontrol.domain.rules.RuleContext

dialect  "mvel"


rule "Only .java files"
    no-loop
    when
       $c: RuleContext(path matches '.+\\.java')
    then
       $c.include = true;
       update($c)
end
```

## Exclusion:

```drools
package com.project_control.rules

import  com.valb3r.projectcontrol.domain.rules.RuleContext

dialect  "mvel"


rule "Not in resources files"
    no-loop
    when
       $c: RuleContext(path matches '.+/resources/.+')
    then
       $c.exclude = true;
       update($c)
end
```
