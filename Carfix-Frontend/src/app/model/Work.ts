
import Big from 'big.js';

export interface Work{
  id: string; // UUID
  name: string; // String
  description: string; // String
  imageUrl: string; // String
  workPrice: Big; // Вложенный объект WorkPriceDto
}
