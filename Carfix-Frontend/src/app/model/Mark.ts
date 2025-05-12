import {Model} from './Model';

export interface Mark{
  id: string | null,
  markName: string,
  models: Model[]
}
