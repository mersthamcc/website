package cricket.merstham.graphql.jpa;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.isNull;

public class JpaJsonbType implements UserType {

    protected final ObjectMapper mapper = new JsonMapper();

    @Override
    public int[] sqlTypes() {
        return new int[] {Types.JAVA_OBJECT};
    }

    @Override
    public Class<JsonNode> returnedClass() {
        return JsonNode.class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        if (isNull(x)) {
            return isNull(y);
        }
        return x.equals(y);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public Object nullSafeGet(
            ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
            throws HibernateException, SQLException {
        final String data = rs.getString(names[0]);
        if (isNull(data)) {
            return null;
        }
        try {
            return mapper.readTree(data.getBytes(UTF_8));
        } catch (final Exception ex) {
            throw new RuntimeException("Failed to parse JSON: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void nullSafeSet(
            PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
            throws HibernateException, SQLException {
        if (isNull(value)) {
            st.setNull(index, Types.OTHER);
            return;
        }
        try {
            final StringWriter w = new StringWriter();
            mapper.writeValue(w, value);
            w.flush();
            st.setObject(index, w.toString(), Types.OTHER);
        } catch (final Exception ex) {
            throw new RuntimeException("Failed to convert to JSONB: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        try {
            TokenBuffer tb = new TokenBuffer(mapper, false);
            mapper.writeTree(tb, (JsonNode) value);
            return mapper.readTree(tb.asParser());
        } catch (IOException ex) {
            throw new HibernateException(ex);
        }
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(final Object value) throws HibernateException {
        return (Serializable) this.deepCopy(value);
    }

    @Override
    public Object assemble(final Serializable cached, final Object owner)
            throws HibernateException {
        return this.deepCopy(cached);
    }

    @Override
    public Object replace(final Object original, final Object target, final Object owner)
            throws HibernateException {
        return this.deepCopy(original);
    }
}
