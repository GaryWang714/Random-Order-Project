import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { OrderService, Order } from '../services/order.service';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-orders',
  imports: [CommonModule, FormsModule],
  templateUrl: './orders.html',
  styleUrl: './orders.css'
})
export class OrdersComponent implements OnInit {

  orders: Order[] = [];
  newOrder: Order = { total: 0, status: 'PENDING' };
  errorMessage: string = '';

  constructor(
    private orderService: OrderService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(): void {
    this.orderService.getOrders().subscribe(data =>
      this.orders = data.map(o => ({ ...o, clientId: this.generateId() }))
    );
  }

  createOrder(): void {
    const total = Number(this.newOrder.total);
    if (total <= 0) {
      this.errorMessage = 'Total must be greater than zero';
      return;
    }

    const clientId = this.generateId();
    const payload: Order = { ...this.newOrder, total };
    const temp: Order = { clientId, ...payload };

    this.orders = [...this.orders, temp];
    this.newOrder = { total: 0, status: 'PENDING' };

    this.orderService.createOrder(payload).subscribe({
      next: (created) => {
        this.orders = this.orders.map(o => o.clientId === clientId ? { ...created, clientId } : o);
        this.errorMessage = '';
      },
      error: (err) => {
        this.orders = this.orders.filter(o => o.clientId !== clientId);
        if (err.error && typeof err.error === 'object') {
          this.errorMessage = Object.values(err.error).join(', ');
        } else {
          this.errorMessage = 'Something went wrong. Please try again.';
        }
      }
    });
  }

  deleteOrder(id: number): void {
    const backup = [...this.orders];
    this.orders = this.orders.filter(o => o.id !== id);

    this.orderService.deleteOrder(id).subscribe({
      error: () => {
        this.orders = backup;
      }
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  private generateId(): string {
    return Math.random().toString(36).substring(2) + Date.now().toString(36);
  }
}
