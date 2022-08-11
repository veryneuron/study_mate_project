import { Collection, MongoClient } from 'mongodb';

export const collections: { chattingData?: Collection } = {};

export async function connectToDatabase() {
  const client = await MongoClient.connect(
    `mongodb://${process.env.POSTGRES_ID}:${process.env.POSTGRES_PW}@localhost:27017`
  );
  const db = client.db('study_mate');
  collections.chattingData = db.collection('chatting_data');

  console.log(
    `Successfully connected to database: ${db.databaseName} and collection: ${collections.chattingData.collectionName}`
  );
}
