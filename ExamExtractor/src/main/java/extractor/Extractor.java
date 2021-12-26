package extractor;

import technology.tabula.*;
import technology.tabula.extractors.BasicExtractionAlgorithm;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;
import technology.tabula.writers.CSVWriter;
import technology.tabula.writers.Writer;
import org.apache.pdfbox.pdmodel.PDDocument;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

public class Extractor {
    private PDDocument pdf;

    public void loadURL(URL url) throws IOException {
        this.pdf = PDDocument.load(url.openStream());
    }

    private List<ExamSession> CSV_to_ExamSessions(String csvString) {
        CSVReader csvReader = new CSVReader(new StringReader(csvString));
        List<ExamSession> examSessions = new ArrayList<>();
        try {
            List<String[]> csvRows = csvReader.readAll();
            for (String[] row : csvRows) {
                if (row[0].isEmpty() || row[4].isEmpty()) {
                    continue;
                } else {
                    examSessions.add(
                            new ExamSession(row[0], row[1], row[2], row[3], row[4], row[5], row[6], row[7],
                                    row[8], row[9], row[10]));
                }
            }
            csvReader.close();
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
        return examSessions;
    }

    private String CSV_to_String(List<Table> tables) throws IOException {
        Writer writer = new CSVWriter();
        StringBuilder csvOutput = new StringBuilder();
        writer.write(csvOutput, tables);
        return csvOutput.toString();
    }

    private List<Table> extract_tables() throws IOException {
        SpreadsheetExtractionAlgorithm spreadsheetExtractor = new SpreadsheetExtractionAlgorithm();
        BasicExtractionAlgorithm basicExtractor = new BasicExtractionAlgorithm();
        ObjectExtractor extractor = new ObjectExtractor(pdf);
        List<Table> tables = new ArrayList<>();
        PageIterator pages = extractor.extract();

        while (pages.hasNext()) {
            Page page = pages.next();
            // hard-code the areas to extract (-a "%19,0,100,100") -> top 19,left 0, width
            // 100, height 81
            // For reference, see Line 173 of github.com/tabulapdf/tabula-java/
            Rectangle tablePosition = new Rectangle((float) (19.0 / 100 * page.getHeight()), (float) 0,
                    (float) (page.getWidth()), (float) (81.0 / 100 * page.getHeight()));
            Page tableArea = page.getArea(tablePosition);
            if (spreadsheetExtractor.isTabular(page)) {
                tables.addAll(spreadsheetExtractor.extract(tableArea));
            } else {
                tables.addAll(basicExtractor.extract(tableArea));
            }

        }
        extractor.close();
        return tables;
    }

    public List<ExamSession> extract_csv_to_ExamSessions() throws IOException {
        List<Table> extractedTables = extract_tables();
        String CSV_string = CSV_to_String(extractedTables);
        return CSV_to_ExamSessions(CSV_string);
    }
}
