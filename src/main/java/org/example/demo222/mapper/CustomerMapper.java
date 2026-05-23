package org.example.demo222.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.demo222.entity.Customer;

import java.util.List;

/**
 * 客户Mapper接口
 */
@Mapper
public interface CustomerMapper {
    int insert(Customer customer);
    int update(Customer customer);
    int deleteById(@Param("id") Long id);
    Customer selectById(@Param("id") Long id);
    List<Customer> selectByPage(@Param("name") String name,
                                @Param("phone") String phone,
                                @Param("offset") int offset,
                                @Param("pageSize") int pageSize);
    long selectCount(@Param("name") String name,
                     @Param("phone") String phone);
    List<Customer> selectAll();
    int countByCustomerId(@Param("customerId") Long customerId);
}