package tests;

import iputils.IPAddress;
import iputils.Subnet;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class Tests {
    private static void testSet(Set<IPAddress> sIP) {
        sIP.add(new IPAddress("10.0.0.1"));
        sIP.add(new IPAddress("10.0.0.2"));
        sIP.add(new IPAddress("10.0.0.1"));
        System.out.println("sIP.size() = " + sIP.size()); // IntelliJ: soutv
        for (IPAddress ip : sIP) {
            System.out.println("ip = " + ip);
        }
    }

    public static void main(String[] args) {
        testSet(new HashSet<>());
        testSet(new TreeSet<>());
        perfTest(new Subnet("192.168.0.0/24"), new HashSet<>());
        toExcel();
    }

    private static Map<Integer, Long> perfTest(Subnet s, Set<IPAddress> sIP) {
        long start = System.currentTimeMillis();
        int cidr = Integer.toBinaryString(s.getMask().getIP()).replaceAll("0", "").length();
        for (int i = 0; i < Math.pow(2, 32 - cidr); i++) {
            sIP.add(new IPAddress(s.getNet().getIP() + i));
        }
        for (int i = 0; i < Math.pow(2, 32 - cidr) * 2; i++) {
            sIP.contains(new IPAddress(s.getNet().getIP() + i));
        }
        HashMap<Integer, Long> h = new HashMap<>();
        h.put(cidr, System.currentTimeMillis() - start);
        return h;
    }

    public static void toExcel() {
        HashMap<Integer, Long> all = new HashMap<>();
        for (int i = 0; i < 32; i++) {
            Subnet s = new Subnet("0.0.0.0/" + i);
            all.putAll(perfTest(s, new HashSet<>()));
        }

        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Chart sheet");

            // TODO: excel dingsen und javaheapspace entficken


            // Create a row and put some cells in it. Rows are 0 based.
            Row row = sheet.createRow(0);
            Cell cell = row.createCell(0);
            cell.setCellValue("SNM");
            cell = row.createCell(1);
            cell.setCellValue("Duration");

            int rownum = 1;
            for (Map.Entry<Integer, Long> entry : all.entrySet()) {
                row = sheet.createRow(rownum++);
                cell = row.createCell(0);
                cell.setCellValue(entry.getKey());
                cell = row.createCell(1);
                cell.setCellValue((double) entry.getValue()/100);
            }

            Drawing<?> drawing = sheet.createDrawingPatriarch();
            try (OutputStream fileOut = new FileOutputStream("workbook.xlsx")) {
                workbook.write(fileOut);
            }

            workbook.close();

        }catch (IOException e){e.printStackTrace();}

    }
}

