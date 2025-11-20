# Checklist de Implementación - Sistema Inmobiliario

## Progreso General
**Total: 0/62 issues completados**

---

## Milestone 1: Configuración Inicial (0/5)

- [ ] #1 - Configurar proyecto backend Spring Boot + Spring Modulith
- [ ] #2 - Configurar proyecto frontend React + Vite + MUI
- [ ] #3 - Configurar base de datos PostgreSQL
- [ ] #4 - Implementar arquitectura multi-tenant con empresa_id
- [ ] #5 - Configurar Spring Security con JWT

---

## Milestone 2: Catálogos Base (0/6)

- [ ] #6 - Implementar catálogo de Estados (CAT_ESTADOS)
- [ ] #7 - Implementar catálogo de Municipios (CAT_MUNICIPIOS)
- [ ] #8 - Implementar catálogo de Colonias (CAT_COLONIAS)
- [ ] #9 - Implementar catálogo de Códigos Postales
- [ ] #10 - Implementar catálogo de Tipos de Asentamiento
- [ ] #11 - Implementar catálogo de Roles (CAT_ROLES)

---

## Milestone 3: Módulo Empresa (0/3)

- [ ] #12 - CRUD de Empresas/Inmobiliarias
- [ ] #13 - Configuración de tenant context
- [ ] #14 - Frontend: Selector de empresa y dashboard

---

## Milestone 4: Módulo Personas (0/5)

- [ ] #15 - CRUD de Personas (física/moral)
- [ ] #16 - Gestión de roles por persona (PERSONA_ROL)
- [ ] #17 - CRUD de Direcciones
- [ ] #18 - CRUD de Cuentas Bancarias
- [ ] #19 - Frontend: Listado y formularios de personas

---

## Milestone 5: Módulo Propiedades (0/4)

- [ ] #20 - CRUD de Propiedades
- [ ] #21 - Catálogo de Tipos de Propiedad
- [ ] #22 - Asociación propiedad-propietario
- [ ] #23 - Frontend: Listado y formularios de propiedades

---

## Milestone 6: Módulo Contratos (0/5)

- [ ] #24 - CRUD de Contratos de Arrendamiento
- [ ] #25 - Asociación contrato-propiedad-arrendatario-aval
- [ ] #26 - Control de vencimientos de contratos
- [ ] #27 - Renovación y terminación de contratos
- [ ] #28 - Frontend: Gestión de contratos

---

## Milestone 7: Módulo Pagos (0/5)

- [ ] #29 - CRUD de Pagos
- [ ] #30 - Tipos de pago (transferencia, efectivo, cheque)
- [ ] #31 - Generación automática de cargos fijos
- [ ] #32 - Registro de cargos variables
- [ ] #33 - Frontend: Registro y consulta de pagos

---

## Milestone 8: Control de Cobranza (0/5)

- [ ] #34 - Administración de cartera vencida
- [ ] #35 - Cálculo de morosidad y penalidades
- [ ] #36 - Proyección de cobranza
- [ ] #37 - Seguimiento de cobranza
- [ ] #38 - Frontend: Dashboard de cobranza

---

## Milestone 9: Reportes (0/6)

- [ ] #39 - Estado de cuenta de clientes
- [ ] #40 - Reporte de antigüedad de saldos
- [ ] #41 - Reporte de cartera vencida
- [ ] #42 - Reporte de proyección de cobranza
- [ ] #43 - Exportación a Excel/CSV
- [ ] #44 - Frontend: Módulo de reportes

---

## Milestone 10: Notificaciones (0/5)

- [ ] #45 - Servicio de notificaciones por email
- [ ] #46 - Integración con WhatsApp
- [ ] #47 - Alertas de vencimiento de contratos
- [ ] #48 - Recordatorios de pagos pendientes
- [ ] #49 - Configuración de anticipación de alertas

---

## Milestone 11: Documentos (0/3)

- [ ] #50 - CRUD de Documentos
- [ ] #51 - Carga y almacenamiento de archivos
- [ ] #52 - Asociación documentos con entidades

---

## Milestone 12: Mantenimiento (0/3)

- [ ] #53 - CRUD de Proveedores
- [ ] #54 - CRUD de Órdenes de Mantenimiento
- [ ] #55 - Seguimiento de solicitudes de mantenimiento

---

## Milestone 13: Testing y QA (0/4)

- [ ] #56 - Tests unitarios backend
- [ ] #57 - Tests de integración con Testcontainers
- [ ] #58 - Tests E2E frontend con Playwright
- [ ] #59 - Verificación de módulos Spring Modulith

---

## Milestone 14: Deployment (0/3)

- [ ] #60 - Configurar CI/CD con GitHub Actions
- [ ] #61 - Configurar deployment a Cloud Run
- [ ] #62 - Documentación de deployment

---

## Notas de Implementación

### Orden de dependencias recomendado:
1. **Milestone 1** debe completarse primero (infraestructura base)
2. **Milestone 2** necesita BD configurada
3. **Milestone 3** habilita el multi-tenancy
4. **Milestones 4-7** pueden desarrollarse en paralelo parcialmente
5. **Milestones 8-10** dependen de pagos y contratos
6. **Milestones 11-12** son módulos independientes
7. **Milestones 13-14** al final del desarrollo

### Registro de cambios
| Fecha | Issue | Notas |
|-------|-------|-------|
| | | |

---

*Última actualización: Pendiente*
