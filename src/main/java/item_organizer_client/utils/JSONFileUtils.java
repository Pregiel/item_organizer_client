package item_organizer_client.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JSONFileUtils {
    public class JSONValue {
        private String key;
        private Object value;

        public JSONValue(String key, Object value) {
            this.key = key;
            this.value = value;
        }
    }

    public static JSONArray getJSONArrayFromFile(String path) {
        JSONParser jsonParser = new JSONParser();
        JSONArray fileContent;

        try (FileReader file = new FileReader(path)) {
            fileContent = (JSONArray) jsonParser.parse(file);
        } catch (IOException | ParseException e) {
            fileContent = new JSONArray();
        }
        return fileContent;
    }

    public static void putJSONObjectToFile(String path, JSONObject jsonObject) {
        JSONArray fileContent = getJSONArrayFromFile(path);

        fileContent.add(jsonObject);
        try (FileWriter file = new FileWriter(path)) {
            file.write(fileContent.toString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clearJSONFile(String path) {
        try (FileWriter file = new FileWriter(path)) {
            file.write(new JSONArray().toString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkIfJSONArrayContainsJSONObject(JSONArray jsonArray, JSONObject jsonObject) {
        for (Object o : jsonArray) {
            JSONObject object = (JSONObject) o;
            int matched = 0;

            for (Object objectValue : jsonObject.entrySet()) {
                Map.Entry<String, Object> value = (Map.Entry<String, Object>) objectValue;
                System.out.println(value.getKey() + " " + value.getValue() + "/" + object.get(value.getKey()));
                if (object.get(value.getKey()).equals(value.getValue())) {
                    matched++;
                }
            }

            System.out.println(matched);
            if (matched == jsonObject.entrySet().size()) {
                System.out.println(matched);
                return false;
            }
        }
        return true;
    }
}
