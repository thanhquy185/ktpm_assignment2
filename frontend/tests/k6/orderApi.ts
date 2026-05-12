import http from "k6/http";
import { check, sleep } from "k6";
import type { Options } from "k6/options";
import { OrderCreateRequest } from "../../src/types/order";

export const options: Options = {
  stages: [
    // Load Test
    { duration: "1m", target: 50 },
    { duration: "1m", target: 100 },
    { duration: "1m", target: 200 },
    { duration: "1m", target: 0 },
    // // Stress Test
    // { duration: "1m", target: 500 },
    // { duration: "1m", target: 1000 },
    // { duration: "1m", target: 2000 },
    // { duration: "1m", target: 0 },
  ],
  thresholds: {
    http_req_failed: ["rate<0.01"], // <1% lỗi
    http_req_duration: ["p(95)<500"], // 95% < 500ms
  },
};

export default function (): void {
  const url: string = "http://localhost:8080/api/orders";
  const payload: OrderCreateRequest = {
    userId: "1354d8fd-0380-4347-99d1-6450649b10d6",
    couponId: null,
    shippingAddress: "123 Nguyễn Huệ",
    shippingMethod: "Tiêu chuẩn",
    shippingFee: 20000,
    paymentMethod: "Thanh toán khi nhận hàng",
    orderItems: [
      {
        productId: "0af432c6-2aed-4429-8155-d2d1b17fcd03",
        quantity: 2,
        price: 20000,
      },
    ],
  };
  const params = {
    headers: {
      "Content-Type": "application/json",
      Authorization:
        "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjdXN0b21lciIsImV4cCI6MTc3ODU4NDM2NywiaWF0IjoxNzc4NDk3OTY3LCJ1c2VyIjp7ImlkIjoiM2VkZWJhMjAtMmJlNC00NzI1LTkxOTctOWI1NWQ4MjQ5NDJiIiwidXNlcm5hbWUiOiJjdXN0b21lciJ9fQ.peSmjAYewgytrVmgT1_SipqenLZA2YwXit7p_K8NgeH3bYwMW-3-CDfwF1uI0Pcln6lhNAx_w7wRPHVV6Zy-MQ",
    },
  };
  const res = http.post(url, JSON.stringify(payload), params);

  check(res, {
    "debug status": (r) => {
      console.log("STATUS:", r.status);
      console.log("BODY:", r.body);
      return r.status === 201;
    },
  });
  sleep(1);
}
