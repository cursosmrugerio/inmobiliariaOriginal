import api from './api';

export const authService = {
  async login(email: string, password: string) {
    const response = await api.post('/api/auth/login', { email, password });
    return response.data;
  },

  async getCurrentUser() {
    const response = await api.get('/api/auth/me');
    return response.data;
  },

  async register(data: {
    email: string;
    password: string;
    nombre: string;
    apellido: string;
    empresaId: number;
  }) {
    const response = await api.post('/api/auth/register', data);
    return response.data;
  },
};
