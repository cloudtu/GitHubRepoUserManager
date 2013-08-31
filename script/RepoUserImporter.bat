@echo on

set JAVA_HOME=C:\Java\jdk1.6.0_35
set path=%JAVA_HOME%\bin;%path%
java -Dfile.encoding=UTF-8 -cp ".;lib/*;*" cloudtu.github.RepoUserImporter
rem pause