package com.inmobiliaria.reporte.service;

import com.inmobiliaria.reporte.dto.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ========== EXCEL EXPORTS ==========

    public byte[] exportEstadoCuentaExcel(EstadoCuentaDTO estadoCuenta) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Estado de Cuenta");

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle moneyStyle = createMoneyStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);

            int rowNum = 0;

            // Información del cliente
            Row titleRow = sheet.createRow(rowNum++);
            titleRow.createCell(0).setCellValue("ESTADO DE CUENTA");

            rowNum++; // Empty row

            createLabelValueRow(sheet, rowNum++, "Cliente:", estadoCuenta.getNombreCliente());
            createLabelValueRow(sheet, rowNum++, "RFC:", estadoCuenta.getRfc());
            createLabelValueRow(sheet, rowNum++, "Email:", estadoCuenta.getEmail());
            createLabelValueRow(sheet, rowNum++, "Teléfono:", estadoCuenta.getTelefono());
            createLabelValueRow(sheet, rowNum++, "Fecha de generación:", estadoCuenta.getFechaGeneracion().format(DATE_FORMATTER));

            rowNum++; // Empty row

            // Resumen
            createLabelValueRow(sheet, rowNum++, "Total Cargos:", formatMoney(estadoCuenta.getTotalCargos()));
            createLabelValueRow(sheet, rowNum++, "Total Abonos:", formatMoney(estadoCuenta.getTotalAbonos()));
            createLabelValueRow(sheet, rowNum++, "Saldo Actual:", formatMoney(estadoCuenta.getSaldoActual()));
            createLabelValueRow(sheet, rowNum++, "Saldo Vencido:", formatMoney(estadoCuenta.getSaldoVencido()));

            rowNum++; // Empty row

            // Headers de movimientos
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {"Fecha", "Concepto", "Tipo", "Cargo", "Abono", "Saldo", "Días Vencido", "Estado"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Datos de movimientos
            for (EstadoCuentaItemDTO item : estadoCuenta.getMovimientos()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(item.getFecha() != null ? item.getFecha().format(DATE_FORMATTER) : "");
                row.createCell(1).setCellValue(item.getConcepto());
                row.createCell(2).setCellValue(item.getTipo());
                row.createCell(3).setCellValue(item.getCargo() != null ? item.getCargo().doubleValue() : 0);
                row.createCell(4).setCellValue(item.getAbono() != null ? item.getAbono().doubleValue() : 0);
                row.createCell(5).setCellValue(item.getSaldo() != null ? item.getSaldo().doubleValue() : 0);
                row.createCell(6).setCellValue(item.getDiasVencido() != null ? item.getDiasVencido() : 0);
                row.createCell(7).setCellValue(item.getEstado() != null ? item.getEstado() : "");
            }

            // Autosize columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] exportAntiguedadSaldosExcel(AntiguedadSaldosDTO antiguedad) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Antigüedad de Saldos");

            CellStyle headerStyle = createHeaderStyle(workbook);
            int rowNum = 0;

            // Título
            Row titleRow = sheet.createRow(rowNum++);
            titleRow.createCell(0).setCellValue("REPORTE DE ANTIGÜEDAD DE SALDOS");

            rowNum++;

            createLabelValueRow(sheet, rowNum++, "Fecha de Corte:", antiguedad.getFechaCorte().format(DATE_FORMATTER));
            createLabelValueRow(sheet, rowNum++, "Fecha de Generación:", antiguedad.getFechaGeneracion().format(DATE_FORMATTER));
            createLabelValueRow(sheet, rowNum++, "Total Clientes:", String.valueOf(antiguedad.getCantidadClientes()));
            createLabelValueRow(sheet, rowNum++, "Total Documentos:", String.valueOf(antiguedad.getCantidadDocumentos()));

            rowNum++;

            // Totales por antigüedad
            createLabelValueRow(sheet, rowNum++, "Vigente:", formatMoney(antiguedad.getTotalVigente()));
            createLabelValueRow(sheet, rowNum++, "1-30 días:", formatMoney(antiguedad.getTotalVencido1a30()));
            createLabelValueRow(sheet, rowNum++, "31-60 días:", formatMoney(antiguedad.getTotalVencido31a60()));
            createLabelValueRow(sheet, rowNum++, "61-90 días:", formatMoney(antiguedad.getTotalVencido61a90()));
            createLabelValueRow(sheet, rowNum++, "Más de 90 días:", formatMoney(antiguedad.getTotalVencidoMas90()));
            createLabelValueRow(sheet, rowNum++, "Total General:", formatMoney(antiguedad.getTotalGeneral()));

            rowNum++;

            // Headers
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {"Cliente", "Propiedad", "Vigente", "1-30", "31-60", "61-90", "+90", "Total Vencido", "Saldo Total"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Datos
            for (AntiguedadSaldosItemDTO item : antiguedad.getDetalle()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(item.getNombreCliente());
                row.createCell(1).setCellValue(item.getDireccionPropiedad());
                row.createCell(2).setCellValue(item.getVigente().doubleValue());
                row.createCell(3).setCellValue(item.getVencido1a30().doubleValue());
                row.createCell(4).setCellValue(item.getVencido31a60().doubleValue());
                row.createCell(5).setCellValue(item.getVencido61a90().doubleValue());
                row.createCell(6).setCellValue(item.getVencidoMas90().doubleValue());
                row.createCell(7).setCellValue(item.getTotalVencido().doubleValue());
                row.createCell(8).setCellValue(item.getSaldoTotal().doubleValue());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] exportCarteraVencidaExcel(ReporteCarteraVencidaDTO cartera) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Cartera Vencida");

            CellStyle headerStyle = createHeaderStyle(workbook);
            int rowNum = 0;

            // Título
            Row titleRow = sheet.createRow(rowNum++);
            titleRow.createCell(0).setCellValue("REPORTE DE CARTERA VENCIDA");

            rowNum++;

            createLabelValueRow(sheet, rowNum++, "Fecha de Corte:", cartera.getFechaCorte().format(DATE_FORMATTER));
            createLabelValueRow(sheet, rowNum++, "Total Cartera:", formatMoney(cartera.getTotalCartera()));
            createLabelValueRow(sheet, rowNum++, "Total Penalidades:", formatMoney(cartera.getTotalPenalidades()));
            createLabelValueRow(sheet, rowNum++, "Total General:", formatMoney(cartera.getTotalGeneral()));
            createLabelValueRow(sheet, rowNum++, "Cantidad de Cuentas:", String.valueOf(cartera.getCantidadCuentas()));

            rowNum++;

            // Headers
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {"ID", "Cliente", "Propiedad", "Concepto", "Fecha Venc.", "Días Venc.",
                               "Clasificación", "Monto Original", "Pendiente", "Penalidad", "Total", "Estado"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Datos
            for (ReporteCarteraVencidaDTO.CarteraVencidaItemDTO item : cartera.getDetalle()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(item.getId());
                row.createCell(1).setCellValue(item.getNombreCliente());
                row.createCell(2).setCellValue(item.getDireccionPropiedad());
                row.createCell(3).setCellValue(item.getConcepto());
                row.createCell(4).setCellValue(item.getFechaVencimiento() != null ? item.getFechaVencimiento().format(DATE_FORMATTER) : "");
                row.createCell(5).setCellValue(item.getDiasVencido() != null ? item.getDiasVencido() : 0);
                row.createCell(6).setCellValue(item.getClasificacion());
                row.createCell(7).setCellValue(item.getMontoOriginal() != null ? item.getMontoOriginal().doubleValue() : 0);
                row.createCell(8).setCellValue(item.getMontoPendiente() != null ? item.getMontoPendiente().doubleValue() : 0);
                row.createCell(9).setCellValue(item.getMontoPenalidad() != null ? item.getMontoPenalidad().doubleValue() : 0);
                row.createCell(10).setCellValue(item.getMontoTotal() != null ? item.getMontoTotal().doubleValue() : 0);
                row.createCell(11).setCellValue(item.getEstadoCobranza());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] exportProyeccionExcel(ProyeccionCobranzaReporteDTO proyeccion) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Proyección Cobranza");

            CellStyle headerStyle = createHeaderStyle(workbook);
            int rowNum = 0;

            // Título
            Row titleRow = sheet.createRow(rowNum++);
            titleRow.createCell(0).setCellValue("REPORTE DE PROYECCIÓN DE COBRANZA");

            rowNum++;

            createLabelValueRow(sheet, rowNum++, "Periodo:", proyeccion.getPeriodoInicio().format(DATE_FORMATTER) + " - " + proyeccion.getPeriodoFin().format(DATE_FORMATTER));
            createLabelValueRow(sheet, rowNum++, "Total Proyectado:", formatMoney(proyeccion.getTotalProyectado()));
            createLabelValueRow(sheet, rowNum++, "Total Cobrado:", formatMoney(proyeccion.getTotalCobrado()));
            createLabelValueRow(sheet, rowNum++, "% Cumplimiento:", proyeccion.getPorcentajeCumplimiento() + "%");

            rowNum++;

            // Headers
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {"Periodo", "Proyectado", "Cobrado", "Diferencia", "% Cumplimiento", "Contratos", "Pagos Esperados", "Pagos Recibidos"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Datos
            for (ProyeccionCobranzaReporteDTO.ProyeccionMesDTO item : proyeccion.getDetalleMensual()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(item.getMesAnio());
                row.createCell(1).setCellValue(item.getMontoProyectado().doubleValue());
                row.createCell(2).setCellValue(item.getMontoCobrado().doubleValue());
                row.createCell(3).setCellValue(item.getDiferencia().doubleValue());
                row.createCell(4).setCellValue(item.getPorcentajeCumplimiento().doubleValue());
                row.createCell(5).setCellValue(item.getCantidadContratos());
                row.createCell(6).setCellValue(item.getPagosEsperados());
                row.createCell(7).setCellValue(item.getPagosRecibidos());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    // ========== CSV EXPORTS ==========

    public byte[] exportEstadoCuentaCsv(EstadoCuentaDTO estadoCuenta) {
        StringBuilder csv = new StringBuilder();
        csv.append("Fecha,Concepto,Tipo,Cargo,Abono,Saldo,Días Vencido,Estado\n");

        for (EstadoCuentaItemDTO item : estadoCuenta.getMovimientos()) {
            csv.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s\n",
                    item.getFecha() != null ? item.getFecha().format(DATE_FORMATTER) : "",
                    escapeCsv(item.getConcepto()),
                    item.getTipo(),
                    item.getCargo(),
                    item.getAbono(),
                    item.getSaldo(),
                    item.getDiasVencido(),
                    item.getEstado() != null ? item.getEstado() : ""));
        }

        return csv.toString().getBytes();
    }

    public byte[] exportAntiguedadSaldosCsv(AntiguedadSaldosDTO antiguedad) {
        StringBuilder csv = new StringBuilder();
        csv.append("Cliente,Propiedad,Vigente,1-30,31-60,61-90,+90,Total Vencido,Saldo Total\n");

        for (AntiguedadSaldosItemDTO item : antiguedad.getDetalle()) {
            csv.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                    escapeCsv(item.getNombreCliente()),
                    escapeCsv(item.getDireccionPropiedad()),
                    item.getVigente(),
                    item.getVencido1a30(),
                    item.getVencido31a60(),
                    item.getVencido61a90(),
                    item.getVencidoMas90(),
                    item.getTotalVencido(),
                    item.getSaldoTotal()));
        }

        return csv.toString().getBytes();
    }

    public byte[] exportCarteraVencidaCsv(ReporteCarteraVencidaDTO cartera) {
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Cliente,Propiedad,Concepto,Fecha Venc.,Días Venc.,Clasificación,Monto Original,Pendiente,Penalidad,Total,Estado\n");

        for (ReporteCarteraVencidaDTO.CarteraVencidaItemDTO item : cartera.getDetalle()) {
            csv.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                    item.getId(),
                    escapeCsv(item.getNombreCliente()),
                    escapeCsv(item.getDireccionPropiedad()),
                    escapeCsv(item.getConcepto()),
                    item.getFechaVencimiento() != null ? item.getFechaVencimiento().format(DATE_FORMATTER) : "",
                    item.getDiasVencido(),
                    item.getClasificacion(),
                    item.getMontoOriginal(),
                    item.getMontoPendiente(),
                    item.getMontoPenalidad() != null ? item.getMontoPenalidad() : BigDecimal.ZERO,
                    item.getMontoTotal(),
                    item.getEstadoCobranza()));
        }

        return csv.toString().getBytes();
    }

    public byte[] exportProyeccionCsv(ProyeccionCobranzaReporteDTO proyeccion) {
        StringBuilder csv = new StringBuilder();
        csv.append("Periodo,Proyectado,Cobrado,Diferencia,% Cumplimiento,Contratos,Pagos Esperados,Pagos Recibidos\n");

        for (ProyeccionCobranzaReporteDTO.ProyeccionMesDTO item : proyeccion.getDetalleMensual()) {
            csv.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s\n",
                    item.getMesAnio(),
                    item.getMontoProyectado(),
                    item.getMontoCobrado(),
                    item.getDiferencia(),
                    item.getPorcentajeCumplimiento(),
                    item.getCantidadContratos(),
                    item.getPagosEsperados(),
                    item.getPagosRecibidos()));
        }

        return csv.toString().getBytes();
    }

    // ========== HELPER METHODS ==========

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createMoneyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("$#,##0.00"));
        return style;
    }

    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("dd/mm/yyyy"));
        return style;
    }

    private void createLabelValueRow(Sheet sheet, int rowNum, String label, String value) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(label);
        row.createCell(1).setCellValue(value != null ? value : "");
    }

    private String formatMoney(BigDecimal amount) {
        if (amount == null) return "$0.00";
        return String.format("$%,.2f", amount);
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
