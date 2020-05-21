package com.company;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainSceneController {
    @FXML
    private Button fromJPGtoPDF;

    @FXML
    private Button fromPDFtoJPG;

    @FXML
    private void JPGtoPDF(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("jpg", "*.jpg"));

        List<File> files = fileChooser.showOpenMultipleDialog(fromJPGtoPDF.getScene().getWindow());
        String address = files.get(0).getAbsolutePath();

        try {
            generatePDFFromImages(files, address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generatePDFFromImages(List<File> list, String path) throws IOException{
        PDDocument document = new PDDocument();

        for (File f : list) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDImageXObject pdImage = PDImageXObject.createFromFileByContent(f, document);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            float imageHeight = pdImage.getHeight();
            float imageWidth = pdImage.getWidth();
            float maxH = 842;
            float maxW = 595;
            float bestRatio = Math.min(maxW / imageWidth, maxH / imageHeight);
            float newWidth = imageWidth * bestRatio;
            float newHeight = imageHeight * bestRatio;
            float top = (maxH - newHeight) / 2;
            float left = (maxW - newWidth) / 2;

            if (imageHeight>maxH || imageWidth>maxW) {
                contentStream.drawImage(pdImage, left, top, newWidth, newHeight);
            } else
                contentStream.drawImage(pdImage, (maxW - imageWidth) / 2, (maxH - imageHeight) / 2, imageWidth, imageHeight);

            contentStream.close();
        }

        path = path.substring(0, path.lastIndexOf('\\'));
        path += "\\newPDF.pdf";

        document.save(path);
        document.close();
    }

    @FXML
    private void PDFtoJPG() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("pdf", "*.pdf"));

        File file = fileChooser.showOpenDialog(fromPDFtoJPG.getScene().getWindow());
        try {
            generateImageFromPDF(file);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void generateImageFromPDF(File file) throws IOException {
        String path = file.getAbsolutePath();
        path = path.substring(0, path.lastIndexOf('\\'));

        PDDocument document = PDDocument.load(file);
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        for (int page = 0; page < document.getNumberOfPages(); ++page) {
            BufferedImage bim = pdfRenderer.renderImageWithDPI(
                    page, 300, ImageType.RGB);
            ImageIOUtil.writeImage(
                    bim, String.format("%s\\pdf-%d.%s", path, page + 1, "jpg"), 300);
        }
        document.close();
    }

}
