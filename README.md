# Internationalization (**i18n**) Migration Tool

**Migrator v1**: is a command line tool to help you create translation (`.properties` or `.json`) files from the **i18Nexus** collaboration tool.

## Getting Started

To start with the migrator tool, first, you need to build it and then use it. The tool is a shell-like interactive tool, so you start to use it by running the tool jar file, and a migrator prompt will appear ```Migrator ==>```; then you can use the following commands:

1. `config`:  it will allow you to configure the shell by passing some parameters.
2. `migrate`: this command will fetch the content for a specific project and create files (per language) based on supplied configurations.

This tool has a memory to remember the commands history for all the commands you were using, and it supports an auto-complete feature.

### Build Migrator

You can build the tool and use it on your own. But also you can use it directly by downloading the jar file and running it as described in the section *Use Migrator* below.

#### Tools

To build and use the migrator tool, you probably need the following software installed on your machine:

1. Maven v3.9+, and you can download it from [here](https://maven.apache.org/download.cgi). Only required for building the tool.
2. Java JDK 17+, and you can download it from [here](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html).

#### Build The Tool

To build the tool on your own

1. Clone the project:

   ```bash
   $ git clone https://github.com/mTaman-ananas/i18n-migration-tool.git
   ```
2. Clean:

   ```bash
   $ mvn clean
   ```
3. Build:

   ```bash
   $ mvn verify
   ```
4. Run:

   ```bash
   $ cd target
   $ java -jar i18n-migration-tool-0.0.1-SNAPSHOT.jar
   Migrator ==>
   ```

### Using Migrator

Now let us see how we can run this tool.

1. First, ensure you have Java (you don't need maven here).

   ```bash
   $ java --version
   java 19.0.1 2022-10-18
   Java(TM) SE Runtime Environment (build 19.0.1+10-21)
   Java HotSpot(TM) 64-Bit Server VM (build 19.0.1+10-21, mixed mode, sharing)
   ```
2. Download the migrator v1 tool's latest version from here.
3. Open the command prompt, and run your file as the following:

   ```bash
   $ cd downloads
   $ java -jar i18n-migration-tool-0.0.1-SNAPSHOT.jar
   Migrator ==>
   ```
4. Type help to see available commands:

   ```bash
   Migrator ==> help
   AVAILABLE COMMANDS

   Built-In Commands
          help: Display help about available commands
          stacktrace: Display the full stacktrace of the last error.
          clear: Clear the shell screen.
          quit, exit: Exit the shell.
          history: Display or save the history of previously run commands
          version: Show version info
          script: Read and execute commands from a file.

   Migrator Command
          config: To configure the migrator too with the following parameters:
              - API key (--apiKey): i18Nexus project API Key, The only mandatory parameter.
              - Project name (--projectName): all the files will be saved inside this folder. The default value is "i18n".
              - File name (--fileName): to save fetched content to it. The default value is "messages".
              - File format (--format): is the final file content type and format [JSON, PROPERTIES]. The default is "properties"

          * migrate: Download content and create the files. This command is available once you configure the tool.


   Commands marked with (*) are currently unavailable.
   Type `help <command>` to learn more.
   ```
5. Trick: you can use `tab` to auto-complete commands and command parameters as the following, type `c` then `tab`, and `dash (-)` then `tab`:

   ```bash
   Migrator ==> config -
   --apiKey        --fileName      --format        --help          --projectName   -h
   ```
6. The following command is the full configuration with `config` command `config --apiKey "fKVUc_ZNsooPIeOGU_xe-w" --fileName "lang" --format "JSON" --projectName "adirect_service"`, but next, I will rely on defaults and only use`--apiKey`:

   ```bash
   Migrator ==> config --apiKey "fKVUc_ZNsooPIeOGU_xe-w"
   2023-03-16T10:26:45.926+01:00: Configurations: [API Key: 'fKVUc_ZNsooPIeOGU_xe-w', Project name: 'i18n', File name: 'messages', File format 'properties']
   ```
7. Now you can use the `migrate` command to fetch the content from i18nexus related to previously configured API-KEY, and combine all the project namespaces per each language into a file ( for example, `messages_en.properties`) created under the `i18n` folder:

   ```bash
   Migrator ==> migrate

   2023-03-16T10:43:20.613+01:00: Current Configs: [Project APIKey/name: 'fKVUc_ZNsooPIeOGU_xe-w'/'i18n', File name/format: messages'/'properties']
   2023-03-16T10:43:20.614+01:00: Connecting to i18Nexus for project {fKVUc_ZNsooPIeOGU_xe-w}...
   2023-03-16T10:43:21.730+01:00: Fetched languages [[en, mk, sr]].
   2023-03-16T10:43:22.528+01:00: Fetched namespaces [[default, excel]].
   2023-03-16T10:43:22.529+01:00: Fetching content & Creating files...
   2023-03-16T10:43:23.946+01:00: Creating file messages_en.properties ...
   2023-03-16T10:43:25.268+01:00: Creating file messages_mk.properties ...
   2023-03-16T10:43:26.664+01:00: Creating file messages_sr.properties ...
   2023-03-16T10:43:26.680+01:00: Done.
   ```
8. Finally, when you finish with tool exit: `Migrator ==> exit`.

Congratulations 😄, the i18n folder and its files are ready to move to your application.
