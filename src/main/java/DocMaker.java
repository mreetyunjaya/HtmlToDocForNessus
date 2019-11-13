import org.docx4j.jaxb.Context;
import org.docx4j.model.table.TblFactory;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.*;

import java.io.File;
import java.util.List;

public class DocMaker {

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

    public static P produceHighLightedText(String text, String color) {
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
        Highlight myColor = factory.createHighlight();
        myColor.setVal(color);
        rpr.setHighlight(myColor);
        r.setRPr(rpr);
        return p;
    }

    public static Text produceText(String text) {
        ObjectFactory factory = Context.getWmlObjectFactory();
        Text t = factory.createText();
        t.setValue(text);
        return t;
    }

    public static TcPr generateShade(String color) {
        ObjectFactory factory = Context.getWmlObjectFactory();
        CTShd shd = factory.createCTShd();
        shd.setVal(org.docx4j.wml.STShd.CLEAR);
        //shd.setColor(color);
        shd.setColor( "auto");
        shd.setFill(color);
        //shd.setThemeFill(org.docx4j.wml.STThemeColor.ACCENT_1);
        //shd.setThemeFillTint( "66");

        TcPr tcPr = new TcPr();
        tcPr.setShd(shd);
        return tcPr;
    }

    public static void produceDoc() throws Docx4JException {
        WordprocessingMLPackage wordPackage = WordprocessingMLPackage.createPackage();
        MainDocumentPart mainDocumentPart = wordPackage.getMainDocumentPart();
        mainDocumentPart.addStyledParagraphOfText("Title", "Nessus!");
        mainDocumentPart.addParagraphOfText("Klasyfikacja luk");
        P emptyText = produceColoredText("", "black");

        for (IpInfoHolder ipInfoHolder : AppMemory.getInstance().getIpInfoList()) {
            P p = produceColoredText("Host: " + ipInfoHolder.getIpAddress(), "black");
            mainDocumentPart.getContent().add(p);

            String openPorts = ipInfoHolder.getPorts();
            if (openPorts == null || openPorts.trim().equals("")) {
                p = produceColoredText("Nie wykryto otwartych portow", "black");
                mainDocumentPart.getContent().add(p);
            }
            else {
                p = produceColoredText("Otwarte porty: " + openPorts, "black");
                mainDocumentPart.getContent().add(p);
            }

            p = produceColoredText("Wykryte Luki: ", "black");
            mainDocumentPart.getContent().add(p);

            if (ipInfoHolder.getFoundExploits() == null || ipInfoHolder.getFoundExploits().size() == 0) {
                p = produceColoredText("Nie wykryto zadnych luk", "black");
                mainDocumentPart.getContent().add(p);

                mainDocumentPart.getContent().add(emptyText);
                mainDocumentPart.getContent().add(emptyText);
                continue;
            }

            int writableWidthTwips = wordPackage.getDocumentModel()
                    .getSections().get(0).getPageDimensions().getWritableWidthTwips();
            int columnNumber = 2;
            Tbl tbl = TblFactory.createTable(ipInfoHolder.getFoundExploits().size(), 2, writableWidthTwips/columnNumber);

            boolean titleRowCreated = false;
            int counter = 0;
            List<Object> rows = tbl.getContent();
            for (Object row : rows) {
                ExploitHolder exploitHolder = ipInfoHolder.getFoundExploits().get(counter);
                /*if(!titleRowCreated) {
                    Tr tr = (Tr) row;
                    List<Object> cells = tr.getContent();
                    ((Tc)cells.get(0)).getContent().add(produceColoredText(String.valueOf(rows.indexOf(row)), "black");
                    ((Tc)cells.get(1)).getContent().add(produceColoredText(exploitHolder.getName(), "black"));
                    titleRowCreated=true;
                    continue;
                }*/
                Tr tr = (Tr) row;
                List<Object> cells = tr.getContent();
                ((Tc)cells.get(0)).getContent().clear();
                ((Tc)cells.get(0)).getContent().add(produceColoredText(String.valueOf(counter+1), "black"));
                ((Tc)cells.get(0)).setTcPr(generateShade(ExploitTypeColor.typeStringToColor(exploitHolder.getType()).typeColor));
                ((Tc)cells.get(1)).getContent().clear();
                ((Tc)cells.get(1)).getContent().add(produceColoredText(exploitHolder.getName(), "black"));
                //((Tc)cells.get(0)).getContent().add(produceHighLightedText(String.valueOf(counter+1), ExploitTypeColor.typeStringToColor(exploitHolder.getType()).typeColor));
                //((Tc)cells.get(1)).getContent().add(produceColoredText(exploitHolder.getName(), ExploitTypeColor.typeStringToColor(exploitHolder.getType()).typeColor));
                counter++;
            }
            mainDocumentPart.getContent().add(tbl);
            mainDocumentPart.getContent().add(emptyText);
            mainDocumentPart.getContent().add(emptyText);
        }



        File exportFile = new File("output.docx");
        wordPackage.save(exportFile);
    }
}
