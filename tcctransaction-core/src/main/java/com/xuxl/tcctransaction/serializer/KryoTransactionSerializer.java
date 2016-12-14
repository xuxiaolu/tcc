package com.xuxl.tcctransaction.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.xuxl.tcctransaction.InvocationContext;
import com.xuxl.tcctransaction.Participant;
import com.xuxl.tcctransaction.Terminator;
import com.xuxl.tcctransaction.Transaction;
import com.xuxl.tcctransaction.api.TransactionStatus;
import com.xuxl.tcctransaction.api.TransactionXid;
import com.xuxl.tcctransaction.common.TransactionType;

public class KryoTransactionSerializer<T> implements ObjectSerializer<T> {

    private static Kryo kryo = null;

    static {
        kryo = new Kryo();
        kryo.register(Transaction.class);
        kryo.register(TransactionXid.class);
        kryo.register(TransactionStatus.class);
        kryo.register(TransactionType.class);
        kryo.register(Participant.class);
        kryo.register(Terminator.class);
        kryo.register(InvocationContext.class);
    }


    public byte[] serialize(T transaction) {
        Output output = new Output(256, -1);
        kryo.writeObject(output, transaction);
        return output.toBytes();
    }

    @SuppressWarnings("unchecked")
	public T deserialize(byte[] bytes) {
        Input input = new Input(bytes);
        Transaction transaction = kryo.readObject(input, Transaction.class);
        return (T) transaction;
    }
}
