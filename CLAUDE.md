# CLAUDE.md - Sistema Inmobiliario Multi-tenant

## Descripción del Proyecto
Sistema de gestión inmobiliaria multi-tenant para administración de propiedades, contratos de arrendamiento, pagos y cobranza.

## Stack Tecnológico

### Backend
- **Java 21** + **Spring Boot 3.x**
- **Spring Modulith** - Arquitectura modular
- **PostgreSQL** - Base de datos
- **Spring Security** - Autenticación y autorización
- **Spring Data JPA** - Persistencia

### Frontend
- **React 18** + **TypeScript**
- **Vite** - Build tool
- **Material-UI 7** - Componentes UI
- **Zod** - Validación de formularios

### Multi-tenancy
- Patrón discriminador con `empresa_id`
- Cada inmobiliaria es un tenant aislado
- Row-Level Security en PostgreSQL

## Estructura del Proyecto

```
inmobiliariaOriginal/
├── backend/
│   └── src/main/java/com/inmobiliaria/
│       ├── empresa/        # Módulo tenant root
│       ├── persona/        # Clientes, propietarios, avales
│       ├── propiedad/      # Propiedades
│       ├── contrato/       # Contratos de arrendamiento
│       ├── pago/           # Pagos y cobranza
│       ├── notificacion/   # Email/WhatsApp
│       └── reporte/        # Reportes Excel/CSV
├── frontend/
│   └── src/
│       ├── components/
│       ├── pages/
│       ├── services/
│       └── context/
└── docs/
```

## Comandos Útiles

### Backend
```bash
cd backend
./mvnw spring-boot:run
./mvnw test
./mvnw verify  # Verificar módulos Spring Modulith
```

### Frontend
```bash
cd frontend
npm install
npm run dev
npm run build
npm run test
```

## Módulos Funcionales

1. **Gestión de Clientes** - CRUD de personas (clientes, propietarios, avales)
2. **Gestión de Propiedades** - Registro y administración de inmuebles
3. **Gestión de Contratos** - Contratos de arrendamiento
4. **Gestión de Pagos** - Registro de pagos y control de morosidad
5. **Control de Cobranza** - Cartera vencida y proyecciones
6. **Reportes** - Estados de cuenta, antigüedad de saldos (Excel/CSV)
7. **Notificaciones** - Alertas por email y WhatsApp

## Roles de Usuario

- **Administrador del Sistema** - Configuración y gestión de usuarios
- **Agente Operario** - Gestión operativa de propiedades y contratos

## Convenciones de Código

- Usar Lombok para reducir boilerplate
- DTOs para transferencia de datos entre capas
- Validación con Jakarta Validation
- Tests unitarios con JUnit 5 y Mockito
- Tests de integración con Testcontainers

## Multi-tenancy Implementation

Todas las entidades principales incluyen `empresaId` como discriminador:
- Filtros Hibernate automáticos por tenant
- TenantContext en ThreadLocal
- Spring Security extrae tenant del JWT
