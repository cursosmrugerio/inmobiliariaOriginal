import React, { useState, useRef } from 'react';
import {
  Box,
  Button,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Typography,
  Paper,
  LinearProgress,
  Alert,
  IconButton,
} from '@mui/material';
import { CloudUpload, Close, AttachFile } from '@mui/icons-material';
import documentoService, {
  TipoDocumento,
  TipoEntidad,
  CreateDocumentoRequest,
  Documento,
} from '../../services/documentoService';

interface DocumentUploadProps {
  tipoEntidad: TipoEntidad;
  entidadId: number;
  onUploadSuccess?: (documento: Documento) => void;
  onCancel?: () => void;
  allowedTypes?: TipoDocumento[];
}

const DocumentUpload: React.FC<DocumentUploadProps> = ({
  tipoEntidad,
  entidadId,
  onUploadSuccess,
  onCancel,
  allowedTypes,
}) => {
  const [file, setFile] = useState<File | null>(null);
  const [nombre, setNombre] = useState('');
  const [tipoDocumento, setTipoDocumento] = useState<TipoDocumento>('OTRO');
  const [descripcion, setDescripcion] = useState('');
  const [uploading, setUploading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const allTipoDocumento: TipoDocumento[] = [
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

  const tiposDisponibles = allowedTypes || allTipoDocumento;

  const handleFileSelect = (event: React.ChangeEvent<HTMLInputElement>) => {
    const selectedFile = event.target.files?.[0];
    if (selectedFile) {
      setFile(selectedFile);
      if (!nombre) {
        setNombre(selectedFile.name.split('.')[0]);
      }
      setError(null);
    }
  };

  const handleUpload = async () => {
    if (!file) {
      setError('Seleccione un archivo');
      return;
    }
    if (!nombre.trim()) {
      setError('Ingrese un nombre para el documento');
      return;
    }

    setUploading(true);
    setError(null);

    try {
      const request: CreateDocumentoRequest = {
        nombre: nombre.trim(),
        tipoDocumento,
        tipoEntidad,
        entidadId,
        descripcion: descripcion.trim() || undefined,
      };

      const documento = await documentoService.upload(file, request);
      onUploadSuccess?.(documento);

      // Reset form
      setFile(null);
      setNombre('');
      setDescripcion('');
      setTipoDocumento('OTRO');
      if (fileInputRef.current) {
        fileInputRef.current.value = '';
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Error al subir el documento');
    } finally {
      setUploading(false);
    }
  };

  return (
    <Paper sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
        <Typography variant="h6">Subir Documento</Typography>
        {onCancel && (
          <IconButton onClick={onCancel} size="small">
            <Close />
          </IconButton>
        )}
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
          {error}
        </Alert>
      )}

      <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
        <Box>
          <input
            type="file"
            ref={fileInputRef}
            onChange={handleFileSelect}
            style={{ display: 'none' }}
            id="file-upload"
          />
          <label htmlFor="file-upload">
            <Button
              variant="outlined"
              component="span"
              startIcon={<AttachFile />}
              fullWidth
            >
              {file ? file.name : 'Seleccionar archivo'}
            </Button>
          </label>
          {file && (
            <Typography variant="caption" color="text.secondary" sx={{ mt: 0.5, display: 'block' }}>
              {documentoService.formatFileSize(file.size)}
            </Typography>
          )}
        </Box>

        <TextField
          label="Nombre del documento"
          value={nombre}
          onChange={(e) => setNombre(e.target.value)}
          required
          fullWidth
        />

        <FormControl fullWidth>
          <InputLabel>Tipo de documento</InputLabel>
          <Select
            value={tipoDocumento}
            label="Tipo de documento"
            onChange={(e) => setTipoDocumento(e.target.value as TipoDocumento)}
          >
            {tiposDisponibles.map((tipo) => (
              <MenuItem key={tipo} value={tipo}>
                {documentoService.getTipoDocumentoLabel(tipo)}
              </MenuItem>
            ))}
          </Select>
        </FormControl>

        <TextField
          label="Descripcion (opcional)"
          value={descripcion}
          onChange={(e) => setDescripcion(e.target.value)}
          multiline
          rows={2}
          fullWidth
        />

        {uploading && <LinearProgress />}

        <Button
          variant="contained"
          onClick={handleUpload}
          disabled={uploading || !file}
          startIcon={<CloudUpload />}
        >
          {uploading ? 'Subiendo...' : 'Subir documento'}
        </Button>
      </Box>
    </Paper>
  );
};

export default DocumentUpload;
