import { ObjectId } from 'mongodb';
export default class ChattingData {
  constructor(
    public userId: string,
    public message: string,
    public rasberrypiAddress: string,
    public timestamp: Date,
    public type: string,
    public _id?: ObjectId
  ) {}
}
