package com.xuxl.tcctransaction.spring.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.xa.Xid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import com.xuxl.tcctransaction.Transaction;
import com.xuxl.tcctransaction.common.TransactionType;
import com.xuxl.tcctransaction.repository.CachableTransactionRepository;
import com.xuxl.tcctransaction.repository.TransactionIOException;
import com.xuxl.tcctransaction.serializer.KryoTransactionSerializer;
import com.xuxl.tcctransaction.serializer.ObjectSerializer;
import com.xuxl.tcctransaction.utils.ByteUtils;

public class SpringRedisTransactionRepository extends CachableTransactionRepository {

	@Autowired
	private RedisTemplate<Object, Object> redisTemplate;

	private ObjectSerializer<Transaction> serializer = new KryoTransactionSerializer<Transaction>();

	private String keyPrefix = "TCC:";

	private byte[] getRedisKey(String keyPrefix, Xid xid) {
		byte[] prefix = keyPrefix.getBytes();
		byte[] globalTransactionId = xid.getGlobalTransactionId();
		byte[] branchQualifier = xid.getBranchQualifier();
		byte[] key = new byte[prefix.length + globalTransactionId.length + branchQualifier.length];
		System.arraycopy(prefix, 0, key, 0, prefix.length);
		System.arraycopy(globalTransactionId, 0, key, prefix.length, globalTransactionId.length);
		System.arraycopy(branchQualifier, 0, key, prefix.length + globalTransactionId.length, branchQualifier.length);
		return key;
	}

	protected int doCreate(final Transaction transaction) {
		final byte[] key = getRedisKey(keyPrefix, transaction.getXid());
		try {
			Boolean executeResult = redisTemplate.execute(new RedisCallback<Boolean>() {
				public Boolean doInRedis(RedisConnection conn) throws DataAccessException {
					Boolean result = conn.hSetNX(key, ByteUtils.longToBytes(transaction.getVersion()),
							TransactionSerializer.serialize(serializer, transaction));
					return result;
				}
			});
			return executeResult ? 1 : 0;
		} catch (Exception e) {
			throw new TransactionIOException(e);
		}
	}

	protected int doUpdate(final Transaction transaction) {
		final byte[] key = getRedisKey(keyPrefix, transaction.getXid());
		try {
			Boolean executeResult = redisTemplate.execute(new RedisCallback<Boolean>() {
				public Boolean doInRedis(RedisConnection conn) throws DataAccessException {
					transaction.updateTime();
					transaction.updateVersion();
					Boolean result = conn.hSetNX(key, ByteUtils.longToBytes(transaction.getVersion()),
							TransactionSerializer.serialize(serializer, transaction));
					return result;
				}
			});
			return executeResult ? 1 : 0;
		} catch (Exception e) {
			throw new TransactionIOException(e);
		}
	}

	protected int doDelete(Transaction transaction) {
		final byte[] key = getRedisKey(keyPrefix, transaction.getXid());
		try {
			Long result = redisTemplate.execute(new RedisCallback<Long>() {
				public Long doInRedis(RedisConnection conn) throws DataAccessException {
					return conn.del(key);
				}
			});
			return result.intValue();
		} catch (Exception e) {
			throw new TransactionIOException(e);
		}
	}

	protected Transaction doFindOne(Xid xid) {
		final byte[] key = getRedisKey(keyPrefix, xid);
		try {
			byte[] executeContent = redisTemplate.execute(new RedisCallback<byte[]>() {
				public byte[] doInRedis(RedisConnection conn) throws DataAccessException {
					Map<byte[], byte[]> fieldValueMap = conn.hGetAll(key);
					List<Map.Entry<byte[], byte[]>> entries = new ArrayList<Map.Entry<byte[], byte[]>>(
							fieldValueMap.entrySet());
					Collections.sort(entries, new Comparator<Map.Entry<byte[], byte[]>>() {
						public int compare(Map.Entry<byte[], byte[]> entry1, Map.Entry<byte[], byte[]> entry2) {
							return (int) (ByteUtils.bytesToLong(entry1.getKey())
									- ByteUtils.bytesToLong(entry2.getKey()));
						}
					});
					byte[] content = entries.get(entries.size() - 1).getValue();
					return content;
				}
			});
			if (executeContent != null) {
				return TransactionSerializer.deserialize(serializer, executeContent);
			}
			return null;
		} catch (Exception e) {
			throw new TransactionIOException(e);
		}
	}

	protected List<Transaction> doFindAllUnmodifiedSince(Date date) {
		List<Transaction> allTransactions = doFindAll();
		List<Transaction> allUnmodifiedSince = new ArrayList<Transaction>();
		for (Transaction transaction : allTransactions) {
			if (transaction.getTransactionType().equals(TransactionType.ROOT)
					&& transaction.getLastUpdateTime().compareTo(date) < 0) {
				allUnmodifiedSince.add(transaction);
			}
		}
		return allUnmodifiedSince;
	}

	protected List<Transaction> doFindAll() {
		try {
			List<Transaction> transactions = new ArrayList<Transaction>();
			Set<byte[]> keys = redisTemplate.execute(new RedisCallback<Set<byte[]>>() {
				public Set<byte[]> doInRedis(RedisConnection conn) throws DataAccessException {
					return conn.keys((keyPrefix + "*").getBytes());
				}
			});
			for (final byte[] key : keys) {
				byte[] executeContent = redisTemplate.execute(new RedisCallback<byte[]>() {
					public byte[] doInRedis(RedisConnection conn) throws DataAccessException {
						Map<byte[], byte[]> fieldValueMap = conn.hGetAll(key);
						List<Map.Entry<byte[], byte[]>> entries = new ArrayList<Map.Entry<byte[], byte[]>>(
								fieldValueMap.entrySet());
						Collections.sort(entries, new Comparator<Map.Entry<byte[], byte[]>>() {
							public int compare(Map.Entry<byte[], byte[]> entry1, Map.Entry<byte[], byte[]> entry2) {
								return (int) (ByteUtils.bytesToLong(entry1.getKey())
										- ByteUtils.bytesToLong(entry2.getKey()));
							}
						});
						byte[] content = entries.get(entries.size() - 1).getValue();
						return content;
					}
				});
				if (executeContent != null) {
					transactions.add(TransactionSerializer.deserialize(serializer, executeContent));
				}
			}
			return transactions;
		} catch (Exception e) {
			throw new TransactionIOException(e);
		}
	}
}
