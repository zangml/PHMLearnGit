package com.koala.learn.dao;

import com.koala.learn.entity.SqlTest;
import com.koala.learn.entity.SqlTestWithBLOBs;

import java.util.List;

public interface SqlTestMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SqlTestWithBLOBs record);

    int insertSelective(SqlTestWithBLOBs record);

    SqlTestWithBLOBs selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SqlTestWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(SqlTestWithBLOBs record);

    int updateByPrimaryKey(SqlTest record);

    List<SqlTestWithBLOBs> selectAll();
}