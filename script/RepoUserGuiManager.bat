@echo on

set JAVA_HOME=C:\Java\jdk1.6.0_35
set path=%JAVA_HOME%\bin;%path%
start javaw -Dfile.encoding=UTF-8 -cp ".;lib/*;*" cloudtu.github.RepoUserGuiManager
rem pause