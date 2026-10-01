export interface Engineer {
  id: number;
  userId: number;
  name: string;
  email: string;
  specialization: string;
  location: string;
  availability: string;
}

export interface EngineerRequest {
  userId: number;
  specialization: string;
  location: string;
  availability: string;
}

export interface AvailableUser {
  id: number;
  name: string;
  email: string;
}