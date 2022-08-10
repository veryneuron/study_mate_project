import * as mongoDB from 'mongodb';

export const collections: { chattingData?: mongoDB.Collection } = {};

export async function connectToDatabase() {
  const client: mongoDB.MongoClient = new mongoDB.MongoClient(
    `mongodb://${process.env.POSTGRES_ID}:${process.env.POSTGRES_PW}@localhost:27017`
  );

  await client.connect();

  const db: mongoDB.Db = client.db('study_mate');

  const chattingDataCollection: mongoDB.Collection =
    db.collection('chatting_data');

  collections.chattingData = chattingDataCollection;

  console.log(
    `Successfully connected to database: ${db.databaseName} and collection: ${chattingDataCollection.collectionName}`
  );
}
