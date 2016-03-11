1 build shared

javac -d bin SharedProject\src\Utils\*.java

2 build server

javac -cp .\bin -d bin BlockServerProject\src\*.java


4 Running the program 

java -cp bin ServerMain