import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Order {
  id?: number;
  clientId?: string;
  total: number;
  status: string;
  }

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  private apiUrl = `${(window as any).__env?.apiUrl || 'http://localhost:8080'}/order`;
  //private apiUrl = 'http://localhost:8080/order';

  constructor(private http: HttpClient) {}

  getOrders(): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.apiUrl}/all`);
  }

  createOrder(order: Order): Observable<Order> {
    return this.http.post<Order>(`${this.apiUrl}/add`, order);
  }

  deleteOrder(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete/${id}`);
  }
}
