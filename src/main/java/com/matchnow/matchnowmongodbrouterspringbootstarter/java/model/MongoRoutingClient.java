package com.matchnow.matchnowmongodbrouterspringbootstarter.java.model;

import com.mongodb.ClientSessionOptions;
import com.mongodb.client.*;
import com.mongodb.connection.ClusterDescription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class MongoRoutingClient implements MongoClient {
    private final MongoClient writeClient;
    private final MongoClient readClient;

    @Override
    public MongoDatabase getDatabase(String s) {
        return client().getDatabase(s);
    }

    @Override
    public ClientSession startSession() {
        return client().startSession();
    }

    @Override
    public ClientSession startSession(ClientSessionOptions clientSessionOptions) {
        return client().startSession(clientSessionOptions);
    }

    @Override
    public void close() {
        client().close();
    }

    @Override
    public MongoIterable<String> listDatabaseNames() {
        return client().listDatabaseNames();
    }

    @Override
    public MongoIterable<String> listDatabaseNames(ClientSession clientSession) {
        return client().listDatabaseNames(clientSession);
    }

    @Override
    public ListDatabasesIterable<Document> listDatabases() {
        return client().listDatabases();
    }

    @Override
    public ListDatabasesIterable<Document> listDatabases(ClientSession clientSession) {
        return client().listDatabases(clientSession);
    }

    @Override
    public <TResult> ListDatabasesIterable<TResult> listDatabases(Class<TResult> aClass) {
        return client().listDatabases(aClass);
    }

    @Override
    public <TResult> ListDatabasesIterable<TResult> listDatabases(ClientSession clientSession, Class<TResult> aClass) {
        return client().listDatabases(clientSession, aClass);
    }

    @Override
    public ChangeStreamIterable<Document> watch() {
        return client().watch();
    }

    @Override
    public <TResult> ChangeStreamIterable<TResult> watch(Class<TResult> aClass) {
        return client().watch(aClass);
    }

    @Override
    public ChangeStreamIterable<Document> watch(List<? extends Bson> list) {
        return client().watch(list);
    }

    @Override
    public <TResult> ChangeStreamIterable<TResult> watch(List<? extends Bson> list, Class<TResult> aClass) {
        return client().watch(list, aClass);
    }

    @Override
    public ChangeStreamIterable<Document> watch(ClientSession clientSession) {
        return client().watch(clientSession);
    }

    @Override
    public <TResult> ChangeStreamIterable<TResult> watch(ClientSession clientSession, Class<TResult> aClass) {
        return client().watch(clientSession, aClass);
    }

    @Override
    public ChangeStreamIterable<Document> watch(ClientSession clientSession, List<? extends Bson> list) {
        return client().watch(clientSession, list);
    }

    @Override
    public <TResult> ChangeStreamIterable<TResult> watch(ClientSession clientSession, List<? extends Bson> list, Class<TResult> aClass) {
        return client().watch(clientSession, list, aClass);
    }

    @Override
    public ClusterDescription getClusterDescription() {
        return client().getClusterDescription();
    }

    private MongoClient client() {
        if (readOnly()) {
            log.debug("MongoClient has been selected -> READ");
            return readClient;
        } else {
            log.debug("MongoClient has been selected -> WRITE");
            return writeClient;
        }
    }

    private boolean readOnly() {
        return MongoRoutingContext.getCurrentStatus().equals(MongoRoutingStatus.READ);
    }
}
