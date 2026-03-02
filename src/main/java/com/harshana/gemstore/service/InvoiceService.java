package com.harshana.gemstore.service;

import com.harshana.gemstore.entity.Order;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class InvoiceService {

    public byte[] generateInvoicePdf(Order order) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            document.add(new Paragraph("Ceylon Gems - Invoice", titleFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Order ID: " + order.getId()));
            document.add(new Paragraph("Date: " + order.getCreatedAt()));
            document.add(new Paragraph("Customer: " + order.getCustomerName()));
            document.add(new Paragraph("Phone: " + order.getPhone()));
            document.add(new Paragraph("Address: " + order.getDeliveryAddress()));
            document.add(new Paragraph("Payment: " + order.getPaymentMethod()));
            document.add(new Paragraph("Status: " + order.getOrderStatus()));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{4, 1, 2, 2});

            table.addCell(header("Item"));
            table.addCell(header("Qty"));
            table.addCell(header("Price"));
            table.addCell(header("Total"));

            order.getItems().forEach(it -> {
                table.addCell(cell(it.getGem().getName()));
                table.addCell(cell(String.valueOf(it.getQuantity())));
                table.addCell(cell("Rs " + it.getPrice()));
                table.addCell(cell("Rs " + (it.getPrice() * it.getQuantity())));
            });

            document.add(table);
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Grand Total: Rs " + order.getTotalAmount(), new Font(Font.HELVETICA, 12, Font.BOLD)));

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Invoice PDF generation failed", e);
        }
    }

    private PdfPCell header(String text) {
        PdfPCell c = new PdfPCell(new Phrase(text, new Font(Font.HELVETICA, 12, Font.BOLD)));
        c.setPadding(6);
        return c;
    }

    private PdfPCell cell(String text) {
        PdfPCell c = new PdfPCell(new Phrase(text));
        c.setPadding(6);
        return c;
    }
}