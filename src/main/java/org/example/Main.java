package org.example;

import org.example.constants.Constant;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.constants.Constant.RESOURCES_FOLDER;


public class Main {

    private final static StringBuilder stringBuilder = new StringBuilder();

    public static void main(String[] args) throws IOException {
        merge();
    }

    static void merge() throws IOException {
        doMerge(getFileNames());
    }

    static void doMerge(List<String> fileNames) throws IOException {
        Yaml yaml = new Yaml();

        Map<String, Object> resultObj = load(yaml, "init.yaml");

        for (String fileName : fileNames) {
            Map<String, Object> obj = load(yaml, fileName);
            comparePathObj(obj, resultObj);
            compareSchemasObj(obj, resultObj);
        }

        if(resultObj!=null) {
            generateAndPutInfoObj(resultObj);
            writeOutputToFile(
                    convertMapToString(resultObj),
                    Constant.RESULT_FILE_NAME);
        }
    }

    static void writeOutputToFile(String output, String fileName) throws IOException {
        BufferedWriter writer = null;
        writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(output);
        writer.close();
    }

    static String convertMapToString(Map<String, Object> resultObj) {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);

        Yaml yaml = new Yaml(options);
        return yaml.dump(resultObj);
    }

    @SuppressWarnings("unchecked")
    static void replace(List<?> list) {
        for (Object obj : list) {
            if (obj instanceof Map) {
                replaceAndLog((Map<String, Object>) obj);
            }
        }
    }

    @SuppressWarnings("unchecked")
    static void replaceAndLog(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Map || value instanceof List) {
                if (value instanceof Map) {
                    checkAndReplaceContent(entry);
                    replaceAndLog((Map<String, Object>) value);
                } else {
                    replace((List<?>) value);
                }
            } else if (value instanceof String)
                doReplace(map, entry.getKey(), (String) value);
        }
        try {
            writeOutputToFile(stringBuilder.toString(), Constant.REPLACE_LOG_FILE_NAME);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void doReplace(Map<String, Object> map, String key, String value) {
        String[] values = value.split(Constant.YAML_EXTENSION);
        String currValue = values.length > 1 ? values[1] : values[0];
//        if (Constant.REF.equals(key)) {
//            String editedValue = currValue.replaceAll(Constant.REPLACE_PATTERN, Constant.DEFAULT_STRING);
//            if (!currValue.equals(editedValue)) {
//                stringBuilder.append(
//                        String.format(Constant.REPLACE_SENTENCE, currValue, editedValue));
//                currValue = editedValue;
//            }
//        }
        map.put(key, currValue);
    }

    @SuppressWarnings("unchecked")
    static void checkAndReplaceContent(Map.Entry<String, Object> entry) {
        if (Constant.RESPONSES.equals(entry.getKey())) {
            Map<String, Object> resourcesMap = (Map<String, Object>) entry.getValue();
            Map<String, Object> defaultMap = ((Map<String, Object>) resourcesMap.get(Constant.DEFAULT));
            if (defaultMap != null && !defaultMap.isEmpty()) {
                Map<String, Object> contentMap = (Map<String, Object>) defaultMap.get(Constant.CONTENT);
                if (contentMap.isEmpty()) {
                    Map<String, Object> brandNewResponsesMap = new HashMap<>();
                    for (Map.Entry<String, Object> responsesEntry : ((Map<String, Object>) entry.getValue()).entrySet())
                        if (!responsesEntry.getKey().equals(Constant.DEFAULT))
                            brandNewResponsesMap.put(responsesEntry.getKey(), responsesEntry.getValue());
                    entry.setValue(brandNewResponsesMap);
                    return;
                }
            }
        }
        if (Constant.CONTENT.equals(entry.getKey())) {
            Map<String, Object> contentEntryValue = (Map<String, Object>) entry.getValue();

            for (Map.Entry<String, Object> objectEntry : contentEntryValue.entrySet())
                if (Constant.PATTERN.equals(objectEntry.getKey())) {
                    Map<String, Object> tempMap = new HashMap<>();
                    tempMap.put(Constant.CONTENT_TYPE, objectEntry.getValue());
                    entry.setValue(tempMap);
                }
        }
    }

    static List<String> getFileNames() {
        List<String> fileNames = new ArrayList<>();
        File folder = new File(RESOURCES_FOLDER);
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
        Map<String, Object> pathsObj = (Map<String, Object>) obj.get(Constant.PATHS_KEY);
        if (pathsObj == null) return;
        Map<String, Object> resultPathsObj = getIfNullCreate(resultObj, Constant.PATHS_KEY);
        for (Map.Entry<String, Object> entry : pathsObj.entrySet()) {
            if (!resultPathsObj.containsKey(entry.getKey())) {
                replaceAndLog((Map<String, Object>) entry.getValue());
                resultPathsObj.put(entry.getKey(), entry.getValue());
            }
        }
    }

    @SuppressWarnings("unchecked")
    static void compareSchemasObj(Map<String, Object> obj, Map<String, Object> resultObj) {
        Map<String, Object> componentsObj = (Map<String, Object>) obj.get(Constant.COMPONENTS_KEY);
        if (componentsObj == null) return;
        Map<String, Object> schemasObj = (Map<String, Object>) componentsObj.get(Constant.SCHEMAS_KEY);
        if (schemasObj == null) return;
        Map<String, Object> resultComponentsObj = getIfNullCreate(resultObj, Constant.COMPONENTS_KEY);
        Map<String, Object> resultSchemasObj = getIfNullCreate(resultComponentsObj, Constant.SCHEMAS_KEY);
        for (Map.Entry<String, Object> entry : schemasObj.entrySet()) {
            if (!resultSchemasObj.containsKey(entry.getKey())) {
                replaceAndLog((Map<String, Object>) entry.getValue());
                resultSchemasObj.put(entry.getKey(), entry.getValue());
            }
        }
    }

    static void generateAndPutInfoObj(Map<String, Object> resultObj) {
        resultObj.put(Constant.Key.INFO,
                Map.of(Constant.Key.CONTACT,
                        Map.of(Constant.Key.EMAIL, Constant.Info.CONTACT_EMAIL,
                                Constant.Key.NAME, Constant.Info.CONTACT_NAME,
                                Constant.Key.URL, Constant.Info.CONTACT_URL),
                        Constant.Key.DESCRIPTION, Constant.Info.DESCRIPTION,
                        Constant.Key.LICENSE,
                        Map.of(Constant.Key.NAME, Constant.Info.LICENSE_NAME,
                                Constant.Key.URL, Constant.Info.LICENSE_URL),
                        Constant.Key.TERMS_OF_SERVICE, Constant.Info.TERMS_OF_SERVICE,
                        Constant.Key.TITLE, Constant.Info.TITLE,
                        Constant.Key.VERSION, Constant.Info.VERSION));
    }

    @SuppressWarnings("unchecked")
    static Map<String, Object> getIfNullCreate(Map<String, Object> source, String key) {
        Map<String, Object> obj = (Map<String, Object>) source.get(key);
        if (obj == null) {
            obj = new HashMap<>();
            source.put(key, obj);
            return obj;
        }
        return obj;
    }

    static Map<String, Object> load(Yaml yaml, String fileName) {
        InputStream inputStream = Main.class
                .getClassLoader()
                .getResourceAsStream(fileName);
        return yaml.load(inputStream);
    }
}