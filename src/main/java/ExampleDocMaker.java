import org.docx4j.jaxb.Context;
import org.docx4j.model.table.TblFactory;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.*;

import java.io.File;
import java.util.List;

public class ExampleDocMaker {
    public static void produceDoc() throws Docx4JException {
        WordprocessingMLPackage wordPackage = WordprocessingMLPackage.createPackage();
        MainDocumentPart mainDocumentPart = wordPackage.getMainDocumentPart();
        mainDocumentPart.addStyledParagraphOfText("Title", "Hello World!");
        mainDocumentPart.addParagraphOfText("Welcome");

        ObjectFactory factory = Context.getWmlObjectFactory();
        P p = factory.createP();
        R r = factory.createR();
        Text t = factory.createText();
        t.setValue("Welcome To Docx Maker");
        r.getContent().add(t);
        p.getContent().add(r);
        RPr rpr = factory.createRPr();
        BooleanDefaultTrue b = new BooleanDefaultTrue();
        rpr.setB(b);
        rpr.setI(b);
        rpr.setCaps(b);
        Color green = factory.createColor();
        green.setVal("green");
        rpr.setColor(green);
        r.setRPr(rpr);
        mainDocumentPart.getContent().add(p);

        int writableWidthTwips = wordPackage.getDocumentModel()
                .getSections().get(0).getPageDimensions().getWritableWidthTwips();
        int columnNumber = 3;
        Tbl tbl = TblFactory.createTable(3, 3, writableWidthTwips/columnNumber);
        List<Object> rows = tbl.getContent();
        for (Object row : rows) {
            Tr tr = (Tr) row;
            List<Object> cells = tr.getContent();
            for(Object cell : cells) {
                Tc td = (Tc) cell;
                td.getContent().add(p);
            }
        }
        mainDocumentPart.getContent().add(tbl);

        File exportFile = new File("welcome.docx");
        wordPackage.save(exportFile);
    }

    public static P produceColoredText(String text, String color) {
        ObjectFactory factory = Context.getWmlObjectFactory();
        P p = factory.createP();
        R r = factory.createR();
        Text t = factory.createText();
        t.setValue(text);
        r.getContent().add(t);
        p.getContent().add(r);
        RPr rpr = factory.createRPr();
        BooleanDefaultTrue b = new BooleanDefaultTrue();
        rpr.setB(b); //bold
        //rpr.setI(b); //italic
        //rpr.setCaps(b); //caps lock
        Color myColor = factory.createColor();
        myColor.setVal(color);
        rpr.setColor(myColor);
        r.setRPr(rpr);
        return p;
    }

    public static void produceDoc2() throws Docx4JException {
        WordprocessingMLPackage wordPackage = WordprocessingMLPackage.createPackage();
        MainDocumentPart mainDocumentPart = wordPackage.getMainDocumentPart();
        mainDocumentPart.addStyledParagraphOfText("Title", "Nessus!");
        mainDocumentPart.addParagraphOfText("Klasyfikacja luk");

        P p = produceColoredText("Zajebisty dokument", "red");
        mainDocumentPart.getContent().add(p);

        p = produceColoredText("Host: 192.168.22.1", "black");
        mainDocumentPart.getContent().add(p);

        p = produceColoredText("Otwarte porty: 443, 4125, 8080", "black");
        mainDocumentPart.getContent().add(p);

        p = produceColoredText("Wykryte Luki: ", "black");
        mainDocumentPart.getContent().add(p);

        int writableWidthTwips = wordPackage.getDocumentModel()
                .getSections().get(0).getPageDimensions().getWritableWidthTwips();
        int columnNumber = 3;
        Tbl tbl = TblFactory.createTable(8, 2, writableWidthTwips/columnNumber);
        List<Object> rows = tbl.getContent();
        for (Object row : rows) {
            Tr tr = (Tr) row;
            List<Object> cells = tr.getContent();
            ((Tc)cells.get(0)).getContent().add(produceColoredText(String.valueOf(rows.indexOf(row)+1), "black"));
            ((Tc)cells.get(1)).getContent().add(produceColoredText("Luka nr. " + String.valueOf(rows.indexOf(row)+1), "black"));
        }
        mainDocumentPart.getContent().add(tbl);

        File exportFile = new File("output.docx");
        wordPackage.save(exportFile);
    }
}
