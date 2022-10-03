import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileNameCSV = "data.csv";
        String fileNameXML = "data.xml";

        //Задание 1
        List<Employee> list1 = parseCSV(columnMapping, fileNameCSV);
        String json1 = listToJson(list1);
        writeString(json1, "data.json");

        //Задание 2
        List<Employee> list2 = parseXML(fileNameXML);
        String json2 = listToJson(list2);
        writeString(json2, "data2.json");

        //Задание со *
        String json3 = readString("data.json");
        List<Employee> list3 = jsonToList(json3);
        System.out.println(list3);
    }


    public static List<Employee> parseCSV(String[] columnMap, String fileName) {
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();

            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMap);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();

            List<Employee> emp = csv.parse();
            return emp;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static List<Employee> parseXML(String fileName) {
        List<Employee> list = null;

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(fileName);

            Node root = doc.getDocumentElement();
            list = read(root);

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<Employee> read(Node node) {
        List<Employee> list = new ArrayList<>();
        String[] arr;

        long id;
        String firstName;
        String lastName;
        String country;
        int age;

        NodeList nodeList = node.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node_ = nodeList.item(i);
            if (Node.ELEMENT_NODE == node_.getNodeType() && node_.getNodeName().equals("employee")) {
                Element element = (Element) node_;

                arr = element.getTextContent().trim().replaceAll("\\s+", ",").split(",");

                id = Long.parseLong(arr[0]);
                firstName = arr[1];
                lastName = arr[2];
                country = arr[3];
                age = Integer.parseInt(arr[4]);

                Employee emp = new Employee(id, firstName, lastName, country, age);

                list.add(emp);
                read(node_);
            }
        }
        return list;
    }


    public static String listToJson(List<Employee> list) {

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();

        String json = gson.toJson(list, listType);

        return json;
    }

    public static boolean writeString(String json, String fileName) {
        boolean flag = false;
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(json);
            file.flush();
            flag = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }

    public static String readString(String fileName) {
        StringBuilder sb = new StringBuilder("");
        String line;

        try (BufferedReader bf = new BufferedReader(new FileReader(fileName))) {
            while ((line = bf.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        line = sb.toString();
        return line;
    }

    public static List<Employee> jsonToList(String jsonText) {
        List<Employee> list = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();

        try {
            JSONArray jsonArray = (JSONArray) jsonParser.parse(jsonText);

            GsonBuilder gb = new GsonBuilder();
            Gson gson = gb.create();

            for (Object el : jsonArray) {
                Employee employee = gson.fromJson(String.valueOf(el), Employee.class);
                list.add(employee);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return list;
    }
}
