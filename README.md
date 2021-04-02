# XMLPH  - XML Parallel Highlighting

Command line Tool to Record, Annotate, and Visualize and Parallel Structures in XML Documents

## Table of Contents

- [Motivation](#motivation)
- [Our Approach](#our-approach)
- [How does it work?](#how-does-it-work)
- [Usage](#usage)
- [Feature Roadmap](#feature-roadmap)
- [Maintainers](#maintainers)
- [Contributing](#contributing)
- [License](#license)


## Motivation
Many industries and scientific communities have adopted XML documents as a standard for representing, storing, and exchanging data. The Text Encoding Initiative (TEI) has also become a de facto standard within the humanities, where it is used, for example, to encode printed works (edition science) or to mark up linguistic information (linguistics) in texts. However, all these data are not static, and even semistructured data evolve. In particular, an analysis of such XML documents poses further challenges to the scientific community. Also, the use of hierarchical structures and user-defined tags allows for flexible data representation. However, this also leads to challenges in comparing and analyzing XML documents. This problem has already been studied in the literature.  
However, such data analyses are elementary, for example, identifying potentially suspicious similarities in scientific documents. Another area of data analyses relates to digital editions, the core area of digital humanities, investigations of similarities, and general comparisons in complex textual and semistructured datasets are helpful for further research questions and reuse. Moreover, (semi-)automatic processing steps (e.g., text recognition in manuscripts and inscriptions, heuristic and inferential statistical detection of structural relationships in and empirical analyses of language and text corpora, etc.) and their systematic evaluation (e.g., image analysis, metadata enrichment, directed information, graphical models, word embeddings, interaction and social networks, etc.) are of elementary importance in digital editions. 
In addition to the challenges of comparing and analyzing XML documents and their content, we also face challenges when something within an XML document should be annotated, changed, or deleted. This would occur, for example, if two XML documents were to be compared for similarities, and then the similarities were to be highlighted.

## Approach
Our goal was to develop a four-stage parallel highlighting strategy in which an XML or HTML document is first decomposed into plain text, formulas, and images. These elements are processed with appropriate external algorithms for text similarity or difference analysis. After the elements are processed with external algorithms, these elements can be concatenated with the original XML document's positions to form minimal, non-overlapping elements. To avoid highlighting constraints, matching groups are introduced, as well as the corresponding tags are moved, and the part to be highlighted is split accordingly.  Thus, many applications will benefit from a well-designed method that separates addressing matching locations from inserting highlighting. In our approach, we also consider edge cases, overlaps, as well as MultiMatch. Our approach and tool is a further development of HyPlag (https://hyplag.org/), which provides a template or command-line tool for extracting different data streams from the XML document, annotating them, and then reassembling the original XML tags with the plain text, images, and formulas using our algorithm. 

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

As soon as the jar file has been created, the application can be executed accordingly via the command line:
-----------------------------------------------------------------------------------------------------------

Step 1: to extract the different data stream from the XML Document:

java -cp pds-xmlph-parent-0.0.1-SNAPSHOT-jar.jar org.sciplore.TextFeatureProcess_extract input_document.xml
 -> filename for the input-xml-file:  input_document.xml 

Step 2: the different files will be merged again and an xml document will be output:

java -cp pds-xmlph-parent-0.0.1-SNAPSHOT-jar.jar pds.TextFeatureProcess_extract output_plain.txt modify_plain.txt output_mapping.txt input_document.xml
 -> filename for the output-plain-txt-file:  output_plain.txt
 -> filename for the modify-plain-txt-file:  output_plain.txt
 -> filename for the output_mapping-file:  output_mapping.txt
 -> filename for the input-xml-file:  input_document.xml

## Feature Roadmap
- Of course, space can be output at this point during extraction, but then the calculated positions would no longer be correct during the modified text's subsequent composition and the XML tags. Possibly noting the position at which the blank is inserted with the extraction could help here so that with the assembling again, this position can be determined. Then the position of the XML tags can be corrected accordingly around the blanks.
-A further challenge exists in the topic if in the plain text words are inserted at the edge of an XML tag that the algorithm cannot recognize so far, into which XML tag the new word is to be inserted.  Further considerations are required at this point.

## Maintainers

[Marco Beck](https://github.com/BeckMarco).


## Contributing

Feel free to dive in!

## License

We use the Apache 2.0 Licence. All dependent libraries use the same or similar license.
© Marco Beck





