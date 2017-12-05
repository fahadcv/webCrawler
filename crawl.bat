set seedurl=%1
mvn exec:java -Dexec.mainClass=com.fhd.webcrawler.Main  -Dexec.args=%seedurl%