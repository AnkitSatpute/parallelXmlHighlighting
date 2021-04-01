# XMLPH  - XML Parallel Highlighting

Command line Tool to Record, Annotate, and Visualize and Parallel Structures in XML Documents

## Table of Contents

- [Motivation](#motivation)
- [Project Status](#project-status)
- [How does it work?](#how-does-it-work)
- [Usage](#usage)
- [Install](#install)
- [Development](#development)
- [Feature Roadmap](#feature-roadmap)
- [Maintainers](#maintainers)
- [License](#license)


## Motivation
Many industries and scientific communities have adopted XML documents as a standard for representing, storing, and exchanging data. The Text Encoding Initiative (TEI) has also become a de facto standard within the humanities, where it is used, for example, to encode printed works (edition science) or to mark up linguistic information (linguistics) in texts. However, all these data are not static, and even semistructured data evolve. In particular, an analysis of such XML documents poses further challenges to the scientific community. Also, the use of hierarchical structures and user-defined tags allows for flexible data representation. However, this also leads to challenges in comparing and analyzing XML documents. This problem has already been studied in the literature.  
However, such data analyses are elementary, for example, identifying potentially suspicious similarities in scientific documents. Another area of data analyses relates to digital editions, the core area of digital humanities, investigations of similarities, and general comparisons in complex textual and semistructured datasets are helpful for further research questions and reuse. Moreover, (semi-)automatic processing steps (e.g., text recognition in manuscripts and inscriptions, heuristic and inferential statistical detection of structural relationships in and empirical analyses of language and text corpora, etc.) and their systematic evaluation (e.g., image analysis, metadata enrichment, directed information, graphical models, word embeddings, interaction and social networks, etc.) are of elementary importance in digital editions. 
In addition to the challenges of comparing and analyzing XML documents and their content, we also face challenges when something within an XML document should be annotated, changed, or deleted. This would occur, for example, if two XML documents were to be compared for similarities, and then the similarities were to be highlighted.

## Our 

## How does it work?

This project uses Maven (https://maven.apache.org). We stay by the default maven convention for a multi-module project to attain a functional separation.
As an unique reference / group namespace for all modules the Maven Group ID org.sciplore.pds.backend is used.
-   groupId org.sciplore.pds
-   artifactId pds-xmlph-parent
-   version <main>.<major>[-SNAPSHOT]
  
  
Used Technologies
-----------------
- Java 8 (https://www.oracle.com/de/java/)
- Maven 3 (https://maven.apache.org/plugins/maven-install-plugin/usage.html)


## Usage

Generating the executable jar file via maven
  -> mvn install

As soon as the jar file has been created, the application can be executed accordingly via the command line

java -cp pds-xmlph-parent-0.0.1-SNAPSHOT-jar.jar org.sciplore.TextFeatureProcess_extract input_document.xml
 -> filename for the input-xml-file:  input_document.xml 

java -cp pds-xmlph-parent-0.0.1-SNAPSHOT-jar.jar pds.TextFeatureProcess_extract output_plain.txt modify_plain.txt output_mapping.txt input_document.xml
 -> filename for the output-txt-file:  output_plain.txt
 -> filename for the modify-xml-file:  output_plain.txt
 -> filename for the output_mapping-file:  output_mapping.txt
 -> filename for the input-xml-file:  input_document.xml



## Maintainers

[Marco Beck](https://github.com/BeckMarco).


## License

We use the Apache 2.0 Licence. All dependent libraries use the same or similar license.
© Marco Beck





