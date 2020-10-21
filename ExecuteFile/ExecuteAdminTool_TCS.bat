@echo off
set projectLocation=F:\Terra\TerraFramework
cd %projectLocation%
set classpath=%projectLocation%\src\main\java;%projectLocation%\Dependency\org\testng\testng\7.3.0\*

@echo on
mvn clean test -DsuiteXmlFile=%projectLocation%\TestSuite\RunSuite.xml

pause