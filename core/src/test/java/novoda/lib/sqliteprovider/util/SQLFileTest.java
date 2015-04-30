package novoda.lib.sqliteprovider.util;

import android.test.AndroidTestCase;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

public class SQLFileTest extends AndroidTestCase {

    @Test
    public void testReadMultiLineCommand() throws IOException {
        String firstLine = "CREATE TABLE 'testTable'";
        String secondLine = "_id INTEGER PRIMARY KEY AUTOINCREMENT;";
        String singleLineCommand = firstLine + " " + secondLine;
        String multiLineCommand = firstLine + "\n" + secondLine;
        List<String> statementsFromMultiline = SQLFile.statementsFrom(new StringReader(multiLineCommand));
        assertEquals(singleLineCommand, statementsFromMultiline.get(0));
    }

    @Test
    public void testSkippingTrailingComments() throws IOException {
        String rawLine = "CREATE TABLE 'testTable'";
        String comment = "-- adding some comment that should get stripped off";
        String nextLine = "_id INTEGER PRIMARY KEY AUTOINCREMENT;";
        String lineCommand = rawLine + comment + "\n" + nextLine;
        List<String> statements = SQLFile.statementsFrom(new StringReader(lineCommand));
        assertEquals(rawLine + " " + nextLine, statements.get(0));
    }

    @Test
    public void testMultiLineEqualsSingleLineStatements() throws IOException {
        List<String> oneLiners = readStatements("one_line_statements.sql");
        List<String> multiLiners = readStatements("multi_line_statements.sql");
        assertEquals(oneLiners, multiLiners);
    }

    @Test
    public void testMultiLineEqualsSingleLineCount() throws IOException {
        List<String> oneLiners = readStatements("one_line_statements.sql");
        List<String> multiLiners = readStatements("multi_line_statements.sql");
        assertEquals(oneLiners.size(), multiLiners.size());
    }

    @Test(expected = IOException.class)
    public void testIncompleteLastStatementDetection() throws IOException {
        readStatements("missing_last_semicolon.sql");
    }

    private List<String> readStatements(String fileName) throws IOException {
        return SQLFile.statementsFrom(getSqlFileReader(fileName));
    }

    private InputStreamReader getSqlFileReader(String fileName) {
        return new InputStreamReader(getClass().getResourceAsStream("/assets/sql/" + fileName));
    }
}
