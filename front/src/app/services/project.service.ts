import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProjectService {
  private apiUrl = `${environment.apiUrl}/projects`;

  constructor(private http: HttpClient) {}

  getAllProjects(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  getProjectById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  createProject(projectData: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, projectData);
  }

  updateProject(id: number, projectData: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, projectData);
  }

  toggleProjectActive(id: number): Observable<any> {
    return this.http.patch<any>(`${this.apiUrl}/${id}/toggle-active`, {});
  }

  deleteProject(id: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${id}`);
  }

  getProjectsByManager(managerId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/manager/${managerId}`);
  }

  getProjectsByClient(clientId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/client/${clientId}`);
  }

  getProjectsByEmployee(employeeId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/employee/${employeeId}`);
  }

  getProjectsByStatus(status: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/status/${status}`);
  }

  getActiveProjects(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/active`);
  }

  addEmployeeToProject(projectId: number, employeeId: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/${projectId}/employees/${employeeId}`, {});
  }

  removeEmployeeFromProject(projectId: number, employeeId: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${projectId}/employees/${employeeId}`);
  }
}
