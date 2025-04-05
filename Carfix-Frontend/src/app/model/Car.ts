import {Model} from './Model';

export interface Car{
  id: string | null,
  govNumber: string,
  vinNumber: string,
  modelDto: Model | null
}
