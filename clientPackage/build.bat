mkdir bin
copy .\Shared\libs\*.jar bin
copy .\Library\libs\*.jar bin
javac -cp "Shared\libs\*" -d bin Shared\src\pt\meic\sec\*.java
javac -cp "bin;Library\libs\*" -d bin Library\src\pt\meic\sec\*.java
javac -cp "bin" -d bin src\pt\meic\sec\*.java
