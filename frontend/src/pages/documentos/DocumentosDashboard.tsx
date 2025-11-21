import React, { useState, useEffect, useRef } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  IconButton,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Alert,
  Chip,
  Tooltip,
  Grid,
} from '@mui/material';
import {
  CloudUpload as UploadIcon,
  Download as DownloadIcon,
  Delete as DeleteIcon,
  Edit as EditIcon,
  Refresh as RefreshIcon,
  Description as DocIcon,
  Image as ImageIcon,
  PictureAsPdf as PdfIcon,
} from '@mui/icons-material';
import {
  documentoService,
  Documento,
  TipoDocumento,
  TipoEntidad,
  UpdateDocumentoRequest,
} from '../../services/documentoService';

const DocumentosDashboard: React.FC = () => {
  const [documentos, setDocumentos] = useState<Documento[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [uploadDialogOpen, setUploadDialogOpen] = useState(false);
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [selectedDoc, setSelectedDoc] = useState<Documento | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const [uploadData, setUploadData] = useState({
    nombre: '',
    tipoDocumento: 'OTRO' as TipoDocumento,
    tipoEntidad: 'PERSONA' as TipoEntidad,
    entidadId: '',
    descripcion: '',
    file: null as File | null,
  });

  const [editData, setEditData] = useState<UpdateDocumentoRequest>({
    nombre: '',
    tipoDocumento: 'OTRO',
    descripcion: '',
    activo: true,
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const docs = await documentoService.getAll();
      setDocumentos(docs);
      setError(null);
    } catch (err) {
      setError('Error al cargar documentos');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleUpload = async () => {
    if (!uploadData.file || !uploadData.nombre || !uploadData.entidadId) {
      setError('Complete todos los campos requeridos');
      return;
    }

    try {
      await documentoService.upload(
        uploadData.file,
        uploadData.nombre,
        uploadData.tipoDocumento,
        uploadData.tipoEntidad,
        parseInt(uploadData.entidadId),
        uploadData.descripcion
      );
      setUploadDialogOpen(false);
      resetUploadForm();
      loadData();
    } catch (err) {
      setError('Error al subir documento');
    }
  };

  const handleEdit = async () => {
    if (!selectedDoc) return;

    try {
      await documentoService.update(selectedDoc.id, editData);
      setEditDialogOpen(false);
      loadData();
    } catch (err) {
      setError('Error al actualizar documento');
    }
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('¿Está seguro de eliminar este documento?')) return;

    try {
      await documentoService.delete(id);
      loadData();
    } catch (err) {
      setError('Error al eliminar documento');
    }
  };

  const handleDownload = async (doc: Documento) => {
    try {
      await documentoService.download(doc.id, doc.nombreOriginal || doc.nombre);
    } catch (err) {
      setError('Error al descargar documento');
    }
  };

  const openEditDialog = (doc: Documento) => {
    setSelectedDoc(doc);
    setEditData({
      nombre: doc.nombre,
      tipoDocumento: doc.tipoDocumento,
      descripcion: doc.descripcion || '',
      activo: doc.activo,
    });
    setEditDialogOpen(true);
  };

  const resetUploadForm = () => {
    setUploadData({
      nombre: '',
      tipoDocumento: 'OTRO',
      tipoEntidad: 'PERSONA',
      entidadId: '',
      descripcion: '',
      file: null,
    });
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
  };

  const getFileIcon = (tipoMime?: string) => {
    if (!tipoMime) return <DocIcon />;
    if (tipoMime.startsWith('image/')) return <ImageIcon color="primary" />;
    if (tipoMime === 'application/pdf') return <PdfIcon color="error" />;
    return <DocIcon color="action" />;
  };

  const getTipoDocumentoLabel = (tipo: TipoDocumento): string => {
    const labels: Record<TipoDocumento, string> = {
      CONTRATO: 'Contrato',
      IDENTIFICACION: 'Identificación',
      COMPROBANTE_DOMICILIO: 'Comprobante Domicilio',
      COMPROBANTE_PAGO: 'Comprobante Pago',
      AVALUO: 'Avalúo',
      ESCRITURA: 'Escritura',
      ACTA_CONSTITUTIVA: 'Acta Constitutiva',
      PODER_NOTARIAL: 'Poder Notarial',
      OTRO: 'Otro',
    };
    return labels[tipo] || tipo;
  };

  const formatFileSize = (bytes?: number): string => {
    if (!bytes) return '-';
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
    return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
  };

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4">Documentos</Typography>
        <Box>
          <Button startIcon={<RefreshIcon />} onClick={loadData} sx={{ mr: 1 }}>
            Actualizar
          </Button>
          <Button
            variant="contained"
            startIcon={<UploadIcon />}
            onClick={() => setUploadDialogOpen(true)}
          >
            Subir Documento
          </Button>
        </Box>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
          {error}
        </Alert>
      )}

      <Card>
        <CardContent>
          <TableContainer component={Paper}>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Tipo</TableCell>
                  <TableCell>Nombre</TableCell>
                  <TableCell>Tipo Documento</TableCell>
                  <TableCell>Entidad</TableCell>
                  <TableCell>Tamaño</TableCell>
                  <TableCell>Estado</TableCell>
                  <TableCell>Fecha</TableCell>
                  <TableCell>Acciones</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {documentos.map((doc) => (
                  <TableRow key={doc.id}>
                    <TableCell>{getFileIcon(doc.tipoMime)}</TableCell>
                    <TableCell>
                      <Typography variant="body2">{doc.nombre}</Typography>
                      {doc.nombreOriginal && (
                        <Typography variant="caption" color="textSecondary">
                          {doc.nombreOriginal}
                        </Typography>
                      )}
                    </TableCell>
                    <TableCell>{getTipoDocumentoLabel(doc.tipoDocumento)}</TableCell>
                    <TableCell>
                      <Chip
                        label={`${doc.tipoEntidad} #${doc.entidadId}`}
                        size="small"
                        variant="outlined"
                      />
                    </TableCell>
                    <TableCell>{formatFileSize(doc.tamanio)}</TableCell>
                    <TableCell>
                      <Chip
                        label={doc.activo ? 'Activo' : 'Inactivo'}
                        color={doc.activo ? 'success' : 'default'}
                        size="small"
                      />
                    </TableCell>
                    <TableCell>
                      {new Date(doc.fechaCreacion).toLocaleDateString()}
                    </TableCell>
                    <TableCell>
                      <Tooltip title="Descargar">
                        <IconButton size="small" onClick={() => handleDownload(doc)}>
                          <DownloadIcon />
                        </IconButton>
                      </Tooltip>
                      <Tooltip title="Editar">
                        <IconButton size="small" onClick={() => openEditDialog(doc)}>
                          <EditIcon />
                        </IconButton>
                      </Tooltip>
                      <Tooltip title="Eliminar">
                        <IconButton size="small" onClick={() => handleDelete(doc.id)}>
                          <DeleteIcon />
                        </IconButton>
                      </Tooltip>
                    </TableCell>
                  </TableRow>
                ))}
                {documentos.length === 0 && (
                  <TableRow>
                    <TableCell colSpan={8} align="center">
                      No hay documentos
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </TableContainer>
        </CardContent>
      </Card>

      {/* Upload Dialog */}
      <Dialog open={uploadDialogOpen} onClose={() => setUploadDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Subir Documento</DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12}>
              <input
                type="file"
                ref={fileInputRef}
                onChange={(e) => {
                  const file = e.target.files?.[0] || null;
                  setUploadData({ ...uploadData, file });
                }}
                style={{ width: '100%' }}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Nombre"
                value={uploadData.nombre}
                onChange={(e) => setUploadData({ ...uploadData, nombre: e.target.value })}
                required
              />
            </Grid>
            <Grid item xs={6}>
              <FormControl fullWidth>
                <InputLabel>Tipo Documento</InputLabel>
                <Select
                  value={uploadData.tipoDocumento}
                  label="Tipo Documento"
                  onChange={(e) => setUploadData({ ...uploadData, tipoDocumento: e.target.value as TipoDocumento })}
                >
                  <MenuItem value="CONTRATO">Contrato</MenuItem>
                  <MenuItem value="IDENTIFICACION">Identificación</MenuItem>
                  <MenuItem value="COMPROBANTE_DOMICILIO">Comprobante Domicilio</MenuItem>
                  <MenuItem value="COMPROBANTE_PAGO">Comprobante Pago</MenuItem>
                  <MenuItem value="AVALUO">Avalúo</MenuItem>
                  <MenuItem value="ESCRITURA">Escritura</MenuItem>
                  <MenuItem value="ACTA_CONSTITUTIVA">Acta Constitutiva</MenuItem>
                  <MenuItem value="PODER_NOTARIAL">Poder Notarial</MenuItem>
                  <MenuItem value="OTRO">Otro</MenuItem>
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={6}>
              <FormControl fullWidth>
                <InputLabel>Tipo Entidad</InputLabel>
                <Select
                  value={uploadData.tipoEntidad}
                  label="Tipo Entidad"
                  onChange={(e) => setUploadData({ ...uploadData, tipoEntidad: e.target.value as TipoEntidad })}
                >
                  <MenuItem value="PERSONA">Persona</MenuItem>
                  <MenuItem value="PROPIEDAD">Propiedad</MenuItem>
                  <MenuItem value="CONTRATO">Contrato</MenuItem>
                  <MenuItem value="PAGO">Pago</MenuItem>
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="ID Entidad"
                type="number"
                value={uploadData.entidadId}
                onChange={(e) => setUploadData({ ...uploadData, entidadId: e.target.value })}
                required
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Descripción"
                multiline
                rows={2}
                value={uploadData.descripcion}
                onChange={(e) => setUploadData({ ...uploadData, descripcion: e.target.value })}
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => { setUploadDialogOpen(false); resetUploadForm(); }}>Cancelar</Button>
          <Button onClick={handleUpload} variant="contained">Subir</Button>
        </DialogActions>
      </Dialog>

      {/* Edit Dialog */}
      <Dialog open={editDialogOpen} onClose={() => setEditDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Editar Documento</DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Nombre"
                value={editData.nombre}
                onChange={(e) => setEditData({ ...editData, nombre: e.target.value })}
              />
            </Grid>
            <Grid item xs={12}>
              <FormControl fullWidth>
                <InputLabel>Tipo Documento</InputLabel>
                <Select
                  value={editData.tipoDocumento}
                  label="Tipo Documento"
                  onChange={(e) => setEditData({ ...editData, tipoDocumento: e.target.value as TipoDocumento })}
                >
                  <MenuItem value="CONTRATO">Contrato</MenuItem>
                  <MenuItem value="IDENTIFICACION">Identificación</MenuItem>
                  <MenuItem value="COMPROBANTE_DOMICILIO">Comprobante Domicilio</MenuItem>
                  <MenuItem value="COMPROBANTE_PAGO">Comprobante Pago</MenuItem>
                  <MenuItem value="AVALUO">Avalúo</MenuItem>
                  <MenuItem value="ESCRITURA">Escritura</MenuItem>
                  <MenuItem value="ACTA_CONSTITUTIVA">Acta Constitutiva</MenuItem>
                  <MenuItem value="PODER_NOTARIAL">Poder Notarial</MenuItem>
                  <MenuItem value="OTRO">Otro</MenuItem>
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Descripción"
                multiline
                rows={2}
                value={editData.descripcion}
                onChange={(e) => setEditData({ ...editData, descripcion: e.target.value })}
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setEditDialogOpen(false)}>Cancelar</Button>
          <Button onClick={handleEdit} variant="contained">Guardar</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default DocumentosDashboard;
