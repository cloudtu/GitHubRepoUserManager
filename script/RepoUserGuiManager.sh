#!/bin/sh

JAVA_HOME=/opt/jdk1.6.0
PATH=$JAVA_HOME/bin:$PATH
cd /opt/GitHubTool/
javaw -Dfile.encoding=UTF-8 -cp ".:lib/*:*" cloudtu.github.RepoUserGuiManager
