package rs.ananas.i18n.migration.tool.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import lombok.extern.log4j.Log4j2;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static java.lang.String.format;

@Log4j2
public final class IOUtil {

    public static final String JSON = "json";
    public static final String PROPERTIES = "properties";

    private IOUtil() {
    }

    public static String getUrl(String language, String namespace, String apiKey) {
        String base = "https://api.i18nexus.com/project_resources/translations/%s/%s.json?api_key=%s";
        return format(base, language, namespace, apiKey);
    }

    private static String getFileName(String fileName, String language, String extension) {
        return String.format("%s_%s.%s", fileName, language, extension);
    }

    public static List<String> getProjectNamespaces(String apiKey) throws IOException, InterruptedException {
        var url = String.format("https://api.i18nexus.com/project_resources/namespaces.json?api_key=%s", apiKey);
        return getValuesFromJasonFor("title", fetchContent(url));
    }

    public static List<String> getProjectLanguages(String apiKey) throws IOException, InterruptedException {
        var url = String.format("https://api.i18nexus.com/project_resources/languages.json?api_key=%s", apiKey);
        return getValuesFromJasonFor("language_code", fetchContent(url));
    }

    public static String fetchStringContent(String url) throws IOException, InterruptedException {
        var httpClient = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    private static byte[] fetchContent(String url) throws IOException, InterruptedException {
        return fetchStringContent(url).getBytes();
    }

    private static List<String> getValuesFromJasonFor(String elementName, byte[] contents) {
        List<String> values = new ArrayList<>(3);
        try {
            new ObjectMapper()
                    .readTree(contents)
                    .path("collection")
                    .elements()
                    .forEachRemaining(element -> values
                            .add(element
                                    .findValue(elementName)
                                    .asText()));

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return values;
    }

    public static void downloadTranslationFiles(String fileName, String projectName, String language, String extension, String content) throws IOException {

        Path directory = Paths.get(projectName);

        log.info("Creating file {} ...", getFileName(fileName, language, extension));
        if (!Files.exists(directory))
            Files.createDirectories(directory);

        if (JSON.equalsIgnoreCase(extension)) {
            saveToJsonFile(directory, fileName, language, content);
        } else
            saveToPropertiesFile(directory, fileName, language, content);
    }

    private static void saveToJsonFile(Path directory, String fileName, String language, String content) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String prettifiedJson = mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(mapper.readValue(content, Object.class));
        Files.writeString(Paths.get(directory.toString(), getFileName(fileName, language, JSON)), prettifiedJson);
    }

    private static void saveToPropertiesFile(Path directory, String fileName, String language, String content) throws IOException {

        Properties props = new JavaPropsMapper()
                .writeValueAsProperties(new ObjectMapper()
                        .readValue(content, Map.class));

        try (FileWriter fw =
                     new FileWriter(Paths.get(directory.toString(),
                             getFileName(fileName, language, PROPERTIES)).toFile())) {

            props.store(fw, "Auto generated file by migrator tool v0.1");
        }
    }

}
