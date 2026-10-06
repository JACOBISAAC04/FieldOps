import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  Engineer,
  EngineerRequest,
  AvailableUser
} from '../models/engineer.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class EngineerService {

  private readonly apiUrl = environment.apiUrl + '/engineers';
  private readonly usersApiUrl = environment.apiUrl + '/users';

  constructor(private http: HttpClient) {}

  getAllEngineers(): Observable<Engineer[]> {
    return this.http.get<Engineer[]>(this.apiUrl);
  }

  getEngineerById(id: number): Observable<Engineer> {
    return this.http.get<Engineer>(`${this.apiUrl}/${id}`);
  }

  createEngineer(request: EngineerRequest): Observable<Engineer> {
    return this.http.post<Engineer>(this.apiUrl, request);
  }

  updateEngineer(id: number, request: EngineerRequest): Observable<Engineer> {
    return this.http.put<Engineer>(`${this.apiUrl}/${id}`, request);
  }

  getBySpecialization(specialization: string): Observable<Engineer[]> {
    return this.http.get<Engineer[]>(`${this.apiUrl}/specialization/${specialization}`);
  }

  getByLocation(location: string): Observable<Engineer[]> {
    return this.http.get<Engineer[]>(`${this.apiUrl}/location/${location}`);
  }

  getByAvailability(availability: string): Observable<Engineer[]> {
    return this.http.get<Engineer[]>(`${this.apiUrl}/availability/${availability}`);
  }

  getAvailableEngineers(): Observable<AvailableUser[]> {
    return this.http.get<AvailableUser[]>(`${this.usersApiUrl}/available-engineers`);
  }
}