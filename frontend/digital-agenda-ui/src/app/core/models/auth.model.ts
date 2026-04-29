/*
interfacce per definire come sono fatti i dati scambiati con il backend
*/
export interface LoginRequest {
    email: string;
    password: string;
}

export interface AuthResponse {
    token: string;
    id: number;
    email: string;
    firstName: string;
    lastName: string
    phoneNumber?: string;
    role: 'CUSTOMER' | 'PROFESSIONAL';
}

export interface User {
    id: number;
    email: string;
    firstName: string;
    lastName: string
    phoneNumber?: string;
    role: 'CUSTOMER' | 'PROFESSIONAL' | 'ADMIN';
}

