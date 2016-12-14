package com.xuxl.tcctransaction.spring.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import com.xuxl.tcctransaction.spring.domain.TccTransaction;
import com.xuxl.tcctransaction.spring.domain.TccTransactionCriteria;
import com.xuxl.tcctransaction.spring.domain.TccTransactionWithBLOBs;

public interface TccTransactionMapper {
	
    long countByExample(TccTransactionCriteria example);

    int deleteByExample(TccTransactionCriteria example);

    int deleteByPrimaryKey(Integer transactionId);

    int insert(TccTransactionWithBLOBs record);

    int insertSelective(TccTransactionWithBLOBs record);

    List<TccTransactionWithBLOBs> selectByExampleWithBLOBsWithRowbounds(TccTransactionCriteria example, RowBounds rowBounds);

    List<TccTransactionWithBLOBs> selectByExampleWithBLOBs(TccTransactionCriteria example);

    List<TccTransaction> selectByExampleWithRowbounds(TccTransactionCriteria example, RowBounds rowBounds);

    List<TccTransaction> selectByExample(TccTransactionCriteria example);

    TccTransactionWithBLOBs selectByPrimaryKey(Integer transactionId);

    int updateByExampleSelective(@Param("record") TccTransactionWithBLOBs record, @Param("example") TccTransactionCriteria example);

    int updateByExampleWithBLOBs(@Param("record") TccTransactionWithBLOBs record, @Param("example") TccTransactionCriteria example);

    int updateByExample(@Param("record") TccTransaction record, @Param("example") TccTransactionCriteria example);

    int updateByPrimaryKeySelective(TccTransactionWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(TccTransactionWithBLOBs record);

    int updateByPrimaryKey(TccTransaction record);
}