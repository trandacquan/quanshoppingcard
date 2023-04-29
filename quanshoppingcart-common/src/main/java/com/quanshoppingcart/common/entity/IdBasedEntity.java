package com.quanshoppingcart.common.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass//phải có @MappedSuperclass này thì khi kế thừa entity mới nhận biết được các annotation khai báo trong class này
public abstract class IdBasedEntity {

	@Id//khai báo khóa chính(PRIMARY KEY)
	@GeneratedValue(strategy = GenerationType.IDENTITY)//id tự động tăng
	protected Integer id;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}
