package com.xuxl.tcctransaction.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class JdkSerializationSerializer<T> implements ObjectSerializer<T> {
	
    public byte[] serialize(T object) {
        if (object == null) {
            return null;
        } else {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
            try {
                ObjectOutputStream ex = new ObjectOutputStream(baos);
                ex.writeObject(object);
                ex.flush();
            } catch (IOException var3) {
                throw new IllegalArgumentException("Failed to serialize object of type: " + object.getClass(), var3);
            }

            return baos.toByteArray();
        }
    }

    @SuppressWarnings("unchecked")
	public T deserialize(byte[] bytes) {
        if (bytes == null) {
            return null;
        } else {
            try {
                ObjectInputStream ex = new ObjectInputStream(new ByteArrayInputStream(bytes));
                return (T) ex.readObject();
            } catch (IOException var2) {
                throw new IllegalArgumentException("Failed to deserialize object", var2);
            } catch (ClassNotFoundException var3) {
                throw new IllegalStateException("Failed to deserialize object type", var3);
            }
        }
    }
}
