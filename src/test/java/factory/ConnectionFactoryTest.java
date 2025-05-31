package factory;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionFactoryTest {

    @Test
    void testGetConnection() {
        try (Connection conn = ConnectionFactory.getConnection()) {
            assertNotNull(conn, "A conexão não deve ser nula.");
            assertFalse(conn.isClosed(), "A conexão deve estar aberta.");
        } catch (SQLException e) {
            fail("Falha ao obter conexão: " + e.getMessage());
        }
    }
}
