import {User} from './User';

export interface Review{
  id: string | null,
  reviewContent: string,
  rating: number,
  reviewDate: Date,
  userDto: User | null
}
