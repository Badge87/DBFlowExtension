package com.danielebachicchi.dbflowextension;

import com.dbflow5.query.SQLOperator;
import com.dbflow5.structure.BaseModel;

import java.util.List;

public interface IBaseDAO<ENTITY extends BaseModel> {

    List<ENTITY> queryAll();
    ENTITY querySingle(SQLOperator... conditions);
    List<ENTITY> query(SQLOperator... conditions);
    void delete(SQLOperator... conditions);
    boolean deleteEntity(ENTITY entity);
    void deleteEntities(List<ENTITY> entities);
    void deleteAll();
    long queryCount(SQLOperator... conditions);
    long queryCount();
    boolean saveEntity(ENTITY entity);
    void saveEntities(List<ENTITY> entities);
}
