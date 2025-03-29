
import Big from 'big.js';
import {ZonedDateTime} from 'js-joda';

export interface WorkPrice{
  id: string;
  price: Big;
  date: ZonedDateTime;
}
