export interface ValidateCartItemUnitTestResponse {
  error?: string;
  message?: string;
}

export interface CalculateCartTotalUnitTestResponse {
  error?: string;
  message?: string;
  discount?: number;
  newTotalPriceBeforeDiscount?: number;
  newTotalPriceAfterDiscount?: number;
}
