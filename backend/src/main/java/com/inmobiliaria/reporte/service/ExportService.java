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

    // ========== FINIQUITO EXPORTS ==========

    public byte[] exportFiniquitoExcel(FiniquitoDTO finiquito) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Finiquito");

            CellStyle headerStyle = createHeaderStyle(workbook);
            int rowNum = 0;

            // Título
            Row titleRow = sheet.createRow(rowNum++);
            titleRow.createCell(0).setCellValue("FINIQUITO DE CONTRATO");

            rowNum++;

            // Datos del contrato
            createLabelValueRow(sheet, rowNum++, "No. Contrato:", finiquito.getNumeroContrato());
            createLabelValueRow(sheet, rowNum++, "Arrendatario:", finiquito.getNombreArrendatario());
            createLabelValueRow(sheet, rowNum++, "RFC:", finiquito.getRfcArrendatario());
            createLabelValueRow(sheet, rowNum++, "Email:", finiquito.getEmailArrendatario());
            createLabelValueRow(sheet, rowNum++, "Propiedad:", finiquito.getDireccionPropiedad());
            createLabelValueRow(sheet, rowNum++, "Fecha Inicio:", finiquito.getFechaInicioContrato() != null ? finiquito.getFechaInicioContrato().format(DATE_FORMATTER) : "");
            createLabelValueRow(sheet, rowNum++, "Fecha Fin:", finiquito.getFechaFinContrato() != null ? finiquito.getFechaFinContrato().format(DATE_FORMATTER) : "");
            createLabelValueRow(sheet, rowNum++, "Fecha Terminación:", finiquito.getFechaTerminacion() != null ? finiquito.getFechaTerminacion().format(DATE_FORMATTER) : "");

            rowNum++;

            // Resumen financiero
            createLabelValueRow(sheet, rowNum++, "Total Rentas Pagadas:", formatMoney(finiquito.getTotalRentasPagadas()));
            createLabelValueRow(sheet, rowNum++, "Total Rentas Pendientes:", formatMoney(finiquito.getTotalRentasPendientes()));
            createLabelValueRow(sheet, rowNum++, "Saldo Pendiente:", formatMoney(finiquito.getSaldoPendiente()));
            createLabelValueRow(sheet, rowNum++, "Depósito:", formatMoney(finiquito.getMontoDeposito()));
            createLabelValueRow(sheet, rowNum++, "Deducciones:", formatMoney(finiquito.getDeduccionesDeposito()));
            createLabelValueRow(sheet, rowNum++, "Depósito a Devolver:", formatMoney(finiquito.getDepositoADevolver()));
            createLabelValueRow(sheet, rowNum++, "Monto Liquidación:", formatMoney(finiquito.getMontoLiquidacion()));

            rowNum++;

            // Detalle de conceptos
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {"Fecha", "Concepto", "Tipo", "Monto", "Estado", "Notas"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            for (FiniquitoDTO.ConceptoFiniquitoDTO concepto : finiquito.getConceptos()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(concepto.getFecha() != null ? concepto.getFecha().format(DATE_FORMATTER) : "");
                row.createCell(1).setCellValue(concepto.getConcepto());
                row.createCell(2).setCellValue(concepto.getTipo());
                row.createCell(3).setCellValue(concepto.getMonto() != null ? concepto.getMonto().doubleValue() : 0);
                row.createCell(4).setCellValue(concepto.getEstado());
                row.createCell(5).setCellValue(concepto.getNotas() != null ? concepto.getNotas() : "");
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] exportFiniquitoCsv(FiniquitoDTO finiquito) {
        StringBuilder csv = new StringBuilder();
        csv.append("Fecha,Concepto,Tipo,Monto,Estado,Notas\n");

        for (FiniquitoDTO.ConceptoFiniquitoDTO concepto : finiquito.getConceptos()) {
            csv.append(String.format("%s,%s,%s,%s,%s,%s\n",
                    concepto.getFecha() != null ? concepto.getFecha().format(DATE_FORMATTER) : "",
                    escapeCsv(concepto.getConcepto()),
                    concepto.getTipo(),
                    concepto.getMonto(),
                    concepto.getEstado(),
                    escapeCsv(concepto.getNotas() != null ? concepto.getNotas() : "")));
        }

        return csv.toString().getBytes();
    }

    // ========== REPORTE MENSUAL EXPORTS ==========

    public byte[] exportReporteMensualExcel(ReporteMensualDTO reporte) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // Hoja de resumen
            Sheet resumenSheet = workbook.createSheet("Resumen");
            CellStyle headerStyle = createHeaderStyle(workbook);
            int rowNum = 0;

            Row titleRow = resumenSheet.createRow(rowNum++);
            titleRow.createCell(0).setCellValue("REPORTE MENSUAL - " + reporte.getPeriodoDescripcion().toUpperCase());

            rowNum++;

            createLabelValueRow(resumenSheet, rowNum++, "Fecha Generación:", reporte.getFechaGeneracion().format(DATE_FORMATTER));

            rowNum++;

            // Propiedades
            createLabelValueRow(resumenSheet, rowNum++, "Total Propiedades:", String.valueOf(reporte.getTotalPropiedades()));
            createLabelValueRow(resumenSheet, rowNum++, "Propiedades Ocupadas:", String.valueOf(reporte.getPropiedadesOcupadas()));
            createLabelValueRow(resumenSheet, rowNum++, "Propiedades Disponibles:", String.valueOf(reporte.getPropiedadesDisponibles()));
            createLabelValueRow(resumenSheet, rowNum++, "% Ocupación:", reporte.getPorcentajeOcupacion() + "%");

            rowNum++;

            // Contratos
            createLabelValueRow(resumenSheet, rowNum++, "Contratos Activos:", String.valueOf(reporte.getContratosActivos()));
            createLabelValueRow(resumenSheet, rowNum++, "Contratos Por Vencer:", String.valueOf(reporte.getContratosPorVencer()));
            createLabelValueRow(resumenSheet, rowNum++, "Contratos Vencidos:", String.valueOf(reporte.getContratosVencidos()));

            rowNum++;

            // Financiero
            createLabelValueRow(resumenSheet, rowNum++, "Renta Esperada:", formatMoney(reporte.getRentaEsperada()));
            createLabelValueRow(resumenSheet, rowNum++, "Renta Cobrada:", formatMoney(reporte.getRentaCobrada()));
            createLabelValueRow(resumenSheet, rowNum++, "% Cobranza:", reporte.getPorcentajeCobranza() + "%");
            createLabelValueRow(resumenSheet, rowNum++, "Total Ingresos:", formatMoney(reporte.getTotalIngresos()));

            rowNum++;

            // Cartera
            createLabelValueRow(resumenSheet, rowNum++, "Cartera Vigente:", formatMoney(reporte.getCarteraVigente()));
            createLabelValueRow(resumenSheet, rowNum++, "Cartera Vencida:", formatMoney(reporte.getCarteraVencida()));
            createLabelValueRow(resumenSheet, rowNum++, "Cartera Total:", formatMoney(reporte.getCarteraTotal()));

            resumenSheet.autoSizeColumn(0);
            resumenSheet.autoSizeColumn(1);

            // Hoja de propiedades
            Sheet propiedadesSheet = workbook.createSheet("Propiedades");
            rowNum = 0;

            Row propHeaderRow = propiedadesSheet.createRow(rowNum++);
            String[] propHeaders = {"Dirección", "Tipo", "Estado", "Arrendatario", "Renta", "Cobrado", "Pendiente", "Estado Pago"};
            for (int i = 0; i < propHeaders.length; i++) {
                Cell cell = propHeaderRow.createCell(i);
                cell.setCellValue(propHeaders[i]);
                cell.setCellStyle(headerStyle);
            }

            for (ReporteMensualDTO.PropiedadMensualDTO prop : reporte.getDetallePropiedades()) {
                Row row = propiedadesSheet.createRow(rowNum++);
                row.createCell(0).setCellValue(prop.getDireccion());
                row.createCell(1).setCellValue(prop.getTipoPropiedad());
                row.createCell(2).setCellValue(prop.getEstadoOcupacion());
                row.createCell(3).setCellValue(prop.getArrendatario());
                row.createCell(4).setCellValue(prop.getRentaMensual() != null ? prop.getRentaMensual().doubleValue() : 0);
                row.createCell(5).setCellValue(prop.getRentaCobrada() != null ? prop.getRentaCobrada().doubleValue() : 0);
                row.createCell(6).setCellValue(prop.getSaldoPendiente() != null ? prop.getSaldoPendiente().doubleValue() : 0);
                row.createCell(7).setCellValue(prop.getEstadoPago());
            }

            for (int i = 0; i < propHeaders.length; i++) {
                propiedadesSheet.autoSizeColumn(i);
            }

            // Hoja de ingresos
            Sheet ingresosSheet = workbook.createSheet("Ingresos");
            rowNum = 0;

            Row ingHeaderRow = ingresosSheet.createRow(rowNum++);
            String[] ingHeaders = {"Fecha", "Concepto", "Propiedad", "Cliente", "Monto", "Tipo Pago", "Referencia"};
            for (int i = 0; i < ingHeaders.length; i++) {
                Cell cell = ingHeaderRow.createCell(i);
                cell.setCellValue(ingHeaders[i]);
                cell.setCellStyle(headerStyle);
            }

            for (ReporteMensualDTO.IngresoMensualDTO ing : reporte.getDetalleIngresos()) {
                Row row = ingresosSheet.createRow(rowNum++);
                row.createCell(0).setCellValue(ing.getFecha() != null ? ing.getFecha().format(DATE_FORMATTER) : "");
                row.createCell(1).setCellValue(ing.getConcepto());
                row.createCell(2).setCellValue(ing.getPropiedad());
                row.createCell(3).setCellValue(ing.getCliente());
                row.createCell(4).setCellValue(ing.getMonto() != null ? ing.getMonto().doubleValue() : 0);
                row.createCell(5).setCellValue(ing.getTipoPago());
                row.createCell(6).setCellValue(ing.getReferencia() != null ? ing.getReferencia() : "");
            }

            for (int i = 0; i < ingHeaders.length; i++) {
                ingresosSheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] exportReporteMensualCsv(ReporteMensualDTO reporte) {
        StringBuilder csv = new StringBuilder();
        csv.append("Dirección,Tipo,Estado Ocupación,Arrendatario,Renta Mensual,Renta Cobrada,Saldo Pendiente,Estado Pago\n");

        for (ReporteMensualDTO.PropiedadMensualDTO prop : reporte.getDetallePropiedades()) {
            csv.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s\n",
                    escapeCsv(prop.getDireccion()),
                    escapeCsv(prop.getTipoPropiedad()),
                    prop.getEstadoOcupacion(),
                    escapeCsv(prop.getArrendatario()),
                    prop.getRentaMensual(),
                    prop.getRentaCobrada(),
                    prop.getSaldoPendiente(),
                    prop.getEstadoPago()));
        }

        return csv.toString().getBytes();
    }

    // ========== ESTADO DE CUENTA MENSUAL EXPORTS ==========

    public byte[] exportEstadoCuentaMensualExcel(EstadoCuentaMensualDTO estadoCuenta) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Estado de Cuenta Mensual");

            CellStyle headerStyle = createHeaderStyle(workbook);
            int rowNum = 0;

            // Título
            Row titleRow = sheet.createRow(rowNum++);
            titleRow.createCell(0).setCellValue("ESTADO DE CUENTA MENSUAL - " + estadoCuenta.getPeriodoDescripcion().toUpperCase());

            rowNum++;

            // Información del cliente
            createLabelValueRow(sheet, rowNum++, "Cliente:", estadoCuenta.getNombreCliente());
            createLabelValueRow(sheet, rowNum++, "RFC:", estadoCuenta.getRfc());
            createLabelValueRow(sheet, rowNum++, "Email:", estadoCuenta.getEmail() != null ? estadoCuenta.getEmail() : "");
            createLabelValueRow(sheet, rowNum++, "Teléfono:", estadoCuenta.getTelefono() != null ? estadoCuenta.getTelefono() : "");
            createLabelValueRow(sheet, rowNum++, "Fecha de generación:", estadoCuenta.getFechaGeneracion().format(DATE_FORMATTER));

            rowNum++;

            // Resumen del mes
            createLabelValueRow(sheet, rowNum++, "Saldo Inicial:", formatMoney(estadoCuenta.getSaldoInicial()));
            createLabelValueRow(sheet, rowNum++, "Total Cargos:", formatMoney(estadoCuenta.getTotalCargos()));
            createLabelValueRow(sheet, rowNum++, "Total Abonos:", formatMoney(estadoCuenta.getTotalAbonos()));
            createLabelValueRow(sheet, rowNum++, "Saldo Final:", formatMoney(estadoCuenta.getSaldoFinal()));
            createLabelValueRow(sheet, rowNum++, "Saldo Vencido:", formatMoney(estadoCuenta.getSaldoVencido()));
            createLabelValueRow(sheet, rowNum++, "Saldo Por Vencer:", formatMoney(estadoCuenta.getSaldoPorVencer()));
            createLabelValueRow(sheet, rowNum++, "Días Promedio Vencido:", String.valueOf(estadoCuenta.getDiasPromedioVencido()));

            rowNum++;

            // Headers de movimientos
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {"Fecha", "Concepto", "Tipo", "Cargo", "Abono", "Saldo", "Propiedad", "Estado"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Datos de movimientos
            for (EstadoCuentaMensualDTO.MovimientoMensualDTO item : estadoCuenta.getMovimientos()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(item.getFecha() != null ? item.getFecha().format(DATE_FORMATTER) : "");
                row.createCell(1).setCellValue(item.getConcepto());
                row.createCell(2).setCellValue(item.getTipo());
                row.createCell(3).setCellValue(item.getCargo() != null ? item.getCargo().doubleValue() : 0);
                row.createCell(4).setCellValue(item.getAbono() != null ? item.getAbono().doubleValue() : 0);
                row.createCell(5).setCellValue(item.getSaldoAcumulado() != null ? item.getSaldoAcumulado().doubleValue() : 0);
                row.createCell(6).setCellValue(item.getPropiedad() != null ? item.getPropiedad() : "");
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

    public byte[] exportEstadoCuentaMensualCsv(EstadoCuentaMensualDTO estadoCuenta) {
        StringBuilder csv = new StringBuilder();
        csv.append("Fecha,Concepto,Tipo,Cargo,Abono,Saldo,Propiedad,Estado\n");

        for (EstadoCuentaMensualDTO.MovimientoMensualDTO item : estadoCuenta.getMovimientos()) {
            csv.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s\n",
                    item.getFecha() != null ? item.getFecha().format(DATE_FORMATTER) : "",
                    escapeCsv(item.getConcepto()),
                    item.getTipo(),
                    item.getCargo(),
                    item.getAbono(),
                    item.getSaldoAcumulado(),
                    escapeCsv(item.getPropiedad() != null ? item.getPropiedad() : ""),
                    item.getEstado() != null ? item.getEstado() : ""));
        }

        return csv.toString().getBytes();
    }
}
