package com.df.thorntail.db;

import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class StandaloneDb implements Closeable {

	private MongoClient mClient;
	private MongoDatabase database;

	public StandaloneDb(String connectionString) {

		mClient = MongoClients.create(
				MongoClientSettings
					.builder()
					.applyConnectionString(new ConnectionString(connectionString))
					.build());
	}
	
	public StandaloneDb(String usr, String pwd, String dbname, String host, int port) {
		
		CodecRegistry codecRegistry = fromRegistries(
				getDefaultCodecRegistry(),
				fromProviders(PojoCodecProvider.builder().register("com.df.thorntail.db.pojos").build()));
		
		MongoCredential credential = MongoCredential.createCredential(usr, dbname, pwd.toCharArray());
		
		MongoClientSettings settings = MongoClientSettings.builder()
	            .credential(credential)
	            .codecRegistry(codecRegistry)
//	            .applyToSslSettings(builder -> builder.enabled(true))
	            .applyToClusterSettings(builder -> 
	                builder.hosts(Arrays.asList(new ServerAddress(host, port))))
	            .build();
		
		mClient = MongoClients.create(settings);
		
		database = mClient.getDatabase(dbname);
	}
	
	public <T> MongoCollection<T> getCollection(Class<T> collectionClass) {
		DbCollection dbCollectionSpec = collectionClass.getAnnotation(DbCollection.class);
		return database.getCollection(dbCollectionSpec.value(), collectionClass);
	}
	
	@Override
	public void close() throws IOException {
		if (database != null) database = null;
		if (mClient != null) mClient.close();
	}
	
	public boolean isConnected() {
		return database != null && database.getName() != null;
	}

	public boolean init() {
		return true;
	}
}
