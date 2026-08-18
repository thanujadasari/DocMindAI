package com.docmind.service;

import com.docmind.model.PdfDocument;
import com.docmind.utils.PdfReaderUtil;

public class PdfService {

    public static PdfDocument loadPdf(String filePath, String fileName) {

        String text = PdfReaderUtil.readPdf(filePath);

        return new PdfDocument(fileName, text);
    }
}