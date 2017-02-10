package at.htl.timemanager;

import org.junit.Test;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * @timeline .
 * 21.12.2015: MET 001  created test class
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
        /*File file = new File("/Users/MET/IdeaProjects/SYP/Testumgebung");
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
        List<String> members = MyFile.getMembers(data);
        for (String member : members) {
            System.out.println(member);
        }
        MyFile.writeToCsvFile(data,
                "/Users/MET/Dropbox/test_" + dtfFile.format(LocalDateTime.now()) + ".csv");
        //MyFile.writeToCsvFile(MyFile.getSpecificData(obsData, "MET", "20.10.2015", "31.10.2015", false),
        //      "/Users/MET/Dropbox/test_MET_" + dtfFile.format(LocalDateTime.now()) + ".csv");
        */

    }


    @Test
    public void testName() throws Exception {

        //System.out.println(LocalDate.parse("21.10.2015", DateTimeFormatter.BASIC_ISO_DATE));

        double hours = 4000 / 60.0;
        System.out.println(String.format("Total time:  %1.2f hr", hours));
        int minutes = 4000;
        System.out.println(String.format("Total time:  %1.2f hr", minutes / 60.0));
    }

    @Test
    public void testName2() throws Exception {
        LocalDate date = LocalDate.parse("2015-09-15", DateTimeFormatter.ISO_DATE);
        Map<String, Integer> months = MyUtils.initLineChartData(date);
        System.out.println(months);
        System.out.println(LocalDate.parse("2016-01-01", DateTimeFormatter.ISO_DATE).format(MyUtils.MONTH_YEAR_FORMAT));
    }

    @Test
    public void testName3() throws Exception {
        System.out.println(new File("/x.csv").getParentFile().getPath());
        System.out.println(new File("/test/x").getParentFile().getPath());
        System.out.println(new File("/test/x.csv").getParentFile().getPath());
    }


}