package at.htl.filereader;

import org.junit.Test;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by MET on 19.12.15.
 */
public class MyFileTest {

    @Test
    public void t01Suffix() throws Exception {
        /*String regex = "[Datei] * [Datum]: [Zeit] [Name] [Beschreibung]";
        String str = "FileUtils.java * 14.11.2015: MET 010  corrected deleting files or directories";
        String[] header = regex.split("\\[|\\]");
        ArrayList<String> line = new ArrayList<String>();
        for (int i = 0; i < header.length - 2; i += 2) {
            if (str.startsWith(header[i])) {
                str = str.trim().substring(header[i].trim().length()).trim();
                String text = "";
                while (!str.startsWith(header[i + 2])) {
                    text += str.charAt(0);
                    str = str.substring(1);
                }
                line.add(text.trim());
            }
        }
        line.add(str.trim());
        for (String s : line) {
            System.out.println(s);
        }*/


    }

    @Test
    public void testSearchFile() throws Exception {
        File file = new File("/Users/MET/IdeaProjects/SYP/v1.6.6");
        List<String> extensions = new LinkedList<String>();
        extensions.add("java");
        System.out.println("started filtering files ...");
        List<File> files = MyFile.getFilteredFiles(file, extensions);
        System.out.println(files.size() + " files filtered");

        List<String[]> data = MyFile.getData(files, null);
        for (String[] fields : data) {
            for (String s : fields) {
                System.out.print(s + "; ");
            }
            System.out.print("\n");
        }

        DateTimeFormatter dtfFile = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        MyFile.writeToCsvFile(data, "/Users/MET/Dropbox/test_" +
                dtfFile.format(LocalDateTime.now()) + ".csv");

        boolean reverse = false;
        String p1 = "20.10.2015";
        String p2 = "31.10.2015";
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");

        List<String[]> met = data.stream()
                .filter(p -> p[2].equals("MET"))
                .filter(p -> {
                    try {
                        return df.parse(p[1]).compareTo(df.parse(p1)) != -1
                                && df.parse(p[1]).compareTo(df.parse(p2)) != 1;
                    } catch (ParseException e) {
                        return false;
                    }
                })
                .sorted((e1, e2) -> reverse ? e2[1].compareTo(e1[1]) : e1[1].compareTo(e2[1]))
                .collect(Collectors.toList());

        MyFile.writeToCsvFile(met, "/Users/MET/Dropbox/test_MET_" +
                dtfFile.format(LocalDateTime.now()) + ".csv");

    }

}