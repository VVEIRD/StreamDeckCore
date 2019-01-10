pushd %~dp0
set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_152
call C:\Tools\apache-maven-3.6.0\bin\mvn clean
call C:\Tools\apache-maven-3.6.0\bin\mvn install
call C:\Tools\apache-maven-3.6.0\bin\mvn javadoc:javadoc
pause