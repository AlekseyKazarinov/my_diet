package com.mydiet.mydiet.infrastructure;

import com.mydiet.mydiet.domain.entity.QuantityUnit;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Connection {

    private Consistence  consistence;
    private QuantityUnit toUnit;
    private QuantityUnit[] fromUnits;

    interface ConnectionForType {
        ConnectionFrom forType(Consistence consistence);
    }

    interface ConnectionFrom {
        ConnectionTo from(QuantityUnit... units);
    }

    interface ConnectionTo {
        Connection to(QuantityUnit unit);
    }

    public static ConnectionForType connection() {
        return ConnectionBuilder.getInstance();
    }

    public static class ConnectionBuilder implements
            ConnectionForType, ConnectionFrom, ConnectionTo {//}, ConnectionCreator {

        private Consistence  consistence;
        private QuantityUnit[] fromUnits;

        private ConnectionBuilder() {
        }

        @Override
        public ConnectionFrom forType(Consistence consistence) {
            this.consistence = consistence;
            return this;
        }

        @Override
        public ConnectionTo from(QuantityUnit... units) {
            this.fromUnits = units;
            return this;
        }

        public Connection to(QuantityUnit unit) {
            return new Connection(this.consistence, unit, this.fromUnits);
        }

        public static ConnectionForType getInstance() {
            return new ConnectionBuilder();
        }
    }

}
