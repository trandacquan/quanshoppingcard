package com.quanshoppingcart.frontend.setting;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.quanshoppingcart.common.entity.Country;
import com.quanshoppingcart.common.entity.State;

public interface StateRepository extends CrudRepository<State, Integer> {
	
	public List<State> findByCountryOrderByNameAsc(Country country);
	
}
