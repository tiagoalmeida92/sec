1 build shared

javac -d bin Shared\src\pt\meic\sec\*.java

2 build client

javac -cp .\bin -d bin .\Library\src\pt\meic\sec\*.java

3 build program

javac -cp .\bin -d bin src\pt\meic\sec\*.java

4 Running the program 

java -cp bin pt.meic.sec.Main

