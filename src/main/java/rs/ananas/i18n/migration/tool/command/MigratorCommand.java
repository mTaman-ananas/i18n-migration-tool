package rs.ananas.i18n.migration.tool.command;

import lombok.extern.log4j.Log4j2;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static rs.ananas.i18n.migration.tool.util.IOUtil.*;
import static rs.ananas.i18n.migration.tool.util.ObjectUtil.isEmpty;
import static rs.ananas.i18n.migration.tool.util.ObjectUtil.requireNonEmpty;

@ShellComponent
@Log4j2
public class MigratorCommand {

    private String apiKey;
    private String projectName;
    private String fileName;
    private String format;

    public static boolean isValidFileFormat(String format) {
        requireNonEmpty(format, "File format can't be empty");
        return Stream.of(JSON, PROPERTIES)
                .anyMatch(result -> result.equalsIgnoreCase(format));
    }

    @ShellMethod(value = """ 
            Build i18Nexus project remote server, and migration configuration.
             The you can use the following parameters:
                - API key: i18Nexus project API Key, mandatory field.
                - Project name: Project name, so all files will be saved inside this folder. Default "i18n".
                - File name: File name to save content to. default "messages".
                - File format: final out file content and file format. Default ".properties"
            """)
    public void config(@ShellOption String apiKey,
                       @ShellOption(defaultValue = "i18n") String projectName,
                       @ShellOption(defaultValue = "messages") String fileName,
                       @ShellOption(defaultValue = "properties") String format) {

        if (!isValidFileFormat(format))
            throw new InvalidFileFormat("Invalid file format: " + format);

        this.apiKey = requireNonEmpty(apiKey, "API Key can't be null or empty.");
        this.projectName = requireNonEmpty(projectName, "Project name can't be empty.");
        this.fileName = requireNonEmpty(fileName, "file name can't be empty.");
        this.format = format.toLowerCase();

        log.info("Configurations: [API Key: '{}', Project name: '{}', File name: '{}', File format '{}']",
                this.apiKey, this.projectName, this.fileName, this.format);
    }

    @ShellMethod("Download content and create the files.")
    public void download() throws IOException, InterruptedException {
        log.info("Current Configs: [Project APIKey/name: '{}'/'{}', File name/format: '{}'/'{}']",
                this.apiKey, this.projectName, this.fileName, this.format);

        log.info("Connecting to i18Nexus for project {{}}...", this.apiKey);

        List<String> languages = new ArrayList<>(getProjectLanguages(this.apiKey));
        log.info("Fetched languages [{}].", languages);

        List<String> namespaces = new ArrayList<>(getProjectNamespaces(this.apiKey));
        log.info("Fetched namespaces [{}].", namespaces);

        StringBuilder content;

        if (!(languages.isEmpty() || namespaces.isEmpty()))
            log.info("Fetching content & Creating files...");
        else
            log.info("No content to fetch, then no files to create!");


        for (String language : languages) {
            content = new StringBuilder();
            for (String namespace : namespaces) {
                var result = fetchStringContent(getUrl(language, namespace, this.apiKey));
                if (!isEmpty(result)) {
                    content.append(result);
                }
            }
            if (!content.isEmpty())
                downloadTranslationFiles(fileName, projectName, language, format, content.toString());
        }

        log.info("Done.");
    }

    public Availability downloadAvailability() {
        return apiKey != null && !apiKey.isBlank()
                ? Availability.available()
                : Availability.unavailable("you are not configured.");
    }


}
