import org.docx4j.openpackaging.exceptions.Docx4JException;

import java.io.IOException;

public class Main {

    public static void main (String[] args) throws Exception {
        System.out.println("Hello World!");

        new SiteParser("input.html");

        try {
            DocMaker.produceDoc();
        } catch (Docx4JException e) {
            e.printStackTrace();
        }
    }
}
