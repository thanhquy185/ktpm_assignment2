// import React from "react";
// import { ShoppingCart, Home } from "lucide-react";

// interface HeaderProps {
//   cartItemCount: number;
//   onCartClick: () => void;
//   currentPage: string;
//   onNavigate: (page: string) => void;
// }

// export const Header: React.FC<HeaderProps> = ({
//   cartItemCount,
//   onCartClick,
//   currentPage,
//   onNavigate,
// }) => {
//   return (
//     <header className="bg-white shadow-md sticky top-0 z-50">
//       <div className="max-w-6xl mx-auto px-4 py-4 flex justify-between items-center">
//         <div className="flex items-center gap-8">
//           <h1
//             className="text-2xl font-bold text-blue-600 cursor-pointer"
//             onClick={() => onNavigate("products")}
//           >
//             ShopCart
//           </h1>
//           <nav className="hidden md:flex gap-6">
//             <button
//               onClick={() => onNavigate("products")}
//               className={`px-4 py-2 rounded-lg transition-colors ${
//                 currentPage === "products"
//                   ? "bg-blue-600 text-white"
//                   : "text-gray-700 hover:bg-gray-100"
//               }`}
//             >
//               <Home className="w-5 h-5 inline mr-2" />
//               Sản phẩm
//             </button>
//           </nav>
//         </div>
//         <div className="flex items-center gap-4">
//           <button
//             onClick={onCartClick}
//             className="relative p-2 text-gray-700 hover:text-blue-600 transition-colors"
//             data-testid="cart-icon"
//           >
//             <ShoppingCart className="w-6 h-6" />
//             {cartItemCount > 0 && (
//               <span
//                 className="absolute top-0 right-0 bg-red-500 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center"
//                 data-testid="cart-badge"
//               >
//                 {cartItemCount}
//               </span>
//             )}
//           </button>
//         </div>
//       </div>
//     </header>
//   );
// };
