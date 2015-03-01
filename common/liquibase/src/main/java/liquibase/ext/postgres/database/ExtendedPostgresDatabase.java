package liquibase.ext.postgres.database;

import liquibase.database.ObjectQuotingStrategy;
import liquibase.structure.DatabaseObject;

public class ExtendedPostgresDatabase extends liquibase.database.core.PostgresDatabase {

    @Override
    public int getPriority() {
        return 2;
    }

    @Override
    public String escapeObjectName(String objectName, Class<? extends DatabaseObject> objectType) {
        if (objectName != null) {
            objectName = objectName.trim();
            if (mustQuoteObjectName(objectName, objectType)) {
                return quoteObject(objectName, objectType);
            } else if (quotingStrategy == ObjectQuotingStrategy.QUOTE_ALL_OBJECTS) {
                return quoteObject(objectName, objectType);
            }
            objectName = objectName.trim();
        }
        return objectName;
    }

}
