import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Button,
  Paper,
  Grid,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Card,
  CardContent,
  Collapse,
} from '@mui/material';
import { Add, Storage, FolderOpen } from '@mui/icons-material';
import DocumentUpload from '../../components/documentos/DocumentUpload';
import DocumentList from '../../components/documentos/DocumentList';
import documentoService, {
  Documento,
  TipoDocumento,
  TipoEntidad,
} from '../../services/documentoService';

const DocumentosPage: React.FC = () => {
  const [documentos, setDocumentos] = useState<Documento[]>([]);
  const [loading, setLoading] = useState(true);
  const [showUpload, setShowUpload] = useState(false);
  const [storageUsed, setStorageUsed] = useState(0);
  const [filterTipo, setFilterTipo] = useState<TipoDocumento | ''>('');
  const [filterEntidad, setFilterEntidad] = useState<TipoEntidad | ''>('');

  const loadDocumentos = async () => {
    try {
      setLoading(true);
      const data = await documentoService.getAll();
      setDocumentos(data);
    } catch (error) {
      console.error('Error loading documentos:', error);
    } finally {
      setLoading(false);
    }
  };

  const loadStorageUsed = async () => {
    try {
      const used = await documentoService.getStorageUsed();
      setStorageUsed(used);
    } catch (error) {
      console.error('Error loading storage:', error);
    }
  };

  useEffect(() => {
    loadDocumentos();
    loadStorageUsed();
  }, []);

  const handleUploadSuccess = (documento: Documento) => {
    setDocumentos((prev) => [documento, ...prev]);
    setShowUpload(false);
    loadStorageUsed();
  };

  const handleDocumentDeleted = (id: number) => {
    setDocumentos((prev) => prev.filter((d) => d.id !== id));
    loadStorageUsed();
  };

  const handleDocumentUpdated = (updated: Documento) => {
    setDocumentos((prev) => prev.map((d) => (d.id === updated.id ? updated : d)));
  };

  const filteredDocumentos = documentos.filter((doc) => {
    if (filterTipo && doc.tipoDocumento !== filterTipo) return false;
    if (filterEntidad && doc.tipoEntidad !== filterEntidad) return false;
    return true;
  });

  const tipoDocumentoOptions: TipoDocumento[] = [
    'CONTRATO',
    'IDENTIFICACION',
    'COMPROBANTE_DOMICILIO',
    'COMPROBANTE_INGRESOS',
    'ESCRITURA',
    'RECIBO',
    'FACTURA',
    'FOTO',
    'PLANO',
    'AVALUO',
    'OTRO',
  ];

  const tipoEntidadOptions: TipoEntidad[] = ['PERSONA', 'PROPIEDAD', 'CONTRATO', 'PAGO', 'EMPRESA'];

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4">Documentos</Typography>
        <Button
          variant="contained"
          startIcon={<Add />}
          onClick={() => setShowUpload(!showUpload)}
        >
          {showUpload ? 'Cancelar' : 'Subir Documento'}
        </Button>
      </Box>

      <Grid container spacing={3}>
        {/* Stats Cards */}
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <FolderOpen color="primary" />
                <Box>
                  <Typography variant="h5">{documentos.length}</Typography>
                  <Typography variant="body2" color="text.secondary">
                    Total documentos
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <Storage color="primary" />
                <Box>
                  <Typography variant="h5">
                    {documentoService.formatFileSize(storageUsed)}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Almacenamiento usado
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        {/* Upload Form */}
        <Grid item xs={12}>
          <Collapse in={showUpload}>
            <Box sx={{ mb: 3 }}>
              <DocumentUpload
                tipoEntidad="EMPRESA"
                entidadId={1}
                onUploadSuccess={handleUploadSuccess}
                onCancel={() => setShowUpload(false)}
              />
            </Box>
          </Collapse>
        </Grid>

        {/* Filters */}
        <Grid item xs={12}>
          <Paper sx={{ p: 2, mb: 2 }}>
            <Grid container spacing={2}>
              <Grid item xs={12} sm={6} md={3}>
                <FormControl fullWidth size="small">
                  <InputLabel>Tipo de documento</InputLabel>
                  <Select
                    value={filterTipo}
                    label="Tipo de documento"
                    onChange={(e) => setFilterTipo(e.target.value as TipoDocumento | '')}
                  >
                    <MenuItem value="">Todos</MenuItem>
                    {tipoDocumentoOptions.map((tipo) => (
                      <MenuItem key={tipo} value={tipo}>
                        {documentoService.getTipoDocumentoLabel(tipo)}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={6} md={3}>
                <FormControl fullWidth size="small">
                  <InputLabel>Tipo de entidad</InputLabel>
                  <Select
                    value={filterEntidad}
                    label="Tipo de entidad"
                    onChange={(e) => setFilterEntidad(e.target.value as TipoEntidad | '')}
                  >
                    <MenuItem value="">Todos</MenuItem>
                    {tipoEntidadOptions.map((tipo) => (
                      <MenuItem key={tipo} value={tipo}>
                        {documentoService.getTipoEntidadLabel(tipo)}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>
            </Grid>
          </Paper>
        </Grid>

        {/* Document List */}
        <Grid item xs={12}>
          <DocumentList
            documentos={filteredDocumentos}
            onDocumentDeleted={handleDocumentDeleted}
            onDocumentUpdated={handleDocumentUpdated}
            loading={loading}
          />
        </Grid>
      </Grid>
    </Box>
  );
};

export default DocumentosPage;
