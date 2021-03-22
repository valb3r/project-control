#!/bin/bash

. environment.sh
# Force TLSv1.2 due to https://stackoverflow.com/questions/60955206/java-11-sslhandshakeexception-with-tls-1-3-how-do-i-revert-to-tls-1-2
java -Dserver.ssl.enabled-protocols=TLSv1.2 -jar project-control-*.jar --spring.profiles.active=ssl