# What is this 

A tool to assess the effort spent to develop software project and its readiness.


# Building project

## Build

```shell
./gradlew clean syncNpm build
```

## Build and run

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