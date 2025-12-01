# Análisis Gap: Requerimientos vs. Implementación Actual

**Fecha:** 1 de diciembre de 2025
**Última revisión:** 1 de diciembre de 2025
**Versión del documento de requerimientos:** 1.1 (11 de febrero de 2025)

## Resumen Ejecutivo

Después de analizar exhaustivamente el documento de requerimientos (requerimientos.pdf) y el codebase actual, se identifica que el sistema tiene **aproximadamente 87% de completitud general**.

> ⚠️ **ALERTA CRÍTICA**: No existe `AuthController`. Los endpoints de autenticación (`/api/auth/login`, `/api/auth/register`, `/api/auth/me`) que el frontend espera **no están implementados**. Esto es bloqueante para el funcionamiento del sistema.

A continuación se detalla qué está implementado, qué falta, y qué está parcialmente implementado.

---

## 1. GESTIÓN DE CLIENTES (Módulo Persona)

### Requerimientos del PDF:
- [x] Registro de clientes con información detallada (nombre, contacto, datos fiscales)
- [x] Búsqueda y visualización de clientes
- [x] Edición de datos de clientes
- [x] Asociación de clientes con contratos de arrendamiento

### Estado: **100% COMPLETADO**

El módulo `persona` está completamente implementado con:
- CRUD completo de personas (física/moral)
- Roles múltiples (PROPIETARIO, CLIENTE, AVAL, ARRENDATARIO)
- Direcciones múltiples por persona
- Cuentas bancarias
- Búsqueda por nombre, RFC, email
- Filtro por tipo de persona

---

## 2. GESTIÓN DE PROPIEDADES

### Requerimientos del PDF:
- [x] Registro y edición de propiedades con detalles sobre ubicación, tipo, características y estado
- [x] Búsqueda y visualización de propiedades
- [x] Asociación de propiedades con contratos de arrendamiento

### Estado: **100% COMPLETADO**

El módulo `propiedad` está completamente implementado con:
- CRUD completo con todos los campos del ERD
- Múltiples propietarios por propiedad con porcentaje
- Catálogo de tipos de propiedad
- Estado de disponibilidad
- Integración con direcciones geográficas

---

## 3. GESTIÓN DE CONTRATOS DE ARRENDAMIENTO

### Requerimientos del PDF:
- [x] Creación y administración de contratos de arrendamiento
- [x] Definición de montos de renta, fianzas y depósitos
- [x] Registro de fechas de inicio y fin de contrato
- [x] Asociación de contratos con clientes y propiedades
- [x] Administración de renovaciones y terminaciones de contratos
- [x] Control de vencimientos de contratos

### Estado: **95% COMPLETADO**

**Implementado:**
- CRUD completo de contratos
- Estados: BORRADOR, ACTIVO, VENCIDO, TERMINADO, CANCELADO, RENOVADO
- Renovación con nuevo monto e incremento anual
- Detección automática de vencimientos
- Días de gracia y penalidades
- Asociación con propiedad, arrendatario y aval

**Faltante:**
- [ ] **Fianza/Póliza Jurídica** - El campo `deposito_garantia` existe pero no hay gestión de fianzas formales ni pólizas jurídicas mencionadas en el diagrama de procesos
- [ ] **Acta de entrega-recepción del inmueble** - No existe funcionalidad para documentar la entrega/recepción

---

## 4. GESTIÓN DE PAGOS

### Requerimientos del PDF:
- [x] Registro de pagos de renta y otros cargos (transferencia, efectivo, cheque, etc.)
- [x] Control de morosidad y envío de notificaciones por pagos pendientes

### Criterio de Aceptación CA-04:
> "Un pago registrado debe actualizar el saldo del contrato, reflejarse en el estado de cuenta del cliente y **generar un recibo**."

### Estado: **85% COMPLETADO**

**Implementado:**
- Registro de pagos con múltiples métodos (EFECTIVO, TRANSFERENCIA, CHEQUE, TARJETA_DEBITO, TARJETA_CREDITO, DEPOSITO_BANCARIO)
- Aplicación de pagos a cargos específicos
- Cargos fijos automáticos (generación de rentas mensuales)
- Control de morosidad
- Estados de pago y cargo

**Faltante:**
- [ ] **Generación de recibo de pago** (PDF/imprimible) - Crítico según CA-04. No hay librerías PDF (iText, OpenPDF). Solo Apache POI para Excel.
- [x] ~~**Recepción de comprobantes de alquiler con fotografía**~~ - Campo `comprobanteUrl` existe en `Pago.java:74`. Falta UI para carga de imágenes.
- [ ] **Depósito electrónico con referencia** - Mencionado en el PDF

