mkdir bin
copy .\Libs\*.jar bin
copy .\*.cer bin
copy .\*.crl bin
javac -cp "Libs\*" -d bin SharedProject\src\Utils\*.java
javac -cp "bin" -d bin BlockServerProject\src\*.java