import org.docx4j.openpackaging.exceptions.Docx4JException;

import java.io.IOException;

public class Main {

    public static void main (String[] args) throws Exception {
        System.out.println("Hello World!");

        if (args.length < 3) {
            System.out.println("Example parameters: input.html input.xml output.docx");
            return;
        }


        new SiteParser(args[0]);
        new XmlParser(args[1]);

        try {
            DocMaker.produceDoc(args[2]);
        } catch (Docx4JException e) {
            e.printStackTrace();
        }

    }
}
