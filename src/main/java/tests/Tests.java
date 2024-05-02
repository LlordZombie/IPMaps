package tests;

import iputils.IPAddress;
import iputils.Subnet;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xssf.usermodel.*;

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

    private static long perfTest(Subnet s, Set<IPAddress> sIP) {
        long start = System.nanoTime();
        int cidr = Integer.toBinaryString(s.getMask().getIP()).replaceAll("0", "").length();
        for (int i = 0; i < Math.pow(2, 32 - cidr); i++) {
            sIP.add(new IPAddress(s.getNet().getIP() + i));
        }
        for (int i = 0; i < Math.pow(2, 32 - cidr) * 2; i++) {
            sIP.contains(new IPAddress(s.getNet().getIP() + i));
        }

        return System.nanoTime() - start;
    }

    public static void toExcel() {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Chart sheet");

            // Create a row and put some cells in it. Rows are 0 based.
            Row row = sheet.createRow(0);
            Cell cell = row.createCell(0);
            cell.setCellValue("SNM");
            cell = row.createCell(1);
            cell.setCellValue("Duration");

            int rownum = 1;
            for (int i = 8; i < 32; i++) {
                Subnet s = new Subnet("0.0.0.0/" + i);
                long dur = perfTest(s, new HashSet<>());
                row = sheet.createRow(rownum++);
                cell = row.createCell(0);
                cell.setCellValue(i);
                cell = row.createCell(1);
                cell.setCellValue((double) dur/1000);
            }

            XSSFDrawing drawing = (XSSFDrawing) sheet.createDrawingPatriarch();
            XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, 5, 10, 15);

            XSSFChart chart = drawing.createChart(anchor);
            XDDFChartLegend legend =chart.getOrAddLegend();
            legend.setPosition(LegendPosition.BOTTOM);

            XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
            XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
            XDDFLineChartData data = (XDDFLineChartData) chart.createData(ChartTypes.LINE, bottomAxis, leftAxis);
            chart.setTitleText("Duration of contains()");
            chart.setTitleOverlay(false);
            bottomAxis.setTitle("SNM");
            leftAxis.setTitle("Duration");
            XDDFDataSource<String> xs = XDDFDataSourcesFactory.fromStringCellRange((XSSFSheet) sheet, new CellRangeAddress(1, 24, 0, 0));
            data.addSeries(xs, XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) sheet, new CellRangeAddress(1, 24, 1, 1)));
            chart.plot(data);


            try (OutputStream fileOut = new FileOutputStream("workbook.xlsx")) {
                workbook.write(fileOut);
            }

            workbook.close();

        }catch (IOException e){e.printStackTrace();}
    }
}

