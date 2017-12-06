# WebCrawler
WebCrawler is an web crawler based on Java. It provides simple crawler for single seed url.

## Installation

### Using Maven
#### Pre-requests: java and maven to be installed.
#### Run the below command from project directory to build and run the unit test.
mvn test
### After the build/test, Run the below command.

mvn exec:java -Dexec.mainClass=com.fhd.webcrawler.Main  -Dexec.args="http://google.com"
#### Output will be available in google.com.out file (hostname.out is the default naming convention used for output file)

#### To write the output to specific file, say myOutput.log
mvn exec:java -Dexec.mainClass=com.fhd.webcrawler.Main  -Dexec.args="http://google.com out=faile:myOutput.log"

### To simplify, Alternatively run the below script.
#### In Windows
crawl.bat <Seed URL>
crawl.bat http://google.com

#### In Linux / Unix
./crawl.sh <Seed URL>
crawl.sh http://google.com

###Notes: 
1) We are not doing the parallel processing, since most of the domain have control on requests to defend DoS attack. 
So we will not be getting much benefit on parallel processing on single domain.

2) If the time permits 
    1) We may enhance the output to json format and later persist into NoSQL/Elastic search.
    2) More regress validation and fault tolerance to be done.
    3) Need to respect robots.txt crawling rules.
 



