package dev.nautchkafe.scylladb.bridge;

import io.vavr.collection.List
import java.util.function.Function;

interface ScyllaEntityConfiguration<T> {

    String tableName();

    String schema(); 
    
    ScyllaResultMapper<T> mapper();

    FunctionT, List<Object>> getBindValues();

    Function<T, List<Object>> toBindValues();

    Function<T, Object> primaryKey();
}