---

## 5. CONTROL DE COBRANZA

### Requerimientos del PDF:
- [x] Administración de cartera vencida
- [x] Generación de proyecciones de cobranza
- [x] Notificaciones y recordatorios automáticos a clientes
- [x] Registro y seguimiento de cobranza
- [x] Informes de antigüedad de saldos y cartera vencida
- [x] Reportes detallados de proyección de cobranza con filtros avanzados

### Estado: **95% COMPLETADO**

**Implementado:**
- Cartera vencida con clasificación automática (VIGENTE, 1-30, 31-60, 61-90, +90 días)
- Seguimiento de cobranza con tipos de contacto y resultados
- Proyecciones mensuales vs. cobrado real
- Cálculo de penalidades
- Dashboard de resumen

**Faltante:**
- [ ] **Acumulación de penalidades progresivas** - El diagrama menciona "Procesos de acumulación de penalidades"

---

## 6. GENERACIÓN DE REPORTES

### Requerimientos del PDF:
- [x] Estado de cuenta de clientes
- [x] Antigüedad de saldos
- [x] Cartera vencida
- [x] Proyección de cobranza
- [ ] **Finiquitos de contrato** - NO IMPLEMENTADO
- [ ] **Reportes mensuales de estados de cuenta** - PARCIAL
- [x] Exportación de reportes en formato Excel

### Estado: **80% COMPLETADO**

**Implementado:**
- 4 tipos de reportes (Estado de Cuenta, Antigüedad, Cartera Vencida, Proyección)
- Exportación a Excel y CSV
- Filtros por período

**Faltante:**
- [ ] **Reporte de Finiquito de Contrato** - Debe calcular:
  - Saldo pendiente de rentas
  - Devolución de depósito
  - Deducción de daños/reparaciones
  - Saldo final a pagar/devolver
- [ ] **Generación automática de reportes mensuales** - Programados para envío

---

## 7. NOTIFICACIONES

### Requerimientos del PDF:
- [x] Notificaciones automáticas por correo electrónico y/o WhatsApp
- [x] Recordatorios de pagos y vencimientos de contratos
- [x] Alertas sobre próximos vencimientos de contratos con anticipación configurable

### Estado: **90% COMPLETADO**

**Implementado:**
- Envío de email y WhatsApp
- Alertas automáticas programadas (scheduler diario)
- Configuración por categoría (días anticipación, frecuencia, plantillas)
- Estados y reintentos

**Faltante:**
- [x] ~~**Recordatorio 7 días antes de fecha de pago**~~ - Default configurado en BD (`dias_anticipacion = 7` en V6__notificacion_module.sql). Funciona basándose en cartera vencida.
- [ ] **Alerta de renovación 1 mes de anticipación** - Método `procesarAlertasVencimientoContrato()` existe con default 30 días, pero tiene `TODO: Integrar con ContratosService` en `AlertSchedulerService.java:72`

---

## 8. SEGURIDAD Y CONTROL DE ACCESO

### Requerimientos del PDF:
- [ ] Implementación de autenticación y autorización por roles - **PARCIAL**
- [ ] **Definición de permisos según el rol del usuario** - PARCIAL

### Estado: **50% COMPLETADO**

**Implementado:**
- Infraestructura JWT (JwtAuthenticationFilter, SecurityConfig)
- Multi-tenancy con aislamiento por empresa
- CORS configurado
- Encriptación BCrypt de contraseñas
- Infraestructura para @PreAuthorize (`@EnableMethodSecurity` habilitado)
- Entity Usuario con roles (ADMINISTRADOR, AGENTE)
- UsuarioRepository con métodos básicos

**Faltante (CRÍTICO):**
- [ ] **Endpoints de Autenticación** - NO EXISTE `AuthController`. El frontend (`authService.ts`) llama a:
  - `POST /api/auth/login` - No implementado
  - `POST /api/auth/register` - No implementado
  - `GET /api/auth/me` - No implementado
  - `SecurityConfig.java:41` permite estas rutas pero no hay controlador que las implemente
- [ ] **Permisos granulares por rol** - Los roles existen pero no hay anotaciones `@PreAuthorize` en ningún endpoint (verificado: 0 usos en todo el backend)
- [ ] **Gestión de usuarios del sistema** - No existe `UsuarioController`. No hay endpoints CRUD para usuarios
- [ ] **Asignación de permisos específicos** - No implementado

