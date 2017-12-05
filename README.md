# WebCrawler
WebCrawler is an web crawler based on Java. It provides
  some simple crawler for single seed url





## Installation

### Using Maven

### Run below comman from project directory, assume java & mvn available in your system.

mvn exec:java -Dexec.mainClass=com.fhd.webcrawler.Main  -Dexec.args="http://wiprodigital.com"
## output will be avaialble in wiprodigital.out file (hostname.out is the default naming convention used)

### to write the output to specific file, say myOutput.log
mvn exec:java -Dexec.mainClass=com.fhd.webcrawler.Main  -Dexec.args="http://wiprodigital.com myOutput.log"

