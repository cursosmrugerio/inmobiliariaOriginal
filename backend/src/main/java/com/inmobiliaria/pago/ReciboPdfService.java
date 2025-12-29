package com.inmobiliaria.pago;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

/**
 * Servicio para generar recibos de pago en formato PDF.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReciboPdfService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
    private static final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
    private static final Font LABEL_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.DARK_GRAY);
    private static final Font VALUE_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
    private static final Font TOTAL_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLACK);

    /**
     * Genera un recibo de pago en formato PDF.
     */
    public byte[] generarReciboPdf(Pago pago) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.LETTER);
            PdfWriter.getInstance(document, out);

            document.open();

            // Título
            Paragraph titulo = new Paragraph("RECIBO DE PAGO", TITLE_FONT);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            document.add(titulo);

            // Número de recibo
            Paragraph numeroRecibo = new Paragraph("No. " + pago.getNumeroRecibo(), TOTAL_FONT);
            numeroRecibo.setAlignment(Element.ALIGN_CENTER);
            numeroRecibo.setSpacingAfter(30);
            document.add(numeroRecibo);

            // Información del recibo
            document.add(crearTablaInfoRecibo(pago));
            document.add(new Paragraph(" "));

            // Información del cliente
            document.add(crearSeccionCliente(pago));
            document.add(new Paragraph(" "));

            // Información del contrato/propiedad
            document.add(crearSeccionContrato(pago));
            document.add(new Paragraph(" "));

            // Detalle del pago
            document.add(crearTablaDetallePago(pago));
            document.add(new Paragraph(" "));

            // Total
            document.add(crearSeccionTotal(pago));
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            // Pie de página
            document.add(crearPiePagina());

            document.close();

            return out.toByteArray();
        } catch (Exception e) {
            log.error("Error generando recibo PDF: {}", e.getMessage());
            throw new RuntimeException("Error generando recibo PDF", e);
        }
    }

    private PdfPTable crearTablaInfoRecibo(Pago pago) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 1});

        addCell(table, "Fecha de Pago:", LABEL_FONT, Element.ALIGN_LEFT, false);
        addCell(table, pago.getFechaPago().format(DATE_FORMATTER), VALUE_FONT, Element.ALIGN_LEFT, false);

        addCell(table, "Fecha de Registro:", LABEL_FONT, Element.ALIGN_LEFT, false);
        addCell(table, pago.getCreatedAt() != null ? pago.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "", VALUE_FONT, Element.ALIGN_LEFT, false);

        addCell(table, "Estado:", LABEL_FONT, Element.ALIGN_LEFT, false);
        addCell(table, pago.getEstado() != null ? pago.getEstado().name() : "", VALUE_FONT, Element.ALIGN_LEFT, false);

        return table;
    }

    private PdfPTable crearSeccionCliente(Pago pago) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);

        // Header
        PdfPCell headerCell = new PdfPCell(new Phrase("DATOS DEL CLIENTE", HEADER_FONT));
        headerCell.setBackgroundColor(new Color(51, 51, 51));
        headerCell.setPadding(8);
        table.addCell(headerCell);

        // Contenido
        PdfPTable contentTable = new PdfPTable(2);
        contentTable.setWidthPercentage(100);
        contentTable.setWidths(new float[]{1, 2});

        if (pago.getPersona() != null) {
            addCell(contentTable, "Nombre:", LABEL_FONT, Element.ALIGN_LEFT, false);
            addCell(contentTable, pago.getPersona().getNombreCompleto(), VALUE_FONT, Element.ALIGN_LEFT, false);

            addCell(contentTable, "RFC:", LABEL_FONT, Element.ALIGN_LEFT, false);
            addCell(contentTable, pago.getPersona().getRfc() != null ? pago.getPersona().getRfc() : "-", VALUE_FONT, Element.ALIGN_LEFT, false);

            addCell(contentTable, "Email:", LABEL_FONT, Element.ALIGN_LEFT, false);
            addCell(contentTable, pago.getPersona().getEmail() != null ? pago.getPersona().getEmail() : "-", VALUE_FONT, Element.ALIGN_LEFT, false);

            addCell(contentTable, "Teléfono:", LABEL_FONT, Element.ALIGN_LEFT, false);
            addCell(contentTable, pago.getPersona().getTelefono() != null ? pago.getPersona().getTelefono() : "-", VALUE_FONT, Element.ALIGN_LEFT, false);
        }

        PdfPCell contentCell = new PdfPCell(contentTable);
        contentCell.setPadding(10);
        table.addCell(contentCell);

        return table;
    }

    private PdfPTable crearSeccionContrato(Pago pago) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);

        // Header
        PdfPCell headerCell = new PdfPCell(new Phrase("DATOS DEL CONTRATO", HEADER_FONT));
        headerCell.setBackgroundColor(new Color(51, 51, 51));
        headerCell.setPadding(8);
        table.addCell(headerCell);

        // Contenido
        PdfPTable contentTable = new PdfPTable(2);
        contentTable.setWidthPercentage(100);
        contentTable.setWidths(new float[]{1, 2});

        if (pago.getContrato() != null) {
            addCell(contentTable, "No. Contrato:", LABEL_FONT, Element.ALIGN_LEFT, false);
            addCell(contentTable, pago.getContrato().getNumeroContrato(), VALUE_FONT, Element.ALIGN_LEFT, false);

            if (pago.getContrato().getPropiedad() != null) {
                addCell(contentTable, "Propiedad:", LABEL_FONT, Element.ALIGN_LEFT, false);
                addCell(contentTable, pago.getContrato().getPropiedad().getDireccionCompleta(), VALUE_FONT, Element.ALIGN_LEFT, false);
            }

            addCell(contentTable, "Vigencia:", LABEL_FONT, Element.ALIGN_LEFT, false);
            String vigencia = pago.getContrato().getFechaInicio().format(DATE_FORMATTER) +
                    " - " + pago.getContrato().getFechaFin().format(DATE_FORMATTER);
            addCell(contentTable, vigencia, VALUE_FONT, Element.ALIGN_LEFT, false);
        }

        PdfPCell contentCell = new PdfPCell(contentTable);
        contentCell.setPadding(10);
        table.addCell(contentCell);

        return table;
    }

    private PdfPTable crearTablaDetallePago(Pago pago) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);

        // Header
        PdfPCell headerCell = new PdfPCell(new Phrase("DETALLE DEL PAGO", HEADER_FONT));
        headerCell.setBackgroundColor(new Color(51, 51, 51));
        headerCell.setPadding(8);
        table.addCell(headerCell);

        // Contenido
        PdfPTable contentTable = new PdfPTable(2);
        contentTable.setWidthPercentage(100);
        contentTable.setWidths(new float[]{2, 1});

        addCell(contentTable, "Concepto:", LABEL_FONT, Element.ALIGN_LEFT, false);
        addCell(contentTable, "Pago de arrendamiento", VALUE_FONT, Element.ALIGN_RIGHT, false);

        addCell(contentTable, "Tipo de Pago:", LABEL_FONT, Element.ALIGN_LEFT, false);
        addCell(contentTable, pago.getTipoPago() != null ? formatTipoPago(pago.getTipoPago()) : "-", VALUE_FONT, Element.ALIGN_RIGHT, false);

        if (pago.getReferencia() != null && !pago.getReferencia().isEmpty()) {
            addCell(contentTable, "Referencia:", LABEL_FONT, Element.ALIGN_LEFT, false);
            addCell(contentTable, pago.getReferencia(), VALUE_FONT, Element.ALIGN_RIGHT, false);
        }

        if (pago.getBanco() != null && !pago.getBanco().isEmpty()) {
            addCell(contentTable, "Banco:", LABEL_FONT, Element.ALIGN_LEFT, false);
            addCell(contentTable, pago.getBanco(), VALUE_FONT, Element.ALIGN_RIGHT, false);
        }

        PdfPCell contentCell = new PdfPCell(contentTable);
        contentCell.setPadding(10);
        table.addCell(contentCell);

        return table;
    }

    private PdfPTable crearSeccionTotal(Pago pago) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(50);
        table.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.setWidths(new float[]{1, 1});

        // Monto
        PdfPCell labelCell = new PdfPCell(new Phrase("TOTAL PAGADO:", TOTAL_FONT));
        labelCell.setBorder(Rectangle.TOP);
        labelCell.setBorderWidth(2);
        labelCell.setPaddingTop(10);
        labelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(formatMoney(pago.getMonto()), TOTAL_FONT));
        valueCell.setBorder(Rectangle.TOP);
        valueCell.setBorderWidth(2);
        valueCell.setPaddingTop(10);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(valueCell);

        return table;
    }

    private Paragraph crearPiePagina() {
        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.GRAY);
        Paragraph footer = new Paragraph();
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.add(new Chunk("Este recibo es un comprobante de pago.", footerFont));
        footer.add(Chunk.NEWLINE);
        footer.add(new Chunk("Conserve este documento para cualquier aclaración.", footerFont));
        return footer;
    }

    private void addCell(PdfPTable table, String text, Font font, int alignment, boolean border) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(border ? Rectangle.BOX : Rectangle.NO_BORDER);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private String formatMoney(BigDecimal amount) {
        if (amount == null) return "$0.00";
        return String.format("$%,.2f MXN", amount);
    }

    private String formatTipoPago(TipoPago tipoPago) {
        return switch (tipoPago) {
            case EFECTIVO -> "Efectivo";
            case TRANSFERENCIA -> "Transferencia Bancaria";
            case DEPOSITO_BANCARIO -> "Depósito Bancario";
            case CHEQUE -> "Cheque";
            case TARJETA_CREDITO -> "Tarjeta de Crédito";
            case TARJETA_DEBITO -> "Tarjeta de Débito";
        };
    }
}
