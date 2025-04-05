import {Car} from './Car';
import {User} from './User';
import Big from 'big.js';
import {Status} from './Status';
import {Work} from './Work';

export interface Order{
    id: string | null,
    carDto: Car,
    userDto: User,
    orderDate: Date,
    price: number,
    status: Status,
    description: string,
    works: Work[]
}
