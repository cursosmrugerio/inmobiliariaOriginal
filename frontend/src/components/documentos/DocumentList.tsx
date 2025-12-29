import React, { useState } from 'react';
import {
  Box,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  IconButton,
  Tooltip,
  Typography,
  Chip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
} from '@mui/material';
import {
  Download,
  Delete,
  Edit,
  Description,
  Image,
  PictureAsPdf,
  InsertDriveFile,
} from '@mui/icons-material';
import documentoService, { Documento, UpdateDocumentoRequest } from '../../services/documentoService';

interface DocumentListProps {
  documentos: Documento[];
  onDocumentDeleted?: (id: number) => void;
  onDocumentUpdated?: (documento: Documento) => void;
  loading?: boolean;
}

const DocumentList: React.FC<DocumentListProps> = ({
  documentos,
  onDocumentDeleted,
  onDocumentUpdated,
  loading: _loading,
}) => {
  const [editDialog, setEditDialog] = useState<{ open: boolean; documento: Documento | null }>({
    open: false,
    documento: null,
  });
  const [deleteDialog, setDeleteDialog] = useState<{ open: boolean; id: number | null }>({
    open: false,
    id: null,
  });
  const [editNombre, setEditNombre] = useState('');
  const [editDescripcion, setEditDescripcion] = useState('');

  const getFileIcon = (contentType: string) => {
    if (contentType?.startsWith('image/')) return <Image color="primary" />;
    if (contentType === 'application/pdf') return <PictureAsPdf color="error" />;
    if (contentType?.includes('document') || contentType?.includes('text')) return <Description color="info" />;
    return <InsertDriveFile />;
  };

  const handleDownload = async (documento: Documento) => {
    try {
      const blob = await documentoService.download(documento.id);
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = documento.nombreOriginal;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
    } catch (error) {
      console.error('Error downloading document:', error);
    }
  };

  const handleEditOpen = (documento: Documento) => {
    setEditNombre(documento.nombre);
    setEditDescripcion(documento.descripcion || '');
    setEditDialog({ open: true, documento });
  };

  const handleEditSave = async () => {
    if (!editDialog.documento) return;

    try {
      const request: UpdateDocumentoRequest = {
        nombre: editNombre,
        descripcion: editDescripcion,
      };
      const updated = await documentoService.update(editDialog.documento.id, request);
      onDocumentUpdated?.(updated);
      setEditDialog({ open: false, documento: null });
    } catch (error) {
      console.error('Error updating document:', error);
    }
  };

  const handleDelete = async () => {
    if (!deleteDialog.id) return;

    try {
      await documentoService.delete(deleteDialog.id);
      onDocumentDeleted?.(deleteDialog.id);
      setDeleteDialog({ open: false, id: null });
    } catch (error) {
      console.error('Error deleting document:', error);
    }
  };

  if (documentos.length === 0) {
    return (
      <Paper sx={{ p: 3, textAlign: 'center' }}>
        <Typography color="text.secondary">No hay documentos</Typography>
      </Paper>
    );
  }

  return (
    <>
      <TableContainer component={Paper}>
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell>Documento</TableCell>
              <TableCell>Tipo</TableCell>
              <TableCell>Tamano</TableCell>
              <TableCell>Fecha</TableCell>
              <TableCell align="right">Acciones</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {documentos.map((doc) => (
              <TableRow key={doc.id} hover>
                <TableCell>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    {getFileIcon(doc.contentType)}
                    <Box>
                      <Typography variant="body2">{doc.nombre}</Typography>
                      <Typography variant="caption" color="text.secondary">
                        {doc.nombreOriginal}
                      </Typography>
                    </Box>
                  </Box>
                </TableCell>
                <TableCell>
                  <Chip
                    label={documentoService.getTipoDocumentoLabel(doc.tipoDocumento)}
                    size="small"
                    variant="outlined"
                  />
                </TableCell>
                <TableCell>{documentoService.formatFileSize(doc.tamano)}</TableCell>
                <TableCell>
                  {new Date(doc.fechaCreacion).toLocaleDateString()}
                </TableCell>
                <TableCell align="right">
                  <Tooltip title="Descargar">
                    <IconButton size="small" onClick={() => handleDownload(doc)}>
                      <Download />
                    </IconButton>
                  </Tooltip>
                  <Tooltip title="Editar">
                    <IconButton size="small" onClick={() => handleEditOpen(doc)}>
                      <Edit />
                    </IconButton>
                  </Tooltip>
                  <Tooltip title="Eliminar">
                    <IconButton
                      size="small"
                      color="error"
                      onClick={() => setDeleteDialog({ open: true, id: doc.id })}
                    >
                      <Delete />
                    </IconButton>
                  </Tooltip>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      {/* Edit Dialog */}
      <Dialog open={editDialog.open} onClose={() => setEditDialog({ open: false, documento: null })}>
        <DialogTitle>Editar Documento</DialogTitle>
        <DialogContent>
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1, minWidth: 300 }}>
            <TextField
              label="Nombre"
              value={editNombre}
              onChange={(e) => setEditNombre(e.target.value)}
              fullWidth
            />
            <TextField
              label="Descripcion"
              value={editDescripcion}
              onChange={(e) => setEditDescripcion(e.target.value)}
              multiline
              rows={2}
              fullWidth
            />
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setEditDialog({ open: false, documento: null })}>Cancelar</Button>
          <Button onClick={handleEditSave} variant="contained">Guardar</Button>
        </DialogActions>
      </Dialog>

      {/* Delete Dialog */}
      <Dialog open={deleteDialog.open} onClose={() => setDeleteDialog({ open: false, id: null })}>
        <DialogTitle>Confirmar Eliminacion</DialogTitle>
        <DialogContent>
          <Typography>Esta seguro de eliminar este documento? Esta accion no se puede deshacer.</Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDeleteDialog({ open: false, id: null })}>Cancelar</Button>
          <Button onClick={handleDelete} color="error" variant="contained">Eliminar</Button>
        </DialogActions>
      </Dialog>
    </>
  );
};

export default DocumentList;
