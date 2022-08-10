import { ObjectId } from 'mongodb';
export default class ChattingData {
  constructor(
    public user_id: string,
    public message: string,
    public rasberrypi_address: string,
    public timestamp: Date,
    public _id?: ObjectId
  ) {}
}
