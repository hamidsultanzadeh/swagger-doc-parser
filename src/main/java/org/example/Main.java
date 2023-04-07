package org.example;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws IOException {
        Main main = new Main();
        Yaml yaml = new Yaml();
        List<String> fileNames = main.getFileNames();

        Map<String, Object> resultObj = null;
        for (String fileName : fileNames) {
            Map<String, Object> obj = main.load(yaml, fileName);
            if (resultObj == null) {
                resultObj = obj;
                continue;
            }
            comparePathObj(obj, resultObj);
            compareSchemasObj(obj, resultObj);
        }

        String output = convertMapToString(resultObj);
        writeOutputToFile(output);
    }

    static void writeOutputToFile(String output) throws IOException {
        BufferedWriter writer = null;
        writer = new BufferedWriter(new FileWriter("result.yaml"));
        writer.write(output);
        writer.close();
    }

    static String convertMapToString(Map<String, Object> resultObj) {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);

        Yaml yaml1 = new Yaml(options);
        return yaml1.dump(resultObj);
    }

    @SuppressWarnings("unchecked")
    static void replace(List<?> list) {
        for (Object obj : list) {
            if (obj instanceof Map) {
                replace((Map<String, Object>) obj);
            }
        }
    }

    @SuppressWarnings("unchecked")
    static void replace(Map<String, Object> map) {
        map.forEach((k, v) -> {
            if (v instanceof Map || v instanceof List) {
                if (v instanceof Map) {
                    replace((Map<String, Object>) v);
                } else {
                    replace((List<?>) v);
                }
            } else if (v instanceof String) {
                String[] values = ((String) v).split(".yaml");
                map.put(k, values.length > 1 ? values[1] : values[0]);
            }
        });
    }

    List<String> getFileNames() {
        List<String> fileNames = new ArrayList<>();

        File folder = new File("/home/ubuntu/Workspace/teamprojects/swagger-documentation-parser/src/main/resources");
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) return fileNames;

        for (File file : listOfFiles) {
            if (file.isFile()) {
                fileNames.add(file.getName());
            }
        }

        return fileNames;
    }

    @SuppressWarnings("unchecked")
    static void comparePathObj(Map<String, Object> obj, Map<String, Object> resultObj) {
        Map<String, Object> pathsObj = (Map<String, Object>) obj.get("paths");
        if (pathsObj == null) return;
        Map<String, Object> resultPathsObj = (Map<String, Object>) resultObj.get("paths");
        pathsObj.forEach((k, v) -> {
            if (!resultPathsObj.containsKey(k)) {
                replace((Map<String, Object>) v);
                resultPathsObj.put(k, v);
            }
        });
    }

    @SuppressWarnings("unchecked")
    static void compareSchemasObj(Map<String, Object> obj, Map<String, Object> resultObj) {
        Map<String, Object> componentsObj = (Map<String, Object>) obj.get("components");
        if (componentsObj == null) return;
        Map<String, Object> schemasObj = (Map<String, Object>) componentsObj.get("schemas");
        if (schemasObj == null) return;
        Map<String, Object> resultComponentsObj = (Map<String, Object>) resultObj.get("components");
        Map<String, Object> resultSchemasObj = (Map<String, Object>) resultComponentsObj.get("schemas");
        schemasObj.forEach((k, v) -> {
            if (!resultSchemasObj.containsKey(k)) {
                replace((Map<String, Object>) v);
                resultSchemasObj.put(k, v);
            }
        });
    }

    Map<String, Object> load(Yaml yaml, String fileName) {
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(fileName);
        return yaml.load(inputStream);
    }
}