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
Data Visualization is a key factor to make use of the insights gained from the recent advances in data analysis. While most research in this field focusses on the visualization of cardinal or categorical data the visualization of textual and semi-structured data was neglected in the past. Our goal is to design a two-phase parallel highlighting strategy that calculates the position information from various sources of similarity in an integrated way and inserts highlighting markers for the visual presentation in a second phase. Thus, many applications will benefit from a well-designed method that separates between addressing the match locations and inserting highlighting markers. We also treat the edge cases, overlapping, multi-match and others.

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

java -cp pds-xmlph-parent-0.0.1-SNAPSHOT-jar.jar pds.TextFeatureProcess_extract input_document.xml
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





