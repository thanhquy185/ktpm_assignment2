// import { describe, it, expect, vi, beforeEach } from 'vitest';
// import { render, screen, waitFor } from '@testing-library/react';
// import { BrowserRouter } from 'react-router-dom';
// import { AuthProvider } from '../../src/contexts/AuthContext';
// import { OrderPage } from '../../src/pages/OrderPage';
// import { orderService } from '../../src/services/orderService';

// evi.mock('../../src/services/orderService', () => ({
//   orderService: {
//     getOrdersByUserId: vi.fn(),
//   },
// }));

// describe('OrderPage', () => {
//   beforeEach(() => {
//     localStorage.clear();
//     localStorage.setItem('userId', 'user-test-001');
//     localStorage.setItem('username', 'tester');
//   });

//   it('renders order page header and shows no orders state', async () => {
//     (orderService.getOrdersByUserId as unknown as vi.Mock).mockResolvedValue([]);

//     render(
//       <BrowserRouter>
//         <AuthProvider>
//           <OrderPage />
//         </AuthProvider>
//       </BrowserRouter>,
//     );

//     expect(screen.getByTestId('orders-title')).toHaveTextContent('Đơn hàng');
//     await waitFor(() => expect(screen.getByText('Chưa có đơn hàng nào')).toBeInTheDocument());
//   });

//   it('renders a list of orders when the backend returns orders', async () => {
//     const orders = [
//       {
//         id: 'order-1',
//         status: 'PENDING',
//         totalPrice: 490000,
//         createdAt: new Date().toISOString(),
//         shippingAddress: '123 Đường ABC',
//         orderItems: [{ productId: '1', quantity: 2, price: 120000 }],
//       },
//     ];

//     (orderService.getOrdersByUserId as unknown as vi.Mock).mockResolvedValue(orders);

//     render(
//       <BrowserRouter>
//         <AuthProvider>
//           <OrderPage />
//         </AuthProvider>
//       </BrowserRouter>,
//     );

//     await waitFor(() => expect(screen.getByText('order-1')).toBeInTheDocument());
//     expect(screen.getByText('PENDING')).toBeInTheDocument();
//     expect(screen.getByText('123 Đường ABC')).toBeInTheDocument();
//   });
// });
