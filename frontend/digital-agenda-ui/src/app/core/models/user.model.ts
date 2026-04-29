export interface UserResponseDTO {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  jobTitle: string;
  phoneNumber?: string;
  vatRegistrationNumber?:string;
  remote?: boolean;
  onSite?: boolean;
  role: 'CUSTOMER' | 'PROFESSIONAL' | 'ADMIN';
}

export interface ProfessionalResponseDTO {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  jobTitle: string;
  remote: boolean;
  onSite: boolean;
  vatRegistrationNumber?: string;
  role: 'CUSTOMER' | 'PROFESSIONAL' | 'ADMIN';
  phoneNumber?: string;
  
}
