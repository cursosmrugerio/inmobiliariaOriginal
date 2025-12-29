export type RolUsuario = 'ADMINISTRADOR' | 'AGENTE';

export interface Usuario {
  id: number;
  email: string;
  nombre: string;
  apellido: string;
  rol: RolUsuario;
  activo: boolean;
  empresaId: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateUsuarioRequest {
  email: string;
  password: string;
  nombre: string;
  apellido: string;
  rol: RolUsuario;
}

export interface UpdateUsuarioRequest {
  email?: string;
  password?: string;
  nombre?: string;
  apellido?: string;
  rol?: RolUsuario;
  activo?: boolean;
}

export interface ChangeRolRequest {
  rol: RolUsuario;
}
