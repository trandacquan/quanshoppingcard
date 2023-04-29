package com.quanshoppingcart.frontend.setting;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.quanshoppingcart.common.entity.setting.Setting;
import com.quanshoppingcart.common.entity.setting.SettingCategory;

public interface SettingRepository extends CrudRepository<Setting, String> {
	
	public List<Setting> findByCategory(SettingCategory category);
	
}
