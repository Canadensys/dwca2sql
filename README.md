dwca2sql
========

## Introduction

_Darwin Core to SQL_ (dwca2sql) is a lightweight tool to ease the importation of [Darwin Core archives](http://rs.tdwg.org/dwc/terms/guides/text/index.htm) into a relational database. It translates the structure and content of a Darwin Core Archive file into CREATE TABLE and/or INSERT INTO SQL statements, packaged as an .sql file, which you can then import in your database.

## How to use the tool?
You use the tool from the command line.

### Build the tool
```bash
mvn clean package
```

### Tool structure
Once built, the .jar file will be available in the `target` folder. If you want to move the tool, make sure to carry the `lib` folder.

### Arguments
| Argument | Description | Default |Mandatory|
| -------- | ----------- | ------- |-------  |
|-s <source file> |  Your Darwin Core Archive file (zipped) or folder (unzipped).  |   |yes|
|-c or -i or -ci	 |The type of SQL statements you want to generate: CREATE TABLE or INSERT INTO or both.||		 yes|
|-o <output file>	 |The path and name of the output SQL file.	| dwca2sql.sql in the current directory|	 no|
|-p <prefix>	 |A prefix for the generated table name. Will be combined with the core or extension name, e.g. prefix_occurrences.	|none|	 no|
|-d <database type>	 |Because not all vendors are following the SQL standard, you can specify a vendor for better results.	| postgres|	 no|
|--max-row-per-insert	 |Maximum number of rows to include in a single INSERT statement.|	 100|	 no|
|-f	 |Force the execution: do not ask before overwriting existing files.|	false|	 no|

### Examples
Create the SQL file:
```bash
java -jar dwca2sql.jar -ci -s /Users/JamesHowlett/Docs/MyDarwinCoreArchive.zip -o /tmp/unicorn.sql -p unicorn -d mysql
```

Import into MySQL:
```bash
mysql -hHOST -uUSER -pPASSWORD -DDATABASE_NAME --default_character_set utf8 -e "source /tmp/unicorn.sql"
```
## Known limitations
* Data types are taken from the meta.xml file, not from the Darwin Core definitions.
* Only mysql and postgres are implemented for database type.
* Only executable from the command line.

## Warning
The tool makes the assumption that you trust the Darwin Core Archive file that you're trying to import into your database. You use the tool at your own risk.

## Acknowledgements
These are the third-party software tools used for this tool:

| Component | Version | Developed by |License|Source|
| -------- | ----------- | ------- |-------  |-------  |
|DarwinCore Archive Reader|	1.9.1	| GBIF	| Apache License 2.0	|http://code.google.com/p/darwincore/wiki/DarwinCoreArchiveReader|
|Apache Commons IO|	2.1	| Apache|	 Apache License 2.0|	http://commons.apache.org/io/|
|Apache Commons CLI|	1.2	| Apache	| Apache License 2.0	|http://commons.apache.org/cli/|
|Apache Commons Lang|	3.1	| Apache	| Apache License 2.0|	http://commons.apache.org/lang/|
|Apache Commons Compress|	1.3	| Apache|	 Apache License 2.0	|http://commons.apache.org/compress/|
