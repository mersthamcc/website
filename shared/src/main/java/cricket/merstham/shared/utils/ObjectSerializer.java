package cricket.merstham.shared.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.function.Function;

public class ObjectSerializer<T> {

    private static final Logger LOG = LoggerFactory.getLogger(ObjectSerializer.class);

    public byte[] serialize(T object) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream =
                        new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(object);

            objectOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException ex) {
            LOG.error("Error serializing object", ex);
            return null;
        }
    }

    public <R> R serializeAndWrap(T object, Function<byte[], R> wrapper) {
        return wrapper.apply(serialize(object));
    }

    public T deserialize(byte[] bytes) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            return (T) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            LOG.error("Error deserializing object", ex);
            return null;
        }
    }
}
