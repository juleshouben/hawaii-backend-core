/**
 * Copyright 2015 Q24
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.kahu.hawaii.util.spring;

import io.kahu.hawaii.service.sql.SqlQueryService;
import io.kahu.hawaii.util.exception.ServerException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public abstract class AbstractDBRepository<T> {

    private NamedParameterJdbcTemplate jdbcTemplate;
    private final RowMapper<T> rowMapper = new RowMapper<T>() {
        @Override
        public T mapRow(ResultSet rs, int rowNum) throws SQLException {
            return convertRow(rs);
        }
    };
    private final ResultSetExtractor<T> resultSetExtractor = new ResultSetExtractor<T>() {
        @Override
        public T extractData(ResultSet rs) throws SQLException, DataAccessException {
            if (rs.next()) {
                return convertRow(rs);
            }
            return null;
        }
    };

    private final SqlQueryService queryService;
    private final String resourcePath;

    public AbstractDBRepository(DataSource dataSource, SqlQueryService queryService, String resourcePath) {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.queryService = queryService;
        this.resourcePath = resourcePath;
    }

    protected String getSqlQuery(String queryId) throws ServerException {
        return queryService.getSqlQuery(resourcePath, queryId);
    }

    public NamedParameterJdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    /**
     * Setter added so a mock can be inserted when unit testing...
     */
    public void setJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    protected T query(String sql) {
        return jdbcTemplate.query(sql, resultSetExtractor);
    }

    protected T query(String sql, Map<String, ?> paramMap) {
        return jdbcTemplate.query(sql, paramMap, resultSetExtractor);
    }

    protected List<T> queryList(String sql) {
        return jdbcTemplate.query(sql, rowMapper);
    }

    protected List<T> queryList(String sql, Map<String, ?> paramMap) {
        return jdbcTemplate.query(sql, paramMap, rowMapper);
    }

    protected void updateQuery(String sql, Map<String, ?> paramMap) {
        jdbcTemplate.update(sql, paramMap);
    }

    /**
     * Helper method which will convert a row in a ResultSet to a Java object.
     * There cursor will already be positioned correctly and there is no need to
     * close the ResultSet after the conversion.
     */
    protected abstract T convertRow(ResultSet rs) throws SQLException;

    // Helper methods to retrieve items from a ResultSet and correctly handle
    // null values returned by the database. By default ResultSet works with
    // primitive types for numbers and booleans.

    protected Integer getInteger(ResultSet rs, int columnIndex) throws SQLException {
        int result = rs.getInt(columnIndex);
        return rs.wasNull() ? null : result;
    }

    protected Integer getInteger(ResultSet rs, String columnLabel) throws SQLException {
        int result = rs.getInt(columnLabel);
        return rs.wasNull() ? null : result;
    }

    protected Long getLong(ResultSet rs, int columnIndex) throws SQLException {
        long result = rs.getLong(columnIndex);
        return rs.wasNull() ? null : result;
    }

    protected Long getLong(ResultSet rs, String columnLabel) throws SQLException {
        long result = rs.getLong(columnLabel);
        return rs.wasNull() ? null : result;
    }

    protected Float getFloat(ResultSet rs, int columnIndex) throws SQLException {
        float result = rs.getFloat(columnIndex);
        return rs.wasNull() ? null : result;
    }

    protected Float getFloat(ResultSet rs, String columnLabel) throws SQLException {
        float result = rs.getFloat(columnLabel);
        return rs.wasNull() ? null : result;
    }

    protected Double getDouble(ResultSet rs, int columnIndex) throws SQLException {
        double result = rs.getDouble(columnIndex);
        return rs.wasNull() ? null : result;
    }

    protected Double getDouble(ResultSet rs, String columnLabel) throws SQLException {
        double result = rs.getDouble(columnLabel);
        return rs.wasNull() ? null : result;
    }

    protected Boolean getBoolean(ResultSet rs, int columnIndex) throws SQLException {
        boolean result = rs.getBoolean(columnIndex);
        return rs.wasNull() ? null : result;
    }

    protected Boolean getBoolean(ResultSet rs, String columnLabel) throws SQLException {
        boolean result = rs.getBoolean(columnLabel);
        return rs.wasNull() ? null : result;
    }

}
