import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AccountService {
  private apiUrl = `${environment.apiUrl}/accounts`;

  constructor(private http: HttpClient) {}

  getAllAccounts(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  getAccountById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  createAccount(accountData: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, accountData);
  }

  updateAccount(id: number, accountData: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, accountData);
  }

  toggleAccountActive(id: number): Observable<any> {
    return this.http.patch<any>(`${this.apiUrl}/${id}/toggle-active`, {});
  }

  deleteAccount(id: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${id}`);
  }

  getAccountsByHRManager(hrManagerId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/hr-manager/${hrManagerId}`);
  }

  getAccountsByClient(clientId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/client/${clientId}`);
  }
}
