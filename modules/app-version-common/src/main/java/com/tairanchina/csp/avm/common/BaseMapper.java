package com.tairanchina.csp.avm.common;


import java.io.Serializable;
import java.util.List;

import io.mybatis.mapper.Mapper;
import io.mybatis.mapper.example.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * @author me 2022-11-29 20:44
 */
public interface BaseMapper<T, ID extends Serializable> extends Mapper<T, ID> {

    @Override
    default int insert(T entity) {
        return insertSelective(entity);
    }

    default T selectById(ID id) {
        return this.selectByPrimaryKey(id).orElse(null);
    }

    default int updateById(T entity) {
        return this.updateByPrimaryKey(entity);
    }

    default int deleteById(ID id) {
        return this.deleteByPrimaryKey(id);
    }


    default Page<T> selectPage(Pageable pageable, Example<T> wrapper) {
        Pageable zeroBased = PageRequest.of(Math.max(0, pageable.getPageNumber() - 1), pageable.getPageSize());


        final long count = countByExample(wrapper);
        wrapper.setEndSql(String.format("LIMIT %s,%s", zeroBased.getPageNumber() * zeroBased.getPageSize(), zeroBased.getPageSize()));
        final List<T> select = selectByExample(wrapper);
        return new PageImpl<>(select, zeroBased, count);
    }
}
