package com.quanshoppingcart.backend.setting.country;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.quanshoppingcart.common.entity.Country;

public interface CountryRepository extends CrudRepository<Country, Integer> {
	public List<Country> findAllByOrderByNameAsc();
}
