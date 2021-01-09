# What is this 

A tool to assess the effort spent to develop software project and its readiness.

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