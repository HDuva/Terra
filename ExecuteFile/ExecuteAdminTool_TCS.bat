@echo off
set projectLocation=E:\Automation\Project\Terra
cd %projectLocation%
set classpath=%projectLocation%\src\main\java;%projectLocation%\Dependency*

java org.testng.TestNG %projectLocation%\TestSuite\SmokeTest.xml

pause