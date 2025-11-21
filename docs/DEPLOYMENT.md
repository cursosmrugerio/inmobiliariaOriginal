# Guía de Deployment - Sistema Inmobiliario

## Índice
- [Requisitos Previos](#requisitos-previos)
- [Deployment Local con Docker](#deployment-local-con-docker)
- [Deployment a Google Cloud Run](#deployment-a-google-cloud-run)
- [Configuración de Variables de Entorno](#configuración-de-variables-de-entorno)
- [CI/CD con GitHub Actions](#cicd-con-github-actions)

---

## Requisitos Previos

### Local
- Docker 20.10+
- Docker Compose 2.0+
- Git

### Cloud Run
- Cuenta de Google Cloud Platform
- gcloud CLI instalado
- Proyecto GCP con APIs habilitadas:
  - Cloud Run API
  - Artifact Registry API
  - Secret Manager API
  - Cloud SQL API (si usa Cloud SQL)

---

## Deployment Local con Docker

### 1. Configurar Variables de Entorno

```bash
# Copiar archivo de ejemplo
cp .env.example .env

# Editar variables según tu entorno
nano .env
```

### 2. Iniciar Servicios

```bash
# Construir e iniciar todos los servicios
docker-compose up --build

# O ejecutar en background
docker-compose up -d --build
```

### 3. Verificar Servicios

- **Frontend**: http://localhost:80
- **Backend API**: http://localhost:8080
- **Health Check Backend**: http://localhost:8080/actuator/health

### 4. Comandos Útiles

```bash
# Ver logs
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs -f backend

# Reiniciar servicios
docker-compose restart

# Detener servicios
docker-compose down

# Eliminar volúmenes (resetear BD)
docker-compose down -v
```

---

## Deployment a Google Cloud Run

### 1. Configuración Inicial de GCP

```bash
# Autenticar con GCP
gcloud auth login

# Configurar proyecto
gcloud config set project YOUR_PROJECT_ID

# Habilitar APIs necesarias
gcloud services enable run.googleapis.com
gcloud services enable artifactregistry.googleapis.com
gcloud services enable secretmanager.googleapis.com
```

### 2. Crear Artifact Registry

```bash
gcloud artifacts repositories create inmobiliaria \
    --repository-format=docker \
    --location=us-central1 \
    --description="Docker images for inmobiliaria system"
```

### 3. Configurar Secretos

```bash
# Crear secretos en Secret Manager
echo -n "your-db-host" | gcloud secrets create DB_HOST --data-file=-
echo -n "5432" | gcloud secrets create DB_PORT --data-file=-
echo -n "inmobiliaria" | gcloud secrets create DB_NAME --data-file=-
echo -n "your-db-user" | gcloud secrets create DB_USERNAME --data-file=-
echo -n "your-db-password" | gcloud secrets create DB_PASSWORD --data-file=-
echo -n "your-jwt-secret" | gcloud secrets create JWT_SECRET --data-file=-
echo -n "smtp.gmail.com" | gcloud secrets create MAIL_HOST --data-file=-
echo -n "587" | gcloud secrets create MAIL_PORT --data-file=-
echo -n "your-email@gmail.com" | gcloud secrets create MAIL_USERNAME --data-file=-
echo -n "your-app-password" | gcloud secrets create MAIL_PASSWORD --data-file=-
```

### 4. Deployment Manual

#### Backend

```bash
# Construir imagen
docker build -t us-central1-docker.pkg.dev/YOUR_PROJECT/inmobiliaria/backend:latest ./backend

# Push a Artifact Registry
docker push us-central1-docker.pkg.dev/YOUR_PROJECT/inmobiliaria/backend:latest

# Deploy a Cloud Run
gcloud run deploy inmobiliaria-backend \
    --image us-central1-docker.pkg.dev/YOUR_PROJECT/inmobiliaria/backend:latest \
    --region us-central1 \
    --platform managed \
    --allow-unauthenticated \
    --set-env-vars="SPRING_PROFILES_ACTIVE=prod" \
    --set-secrets="DB_HOST=DB_HOST:latest,DB_PORT=DB_PORT:latest,DB_NAME=DB_NAME:latest,DB_USERNAME=DB_USERNAME:latest,DB_PASSWORD=DB_PASSWORD:latest,JWT_SECRET=JWT_SECRET:latest,MAIL_HOST=MAIL_HOST:latest,MAIL_PORT=MAIL_PORT:latest,MAIL_USERNAME=MAIL_USERNAME:latest,MAIL_PASSWORD=MAIL_PASSWORD:latest"
```

#### Frontend

```bash
# Obtener URL del backend
BACKEND_URL=$(gcloud run services describe inmobiliaria-backend --region=us-central1 --format='value(status.url)')

# Construir imagen frontend
docker build -t us-central1-docker.pkg.dev/YOUR_PROJECT/inmobiliaria/frontend:latest ./frontend

# Push a Artifact Registry
docker push us-central1-docker.pkg.dev/YOUR_PROJECT/inmobiliaria/frontend:latest

# Deploy a Cloud Run
gcloud run deploy inmobiliaria-frontend \
    --image us-central1-docker.pkg.dev/YOUR_PROJECT/inmobiliaria/frontend:latest \
    --region us-central1 \
    --platform managed \
    --allow-unauthenticated
```

---

## Configuración de Variables de Entorno

### Variables Requeridas

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `DB_HOST` | Host de PostgreSQL | `localhost` o IP de Cloud SQL |
| `DB_PORT` | Puerto de PostgreSQL | `5432` |
| `DB_NAME` | Nombre de la base de datos | `inmobiliaria` |
| `DB_USERNAME` | Usuario de PostgreSQL | `postgres` |
| `DB_PASSWORD` | Contraseña de PostgreSQL | `your-password` |
| `JWT_SECRET` | Clave secreta para JWT | `min-32-characters-secret-key` |

### Variables Opcionales

| Variable | Descripción | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Perfil de Spring | `dev` |
| `MAIL_HOST` | Servidor SMTP | `smtp.gmail.com` |
| `MAIL_PORT` | Puerto SMTP | `587` |
| `MAIL_USERNAME` | Usuario email | - |
| `MAIL_PASSWORD` | Contraseña email | - |
| `WHATSAPP_API_URL` | URL de API WhatsApp | - |
| `WHATSAPP_API_TOKEN` | Token de API WhatsApp | - |

---

## CI/CD con GitHub Actions

### Configurar Secrets en GitHub

En Settings > Secrets and variables > Actions, agregar:

| Secret | Descripción |
|--------|-------------|
| `GCP_PROJECT_ID` | ID del proyecto GCP |
| `WIF_PROVIDER` | Proveedor de Workload Identity Federation |
| `WIF_SERVICE_ACCOUNT` | Service account para WIF |

### Configurar Workload Identity Federation

```bash
# Crear pool de identidad
gcloud iam workload-identity-pools create "github-pool" \
    --location="global" \
    --display-name="GitHub Pool"

# Crear proveedor
gcloud iam workload-identity-pools providers create-oidc "github-provider" \
    --location="global" \
    --workload-identity-pool="github-pool" \
    --display-name="GitHub Provider" \
    --issuer-uri="https://token.actions.githubusercontent.com" \
    --attribute-mapping="google.subject=assertion.sub,attribute.actor=assertion.actor,attribute.repository=assertion.repository"

# Crear service account
gcloud iam service-accounts create github-actions \
    --display-name="GitHub Actions"

# Asignar roles
gcloud projects add-iam-policy-binding YOUR_PROJECT_ID \
    --member="serviceAccount:github-actions@YOUR_PROJECT_ID.iam.gserviceaccount.com" \
    --role="roles/run.admin"

gcloud projects add-iam-policy-binding YOUR_PROJECT_ID \
    --member="serviceAccount:github-actions@YOUR_PROJECT_ID.iam.gserviceaccount.com" \
    --role="roles/artifactregistry.writer"

gcloud projects add-iam-policy-binding YOUR_PROJECT_ID \
    --member="serviceAccount:github-actions@YOUR_PROJECT_ID.iam.gserviceaccount.com" \
    --role="roles/secretmanager.secretAccessor"

# Permitir autenticación desde GitHub
gcloud iam service-accounts add-iam-policy-binding \
    github-actions@YOUR_PROJECT_ID.iam.gserviceaccount.com \
    --role="roles/iam.workloadIdentityUser" \
    --member="principalSet://iam.googleapis.com/projects/PROJECT_NUMBER/locations/global/workloadIdentityPools/github-pool/attribute.repository/YOUR_ORG/YOUR_REPO"
```

### Workflows Disponibles

1. **CI** (`.github/workflows/ci.yml`): Se ejecuta en cada push/PR
   - Ejecuta tests de backend con PostgreSQL
   - Ejecuta tests y lint de frontend
   - Construye artefactos

2. **Deploy** (`.github/workflows/deploy-cloudrun.yml`): Se ejecuta en push a main
   - Construye y sube imágenes Docker
   - Deploya a Cloud Run
   - Configura secretos automáticamente

---

## Base de Datos en Producción

### Opción 1: Cloud SQL

```bash
# Crear instancia
gcloud sql instances create inmobiliaria-db \
    --database-version=POSTGRES_15 \
    --tier=db-f1-micro \
    --region=us-central1

# Crear base de datos
gcloud sql databases create inmobiliaria --instance=inmobiliaria-db

# Crear usuario
gcloud sql users create app-user \
    --instance=inmobiliaria-db \
    --password=YOUR_PASSWORD
```

### Opción 2: Base de Datos Externa

Configura las variables de entorno con los datos de conexión de tu proveedor de PostgreSQL preferido (Supabase, Neon, Railway, etc.).

---

## Monitoreo y Logs

### Ver Logs en Cloud Run

```bash
# Logs del backend
gcloud logs read "resource.type=cloud_run_revision AND resource.labels.service_name=inmobiliaria-backend" --limit=50

# Logs del frontend
gcloud logs read "resource.type=cloud_run_revision AND resource.labels.service_name=inmobiliaria-frontend" --limit=50
```

### Health Checks

- Backend: `GET /actuator/health`
- Frontend: `GET /health`

---

## Troubleshooting

### Error: Container failed to start

1. Verificar logs: `gcloud run services logs read SERVICE_NAME`
2. Verificar que los secretos estén configurados correctamente
3. Verificar conexión a base de datos

### Error: Database connection refused

1. Verificar que Cloud SQL esté corriendo
2. Verificar configuración de red/VPC
3. Verificar credenciales en Secret Manager

### Error: Frontend no conecta con backend

1. Verificar que la URL del backend esté correcta
2. Verificar configuración CORS en backend
3. Verificar que nginx.conf apunte al servicio correcto

---

## Actualización de la Aplicación

```bash
# Reconstruir y desplegar
docker-compose build
docker-compose up -d

# O con GitHub Actions
git push origin main  # Dispara deployment automático
```