---

## 9. FUNCIONALIDADES DEL DIAGRAMA DE PROCESOS (Página 13 del PDF)

### Gestión de Contratos - Ciclo de Vida:

**Finaliza contrato o Renovar:**
- [x] Se puede renovar (1 mes de anticipación)
- [x] Condiciones: incrementó alquiler y servicios
- [ ] **Fiador continúa, fianza** - No hay validación de fiador en renovación
- [x] Notificaciones vía correo, WhatsApp

**Cancelación:**
- [ ] **Cálculo de finiquito** - El PDF dice "no se hace por el sistema" pero debería implementarse
- [ ] **Firma de liquidación** - No hay workflow de firmas
- [ ] **Solicitud expresa del propietario** - No hay registro formal

### Ciclo Operativo:
- [x] **Recordar (7 días), avisar, alertar Fechas de pago** - Implementado via scheduler con default 7 días
- [ ] **Recepción de recibos de Servicios (fotografía)** - No implementado
- [x] **Recepción de comprobantes de alquiler** - Campo `comprobanteUrl` existe. Falta UI de carga
- [ ] **Depósito Electrónico con referencia** - Parcial
- [ ] **Procesos de acumulación de penalidades** - Parcial
- [x] **Levantamiento de Órdenes de Servicio o Mantenimiento y/o bitácora** - IMPLEMENTADO

### Ciclo Operativo de la Vigencia del Contrato:
- [x] Generar automáticamente cargos fijos al momento del alta
- [x] Capturar cargos variables manualmente
- [ ] **Concepto del Cargo con IVA** - No hay manejo de IVA
- [ ] **¿Facturado? Sí o No** - No hay campo de facturación
- [ ] **¿Emisión de recibo? Sí o No** - No hay generación de recibos
- [x] Generar Alertas, notificaciones, recordatorios
- [ ] **Manejo de Bitácora de solicitudes** - No hay bitácora general

---

## 10. ENTIDADES DEL ERD vs. IMPLEMENTACIÓN

| Entidad ERD | Implementado | Observaciones |
|-------------|--------------|---------------|
| EMPRESA | ✅ | Completo |
| PERSONA | ✅ | Completo |
| PERSONA_ROL | ✅ | Completo |
| CAT_ROLES | ✅ | Completo |
| DIRECCION | ✅ | Completo |
| PROPIEDAD | ✅ | Completo |
| CONTRATO | ✅ | Falta fianza/póliza |
| PAGO | ✅ | Falta recibo |
| DOCUMENTO | ✅ | Completo |
| MANTENIMIENTO | ✅ | Completo |
| PROVEEDOR | ✅ | Completo |
| CUENTA_BANCARIA | ✅ | Completo |
| CAT_ESTADOS | ✅ | Completo |
| CAT_MUNICIPIOS | ✅ | Completo |
| CAT_COLONIAS | ✅ | Completo |
| CAT_CODIGOS_POSTALES | ✅ | Completo |
| CAT_TIPOS_ASENTAMIENTO | ✅ | Completo |

---

## RESUMEN DE FUNCIONALIDADES FALTANTES

### Prioridad ALTA (Críticas para operación):

| # | Funcionalidad | Módulo | Justificación |
|---|---------------|--------|---------------|
| 0 | **Endpoints de Autenticación (AuthController)** | Seguridad | **BLOQUEANTE**: Sin esto el sistema no funciona. Frontend llama a `/api/auth/login`, `/api/auth/register`, `/api/auth/me` que no existen |
| 1 | Generación de Recibo de Pago (PDF) | Pagos | CA-04 lo requiere explícitamente |
| 2 | Permisos granulares por rol | Seguridad | CA-07 requiere control de acceso por rol |
| 3 | Gestión de Usuarios del Sistema (UsuarioController) | Seguridad | No hay forma de crear usuarios desde la UI |
| 4 | Reporte de Finiquito de Contrato | Reportes | Requerido en el PDF |

### Prioridad MEDIA (Mejoran la operación):

| # | Funcionalidad | Módulo | Justificación |
|---|---------------|--------|---------------|
| 5 | Acta de entrega-recepción del inmueble | Contratos | Documentación formal. Módulo Documento genérico existe, falta tipo específico |
| 6 | UI para carga de comprobantes con fotografía | Pagos | Campo `comprobanteUrl` existe, falta componente de upload en frontend |
| 7 | Integrar alertas de vencimiento de contrato | Notificaciones | Método existe con TODO pendiente en `AlertSchedulerService.java:72` |
| 8 | Manejo de IVA en cargos | Pagos | Para facturación |
| 9 | Campo de facturado Sí/No en cargos | Pagos | Control de facturación |

