@echo on

start javaw -XX:+UseParallelGC -Dfile.encoding=UTF-8 -cp ".;lib/*;*" cloudtu.github.RepoUserGuiManager
rem pause