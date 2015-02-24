package pl.mrasoft.common.hibernate.dialect;


import org.hibernate.dialect.pagination.LegacyLimitHandler;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.dialect.pagination.SQLServer2005LimitHandler;
import org.hibernate.engine.spi.RowSelection;

public class SQLServer2012Dialect extends org.hibernate.dialect.SQLServer2012Dialect {

    // limit/offset support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Override
    public LimitHandler buildLimitHandler(String sql, RowSelection selection) {
        return new LegacyLimitHandler( this, sql, selection );
    }

    @Override
    public boolean supportsLimit() {
        return true;
    }

    @Override
    public boolean supportsLimitOffset() {
        return true;
    }

    @Override
    public boolean supportsVariableLimit() {
        return true;
    }

    @Override
    public boolean bindLimitParametersInReverseOrder() {
        return false;
    }

    @Override
    public boolean bindLimitParametersFirst() {
        return false;
    }

    @Override
    public boolean useMaxForLimit() {
        return false;
    }

    @Override
    public boolean forceLimitUsage() {
        return false;
    }

    @Override
    public String getLimitString(String query, int offset, int limit) {
        return getLimitString( query, offset > 0);
    }

    @Override
    protected String getLimitString(String query, boolean hasOffset) {
        return query + (hasOffset ? " offset ? rows fetch next ? rows only" : " offset 0 rows fetch next ? rows only");
    }

}
