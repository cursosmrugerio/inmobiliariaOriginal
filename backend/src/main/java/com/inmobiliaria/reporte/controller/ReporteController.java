package com.inmobiliaria.reporte.controller;

import com.inmobiliaria.reporte.dto.*;
import com.inmobiliaria.reporte.service.ExportService;
import com.inmobiliaria.reporte.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'AGENTE')")
public class ReporteController {

    private final ReporteService reporteService;
    private final ExportService exportService;

    // ========== ESTADO DE CUENTA (#39) ==========

    @GetMapping("/estado-cuenta/{personaId}")
    public ResponseEntity<EstadoCuentaDTO> getEstadoCuenta(
            @PathVariable Long personaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return ResponseEntity.ok(reporteService.generarEstadoCuenta(personaId, fechaInicio, fechaFin));
    }

    @GetMapping("/estado-cuenta/{personaId}/excel")
    public ResponseEntity<byte[]> exportEstadoCuentaExcel(
            @PathVariable Long personaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) throws IOException {
        EstadoCuentaDTO estadoCuenta = reporteService.generarEstadoCuenta(personaId, fechaInicio, fechaFin);
        byte[] excelData = exportService.exportEstadoCuentaExcel(estadoCuenta);

        String filename = "estado_cuenta_" + personaId + "_" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelData);
    }

    @GetMapping("/estado-cuenta/{personaId}/csv")
    public ResponseEntity<byte[]> exportEstadoCuentaCsv(
            @PathVariable Long personaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        EstadoCuentaDTO estadoCuenta = reporteService.generarEstadoCuenta(personaId, fechaInicio, fechaFin);
        byte[] csvData = exportService.exportEstadoCuentaCsv(estadoCuenta);

        String filename = "estado_cuenta_" + personaId + "_" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }

    // ========== ANTIGÜEDAD DE SALDOS (#40) ==========

    @GetMapping("/antiguedad-saldos")
    public ResponseEntity<AntiguedadSaldosDTO> getAntiguedadSaldos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaCorte) {
        return ResponseEntity.ok(reporteService.generarAntiguedadSaldos(fechaCorte));
    }

    @GetMapping("/antiguedad-saldos/excel")
    public ResponseEntity<byte[]> exportAntiguedadSaldosExcel(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaCorte) throws IOException {
        AntiguedadSaldosDTO antiguedad = reporteService.generarAntiguedadSaldos(fechaCorte);
        byte[] excelData = exportService.exportAntiguedadSaldosExcel(antiguedad);

        String filename = "antiguedad_saldos_" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelData);
    }

    @GetMapping("/antiguedad-saldos/csv")
    public ResponseEntity<byte[]> exportAntiguedadSaldosCsv(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaCorte) {
        AntiguedadSaldosDTO antiguedad = reporteService.generarAntiguedadSaldos(fechaCorte);
        byte[] csvData = exportService.exportAntiguedadSaldosCsv(antiguedad);

        String filename = "antiguedad_saldos_" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }

    // ========== CARTERA VENCIDA (#41) ==========

    @GetMapping("/cartera-vencida")
    public ResponseEntity<ReporteCarteraVencidaDTO> getCarteraVencida(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaCorte) {
        return ResponseEntity.ok(reporteService.generarReporteCarteraVencida(fechaCorte));
    }

    @GetMapping("/cartera-vencida/excel")
    public ResponseEntity<byte[]> exportCarteraVencidaExcel(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaCorte) throws IOException {
        ReporteCarteraVencidaDTO cartera = reporteService.generarReporteCarteraVencida(fechaCorte);
        byte[] excelData = exportService.exportCarteraVencidaExcel(cartera);

        String filename = "cartera_vencida_" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelData);
    }

    @GetMapping("/cartera-vencida/csv")
    public ResponseEntity<byte[]> exportCarteraVencidaCsv(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaCorte) {
        ReporteCarteraVencidaDTO cartera = reporteService.generarReporteCarteraVencida(fechaCorte);
        byte[] csvData = exportService.exportCarteraVencidaCsv(cartera);

        String filename = "cartera_vencida_" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }

    // ========== PROYECCIÓN DE COBRANZA (#42) ==========

    @GetMapping("/proyeccion")
    public ResponseEntity<ProyeccionCobranzaReporteDTO> getProyeccion(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodoInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodoFin) {
        return ResponseEntity.ok(reporteService.generarReporteProyeccion(periodoInicio, periodoFin));
    }

    @GetMapping("/proyeccion/excel")
    public ResponseEntity<byte[]> exportProyeccionExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodoInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodoFin) throws IOException {
        ProyeccionCobranzaReporteDTO proyeccion = reporteService.generarReporteProyeccion(periodoInicio, periodoFin);
        byte[] excelData = exportService.exportProyeccionExcel(proyeccion);

        String filename = "proyeccion_cobranza_" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelData);
    }

    @GetMapping("/proyeccion/csv")
    public ResponseEntity<byte[]> exportProyeccionCsv(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodoInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodoFin) {
        ProyeccionCobranzaReporteDTO proyeccion = reporteService.generarReporteProyeccion(periodoInicio, periodoFin);
        byte[] csvData = exportService.exportProyeccionCsv(proyeccion);

        String filename = "proyeccion_cobranza_" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }

    // ========== FINIQUITO DE CONTRATO ==========

    @GetMapping("/finiquito/{contratoId}")
    public ResponseEntity<FiniquitoDTO> getFiniquito(@PathVariable Long contratoId) {
        return ResponseEntity.ok(reporteService.generarFiniquito(contratoId));
    }

    @GetMapping("/finiquito/{contratoId}/excel")
    public ResponseEntity<byte[]> exportFiniquitoExcel(@PathVariable Long contratoId) throws IOException {
        FiniquitoDTO finiquito = reporteService.generarFiniquito(contratoId);
        byte[] excelData = exportService.exportFiniquitoExcel(finiquito);

        String filename = "finiquito_" + finiquito.getNumeroContrato() + "_" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelData);
    }

    @GetMapping("/finiquito/{contratoId}/csv")
    public ResponseEntity<byte[]> exportFiniquitoCsv(@PathVariable Long contratoId) {
        FiniquitoDTO finiquito = reporteService.generarFiniquito(contratoId);
        byte[] csvData = exportService.exportFiniquitoCsv(finiquito);

        String filename = "finiquito_" + finiquito.getNumeroContrato() + "_" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }

    // ========== REPORTE MENSUAL ==========

    @GetMapping("/mensual")
    public ResponseEntity<ReporteMensualDTO> getReporteMensual(
            @RequestParam Integer mes,
            @RequestParam Integer anio) {
        return ResponseEntity.ok(reporteService.generarReporteMensual(mes, anio));
    }

    @GetMapping("/mensual/excel")
    public ResponseEntity<byte[]> exportReporteMensualExcel(
            @RequestParam Integer mes,
            @RequestParam Integer anio) throws IOException {
        ReporteMensualDTO reporte = reporteService.generarReporteMensual(mes, anio);
        byte[] excelData = exportService.exportReporteMensualExcel(reporte);

        String filename = "reporte_mensual_" + anio + "_" + String.format("%02d", mes) + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelData);
    }

    @GetMapping("/mensual/csv")
    public ResponseEntity<byte[]> exportReporteMensualCsv(
            @RequestParam Integer mes,
            @RequestParam Integer anio) {
        ReporteMensualDTO reporte = reporteService.generarReporteMensual(mes, anio);
        byte[] csvData = exportService.exportReporteMensualCsv(reporte);

        String filename = "reporte_mensual_" + anio + "_" + String.format("%02d", mes) + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }
}
