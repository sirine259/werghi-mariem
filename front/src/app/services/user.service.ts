import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) {}

  getAllUsers(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  getUserById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  createUser(userData: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, userData);
  }

  updateUser(id: number, userData: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, userData);
  }

  toggleUserActive(id: number): Observable<any> {
    return this.http.patch<any>(`${this.apiUrl}/${id}/toggle-active`, {});
  }

  // HR Manager specific methods
  getAllEmployees(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/employees`);
  }

  getAvailableEmployees(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/employees/available`);
  }

  // Client specific methods
  getAllClients(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/clients`);
  }

  getFinalClients(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/clients/final`);
  }

  getIntermediaryClients(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/clients/intermediary`);
  }

  // HR Manager methods
  getHRManagers(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/hr-managers`);
  }
}
