package com.df.thorntail.db;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.df.thorntail.util.SysProperties;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.event.CommandFailedEvent;
import com.mongodb.event.CommandListener;
import com.mongodb.event.CommandStartedEvent;
import com.mongodb.event.CommandSucceededEvent;

@Named
@Startup
@ApplicationScoped
@DependsOn("SysProperties")
public class MongoClientProvider implements CommandListener {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private MongoClient mongoClient = null;
	
	private MongoDatabase database = null;
	
	@Inject
	private SysProperties props;
	
	@Lock(LockType.READ)
	public MongoClient getMongoClient() {
		return mongoClient;
	}
	
	@Lock(LockType.READ)
	public MongoDatabase getMongoDatabase() {
		return database;
	}
	
	@Lock(LockType.READ)
	public <T> MongoCollection<T> getCollection(Class<T> collectionClass) {
		return database.getCollection(collectionClass.getSimpleName(), collectionClass);
	}
	
	@PostConstruct
	public void init() {
		try {
			logger.info("Defining codecs");
			CodecRegistry codecRegistry = fromRegistries(
					getDefaultCodecRegistry(),
					fromProviders(PojoCodecProvider.builder().register("com.df.thorntail.db.pojos").build()));
			
			logger.info("Creating credentials");
			MongoCredential credential = MongoCredential.createCredential(
					props.getDbUser(), 
					props.getDbName(), 
					props.getDbPwd().toCharArray());
			
			logger.info("Creating settings (Host= {}:{})", props.getDbHost(), props.getDbPort());
			MongoClientSettings settings = MongoClientSettings.builder()
		            .credential(credential)
		            .codecRegistry(codecRegistry)
		            .addCommandListener(this)
		            .applyToClusterSettings(builder -> 
		                builder.hosts(Arrays.asList(new ServerAddress(props.getDbHost(), props.getDbPort()))))
		            .build();
			
			logger.info("Creating client");
			mongoClient = MongoClients.create(settings);
			
			database = mongoClient.getDatabase(props.getDbName());
		} catch (Exception e) {
			logger.error("Error on database", e);
		}
	}

	@Override
	public void commandStarted(CommandStartedEvent event) {
		logger.debug("Command {}", event);
	}

	@Override
	public void commandSucceeded(CommandSucceededEvent event) {
		logger.debug("Command {}", event);
	}

	@Override
	public void commandFailed(CommandFailedEvent event) {
		logger.debug("Command {}", event);
	}
}
