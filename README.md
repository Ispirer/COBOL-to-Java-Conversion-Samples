# COBOL to Java Conversion Samples
The migration from COBOL to Java poses a considerable challenge owing to substantial disparities between the two languages. The samples which we are sharing will be beneficial for those who are discovering COBOL to Java migration basics.

The repository above contains examples of code migration from COBOL to Java. Please note that the conversion result is received automatically using Ispirer Toolkit. This way you can evaluate the quality of the automatic conversion and decide whether to consider it as a solution for your project.

## Repository contents
The repository contains three folders: COBOL, Java, IspirerFramework. The **COBOL folder** contains 9 source COBOL files. This is a “sample” application that contains some typical Oracle operations. PROGRAM DEMO.cb is the main program that connects to the database, calls 3 subroutines and shuts down. SKEFYside - creates a table and fills it with data, MNPROCESS.sid reads data from the AVATAR file and enters it into the database table, in addition, it deletes the AVGRPR.cpu, AVREPREC.cpu, PHYSX.cpu table files with structure declarations, and db_script.sql file with the Oracle database queries used in the example (i.e. in COBOL).

The **Java folder** contains the conversion result, which is distributed in two more folders: programs (with converted programs, i.e. .cbl) and models (with converted structures used in programs, i.e. .cpy). The result code uses JDBC to work with databases. However, Ispirer Toolkit can automatically convert COBOL to Java code using Hibernate. You can independently study the source and result code and find out what the tool has transformed into what. A little further on, we will tell you a bit more about Ispirer’s approach and capabilities.

The **IspirerFramework folder** contains the Ispirer framework for [COBOL to Java migration](https://www.ispirer.com/application-conversion/cobol-to-java-conversion). In COBOL, there are constructions or system functions of COBOL for which there are no analogs in Java. We developed these analogs ourselves. To make them convenient to use for automatic conversion and to make the result code readable and maintainable, we put these developments into the framework. It comes complete with a [tool](https://www.ispirer.com/products/nglfly-app-conversion). 

## Conversion overview
Ispirer Toolkit seamlessly transforms COBOL code into Java, tailoring the conversion to the unique features of each language. Following conversion, a Java class is generated for every source file (.cbl, .cob, .cpy). The class name is derived from the PROGRAM-ID specified in the Identification division, adhering to Java's camelCase convention. Variables from the Data division transition into class variables, while paragraphs from the Procedure division become class methods. In cases of using COBOL structures, corresponding additional classes are created in Java.

Moreover, the tool effectively handles COPY and INCLUDE clauses, crafting a class object based on the conversion of the header file. During this process, dependencies and data types from the header files are automatically considered.

Ispirer Toolkit thoroughly analyzes object dependencies throughout the conversion process, offering not just line-by-line conversion but also handling of type conversions. Furthermore, the software intelligently identifies and transforms essential inheritance dependencies. It meticulously processes the entire source code, constructing an internal tree that encapsulates comprehensive information about the objects, subsequently leveraging this data in the migration process.

To ensure a high degree of automation and foster maintainable code, we've developed a specialized Java framework, integral to the outcomes of the conversion process.

### Variable Transformation
Ispirer Toolkit converts COBOL data types, considering PICTURE (PIC) and USAGE clauses. To ensure precise data type alignment, generic Java classes are employed.

### Code Transformation
Every paragraph is converted to a corresponding class method. Meanwhile, the call and execution of each method inherently account for the potential utilization of PERFORM and GO TO statements. COBOL statements and functions lacking direct Java counterparts are converted into methods within the tool’s framework class structure.

### Database Operations
When dealing with databases in a COBOL application, the transition can involve converting to JDBC or Hibernate. Furthermore, if the Java application requires interaction with a different database, both the embedded SQL and the database structure can be converted. 

### File Handling
Ispirer Toolkit effortlessly facilitates the transformation of all three file types: sequential, indexed, and relative. Consequently, file algorithms are automatically transferred from COBOL to Java preserving the initial logic.
