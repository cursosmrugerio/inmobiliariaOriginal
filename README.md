# Sistema Inmobiliario Multi-tenant

Sistema de gestión inmobiliaria para administración de propiedades, contratos de arrendamiento, pagos y cobranza. Soporta múltiples empresas (inmobiliarias) con aislamiento completo de datos.

## Características Principales

- **Multi-tenant**: Cada inmobiliaria opera de forma aislada
- **Gestión de Clientes**: Registro y administración de clientes, propietarios y avales
- **Gestión de Propiedades**: Catálogo de inmuebles con características detalladas
- **Contratos de Arrendamiento**: Creación, renovación y terminación de contratos
- **Control de Pagos**: Registro de pagos con múltiples métodos (transferencia, efectivo, cheque)
- **Cobranza**: Cartera vencida, proyecciones y seguimiento
- **Reportes**: Estados de cuenta, antigüedad de saldos (Excel/CSV)
- **Notificaciones**: Alertas automáticas por email y WhatsApp

## Stack Tecnológico

| Componente | Tecnología |
|------------|------------|
| Backend | Java 21, Spring Boot 3.x, Spring Modulith |
| Frontend | React 18, TypeScript, Vite, Material-UI 7 |
| Base de datos | PostgreSQL |
| Autenticación | Spring Security, JWT |

## Requisitos

- Java 21+
- Node.js 18+
- PostgreSQL 15+
- Maven 3.9+

## Instalación

### Backend

```bash
cd backend
./mvnw clean install
./mvnw spring-boot:run
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

## Configuración

### Base de datos

Crear base de datos PostgreSQL:

```sql
CREATE DATABASE inmobiliaria;
```

Configurar en `backend/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/inmobiliaria
    username: postgres
    password: your_password
```

## Documentación

- [Requerimientos](./requerimientos.pdf) - Documento de requerimientos funcionales
- [CLAUDE.md](./CLAUDE.md) - Guía técnica del proyecto

## Licencia

Propietario - Todos los derechos reservados

## Contacto

- Miguel Ángel Rugerio Flores
- Emilio Benavides
