// import { Component, OnInit } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { FormsModule } from '@angular/forms';
// import { OrderService, Order } from './services/order.service';
// //import { RouterOutlet } from '@angular/router';
//
// @Component({
//   selector: 'app-root',
//   //imports: [RouterOutlet],
//   imports: [CommonModule, FormsModule],
//   templateUrl: './app.html',
//   styleUrl: './app.css'
// })
// export class App implements OnInit {
//
//   orders: Order[] = [];
//   newOrder: Order = { total: 0, status: 'PENDING'};
//   //protected readonly title = signal('frontend');
//
//   constructor(private orderService: OrderService) {}
//
//   ngOnInit(): void {
//     this.loadOrders();
//   }
//
//   loadOrders(): void {
//     this.orderService.getOrders().subscribe(data => this.orders = data);
//   }
//
//   createOrder(): void {
//     const total = Number(this.newOrder.total);
//     //if(!this.newOrder.total || this.newOrder.total <= 0) return;
//     if(total <= 0) return;
//
//     const payload: Order = {
//       ...this.newOrder,
//       total
//     };
//
//     //this.orderService.createOrder(this.newOrder)
//     this.orderService.createOrder(payload).subscribe((created) => {
//       this.orders = [...this.orders, created];
//       this.newOrder = { total: 0, status: 'PENDING' };
//       //setTimeout(() => this.loadOrders(), 300);
//       //this.loadOrders();
//     });
//   }
//
//   deleteOrder(id: number): void {
//     this.orderService.deleteOrder(id).subscribe(() => {
//       this.orders = this.orders.filter(o => o.id !== id);
//       });
//     //this.orderService.deleteOrder(id).subscribe(() => this.loadOrders());
//   }
// }

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { OrderService, Order } from './services/order.service';

@Component({
  selector: 'app-root',
  imports: [CommonModule, FormsModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {

  orders: Order[] = [];
  newOrder: Order = { total: 0, status: 'PENDING' };
  errorMessage: string = '';

  constructor(private orderService: OrderService) {}

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(): void {
    this.orderService.getOrders().subscribe(data => this.orders = data.map(o => ({ ...o, clientId:
      crypto.randomUUID() }))
    );
  }

  createOrder(): void {
    const total = Number(this.newOrder.total);
//     if (total <= 0) return;
    if(total <= 0) {
      this.errorMessage = 'Total must be greater than zero';
      return;
    }

    const clientId = crypto.randomUUID();

    const payload: Order = { ...this.newOrder, total };
    const temp: Order = { clientId, ...payload };

    this.orders = [...this.orders, temp];
    this.newOrder = { total: 0, status: 'PENDING' };

    this.orderService.createOrder(payload).subscribe({
      next: (created) => {
        this.orders = this.orders.map(o => o.clientId === clientId ? { ...created, clientId } : o);
        this.errorMessage = '';
      },
//       error: () => {
//         this.orders = this.orders.filter(o => o.clientId !== clientId);
//       }
      error: (err) => {
        this.orders = this.orders.filter(o => o.clientId !== clientId);
        if(err.error && typeof err.error === 'object') {
          this.errorMessage = Object.values(err.error).join(',');
        }
        else {
          this.errorMessage = 'Something went wrong. Please try again.';
        }
//         const errors = err.error;
//         this.errorMessage = Object.values(errors).join(', ');
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

}