### Prioridad BAJA (Nice to have):

| # | Funcionalidad | Módulo | Justificación |
|---|---------------|--------|---------------|
| 10 | Bitácora general de solicitudes | General | Trazabilidad |
| 11 | Workflow de firma de liquidación | Contratos | Proceso formal |
| 12 | Generación automática de reportes mensuales | Reportes | Automatización |
| 13 | Fianza/Póliza jurídica como entidad separada | Contratos | Mejora gestión |

---

## ESTADÍSTICAS FINALES

| Módulo | Completitud | Notas |
|--------|-------------|-------|
| Gestión de Clientes | 100% | Completo |
| Gestión de Propiedades | 100% | Completo |
| Gestión de Contratos | 95% | Falta fianza/póliza |
| Gestión de Pagos | 88% | Falta PDF recibo. Campo comprobante existe |
| Control de Cobranza | 95% | Completo |
| Generación de Reportes | 80% | Falta finiquito |
| Notificaciones | 92% | Alertas vencimiento con TODO pendiente |
| Seguridad | **50%** | **Sin AuthController ni UsuarioController** |
| **PROMEDIO GENERAL** | **87%** | Reducido por seguridad |

---

## PRÓXIMOS PASOS RECOMENDADOS

Para completar el sistema según los requerimientos, se sugiere implementar en este orden:

### Fase 0: BLOQUEANTE
0. **Endpoints de Autenticación (AuthController)** - Sin esto el sistema no puede funcionar
   - `POST /api/auth/login` - Login con JWT
   - `POST /api/auth/register` - Registro de usuarios
   - `GET /api/auth/me` - Obtener usuario actual

### Fase 1: Críticos
1. Gestión de Usuarios (UsuarioController) - CRUD de usuarios
2. Generación de recibo de pago (PDF) - Agregar iText/OpenPDF
3. Permisos granulares (@PreAuthorize en endpoints)
4. Reporte de finiquito de contrato

### Fase 2: Operativos
5. Integrar alertas de vencimiento de contrato (completar TODO en AlertSchedulerService)
6. UI para carga de comprobantes (el campo `comprobanteUrl` ya existe)
7. Campos de IVA y facturación en cargos

### Fase 3: Complementarios
8. Acta de entrega-recepción (agregar tipo en enum TipoDocumento)
9. Bitácora de solicitudes
10. Reportes mensuales automáticos

---

## CRITERIOS DE ACEPTACIÓN - ESTADO

| ID | Funcionalidad | Estado | Observaciones |
|----|---------------|--------|---------------|
| CA-01 | Registro de Clientes | ✅ Cumple | Nombre, contacto, RFC visibles en lista |
| CA-02 | Gestión de Propiedades | ✅ Cumple | Ubicación, tipo, estado registrados |
| CA-03 | Creación de Contratos | ✅ Cumple | Validación de propiedad/cliente, fechas y monto |
| CA-04 | Gestión de Pagos | ⚠️ Parcial | Actualiza saldo y estado de cuenta, **FALTA RECIBO PDF** |
| CA-05 | Control de Cobranza | ✅ Cumple | Cartera vencida y notificaciones automáticas |
| CA-06 | Generación de Reportes | ✅ Cumple | Excel exportable, información en tiempo real |
| CA-07 | Seguridad y Acceso | ❌ No Cumple | **NO HAY AuthController**. Faltan permisos por rol |

---

## NOTAS DE REVISIÓN (1 de diciembre de 2025)

Esta revisión identificó las siguientes correcciones al análisis original:

1. **Campo `comprobanteUrl` ya existe** en `Pago.java:74` - Solo falta UI de carga
2. **Alertas de 7 días** funcionan via scheduler con default configurado en BD
3. **Alertas de vencimiento de contrato** tienen método pero con `TODO` pendiente
4. **CRÍTICO ENCONTRADO**: No existe `AuthController` - endpoints de login/register no implementados
5. Seguridad reducida de 70% a 50% por falta de autenticación funcional

---

*Documento generado automáticamente basado en análisis del codebase y requerimientos.pdf*
*Última revisión manual: 1 de diciembre de 2025*
