package com.danielebachicchi.dbflowextension;

import com.dbflow5.config.DBFlowDatabase;
import com.dbflow5.config.FlowManager;
import com.dbflow5.database.DatabaseWrapper;
import com.dbflow5.query.From;
import com.dbflow5.query.SQLOperator;
import com.dbflow5.query.SQLite;
import com.dbflow5.structure.BaseModel;
import com.dbflow5.transaction.ITransaction;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class BaseDAO<ENTITY extends BaseModel> implements IBaseDAO<ENTITY> {

    private Class<ENTITY> _entityClass;
    private Class<? extends DBFlowDatabase> _databaseClass;

    public BaseDAO(Class<ENTITY> entityClass, Class<? extends DBFlowDatabase> database) {
        _entityClass = entityClass;
        _databaseClass = database;
    }


    @Override
    public List<ENTITY> queryAll() {
        return executeTransaction(new ITransaction<List<ENTITY>>() {
            @Override
            public List<ENTITY> execute(@NotNull DatabaseWrapper databaseWrapper) {
                return getBaseSelect().queryList(databaseWrapper);
            }
        });
    }

    @Override
    public ENTITY querySingle(final SQLOperator... conditions) {
        return executeTransaction(new ITransaction<ENTITY>() {
            @Override
            public ENTITY execute(@NotNull DatabaseWrapper databaseWrapper) {
                return getBaseSelect().where(conditions).querySingle(databaseWrapper);
            }
        });

    }

    @Override
    public List<ENTITY> query(final SQLOperator... conditions) {
        return executeTransaction(new ITransaction<List<ENTITY>>() {
            @Override
            public List<ENTITY> execute(@NotNull DatabaseWrapper databaseWrapper) {
                return getBaseSelect().where(conditions).queryList(databaseWrapper);
            }
        });
    }

    @Override
    public void delete(final SQLOperator... conditions) {

        executeTransaction(new ITransaction<Void>() {
            @Override
            public Void execute(@NotNull DatabaseWrapper databaseWrapper) {
                getBaseDelete().where(conditions).execute(databaseWrapper);
                return null;
            }
        });

    }

    @Override
    public boolean deleteEntity(final ENTITY entity) {
        if(entity == null)
            return true;

        return executeTransaction(new ITransaction<Boolean>() {
            @Override
            public Boolean execute(@NotNull DatabaseWrapper databaseWrapper) {

                return entity.delete(databaseWrapper);
            }
        });


    }

    @Override
    public void deleteEntities(final List<ENTITY> entities) {
        if(entities == null)
            return;

        executeTransaction(new ITransaction<Void>() {
            @Override
            public Void execute(@NotNull DatabaseWrapper databaseWrapper) {
                for(int i = 0 ; i < entities.size(); i++){
                    entities.get(i).delete(databaseWrapper);
                }
                return null;
            }
        });



    }

    @Override
    public void deleteAll() {
        getBaseDelete().execute(getDatabase());

    }

    @Override
    public long queryCount(final SQLOperator... conditions) {
        final From<ENTITY> from = getBaseCount();
        return executeTransaction(new ITransaction<Long>() {

            @Override
            public Long execute(@NotNull DatabaseWrapper databaseWrapper) {
                return conditions != null ? from.where(conditions).longValue(databaseWrapper): from.longValue(databaseWrapper);

            }
        });

    }

    @Override
    public long queryCount() {
        return queryCount((SQLOperator) null);
    }

    @Override
    public boolean saveEntity(final ENTITY entity) {
        if(entity == null)
            return false;

        return executeTransaction(new ITransaction<Boolean>() {

            @Override
            public Boolean execute(@NotNull DatabaseWrapper databaseWrapper) {
                entity.save(databaseWrapper);
                return true;
            }
        });


    }

    @Override
    public void saveEntities(final List<ENTITY> entities) {
        if(entities == null || entities.size() < 1)
            return;

        int savedItems = executeTransaction(new ITransaction<Integer>(){
            @Override
            public Integer execute(@NotNull DatabaseWrapper databaseWrapper) {
                for(int i = 0 ; i < entities.size(); i++){
                    entities.get(i).save(databaseWrapper);
                }
                return entities.size();
            }
        });


    }

    protected From<ENTITY> getBaseSelect(){
        return SQLite.select().from(_entityClass);
    }
    protected From<ENTITY> getBaseDelete(){
        return SQLite.delete(_entityClass);
    }
    protected From<ENTITY> getBaseCount(){

        return SQLite.selectCountOf().from(_entityClass);
    }




    public DBFlowDatabase getDatabase(){
        return FlowManager.getDatabase(_databaseClass);
    }
    public <I> I executeTransaction(ITransaction<I> transaction){

        return getDatabase().executeTransaction(transaction);

    }

}
