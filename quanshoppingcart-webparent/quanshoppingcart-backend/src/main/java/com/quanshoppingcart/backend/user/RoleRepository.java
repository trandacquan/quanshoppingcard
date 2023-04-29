package com.quanshoppingcart.backend.user;

import org.springframework.data.repository.CrudRepository;

import com.quanshoppingcart.common.entity.Role;
//CrudRepository có các phương thức dùng để SELECT, CREATE, UPDATE, DELETE
public interface RoleRepository extends CrudRepository<Role, Integer> {//tham số thứ 1 là entity, tham số thứ 2 là kiểu dữ liệu của khóa chính(Primary key)

}
