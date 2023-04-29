package com.quanshoppingcart.backend.user;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.quanshoppingcart.common.entity.Role;
import com.quanshoppingcart.common.entity.User;

@Service
@Transactional
public class UserService {

	public static final int USERS_PER_PAGE = 4;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private RoleRepository roleRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public List<User> listAll() {
		return (List<User>) userRepo.findAll(Sort.by("firstName").ascending());
	}

	public Page<User> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

		Pageable pageable = PageRequest.of(pageNum - 1, USERS_PER_PAGE, sort);

		if (keyword != null) {
			return userRepo.findAll(keyword, pageable);
		}

		return userRepo.findAll(pageable);
	}

	public List<Role> listRoles() {
		return (List<Role>) roleRepo.findAll();// .findAll() trả về Iterable<Role> -->ép kiểu thành List<Role>
	}

	public User save(User user) {
		// nếu id == null -->Create, nếu id != null -->Update
		boolean isUpdatingUser = (user.getId() != null);

		if (isUpdatingUser) {// trường hợp edit
			// trường hợp update thì lấy user theo id
			User existingUser = userRepo.findById(user.getId()).get();

			if (user.getPassword().isEmpty()) {
				// nếu ko nhập password thì lấy password cũ để save lại
				user.setPassword(existingUser.getPassword());
			} else {
				// nếu có nhập password thì mã hóa password
				encodePassword(user);
			}

		} else {// trường hợp create
			encodePassword(user);// trường hợp create -->luôn mã hóa password
		}

		return userRepo.save(user);// save user xuống db
	}

	private void encodePassword(User user) {
		// mã hóa password bằng BCrypt
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
	}
	
	public User get(Integer id) throws UserNotFoundException {
		try {
			return userRepo.findById(id).get();//lấy user theo id
		} catch (NoSuchElementException ex) {
			throw new UserNotFoundException("Could not find any user with ID " + id);
			//khai báo exception do mình tự tạo sẽ giúp cho code dễ đọc, dễ hiểu, dễ debug hơn
		}
	}
	
	public void delete(Integer id) throws UserNotFoundException {
		Long countById = userRepo.countById(id);
		if (countById == null || countById == 0) {//nếu count == null hoặc count == 0 -->ko tồn tại user với id này
			throw new UserNotFoundException("Could not find any user with ID " + id);
		}

		userRepo.deleteById(id);
	}
	
	public void updateUserEnabledStatus(Integer id, boolean enabled) {
		userRepo.updateEnabledStatus(id, enabled);
	}
	
	public boolean isEmailUnique(Integer id, String email) {
		//lấy user theo email
		User userByEmail = userRepo.getUserByEmail(email);

		//nếu userByEmail == null -->ko bị trùng email
		if (userByEmail == null)
			return true;
		
		//trường hợp nếu trùng email thì xem xét đang create hay update
		boolean isCreatingNew = (id == null);

		if (isCreatingNew) {
			if (userByEmail != null)//nếu là create thì ko được trùng
				return false;
		} else {
			if (userByEmail.getId() != id) {//nếu là edit thì có thể trùng
				return false;
			}
		}

		return true;
	}
	
	public User getByEmail(String email) {
		return userRepo.getUserByEmail(email);
	}
	
	public User updateAccount(User userInForm) {
		User userInDB = userRepo.findById(userInForm.getId()).get();

		if (!userInForm.getPassword().isEmpty()) {
			userInDB.setPassword(userInForm.getPassword());
			encodePassword(userInDB);
		}

		if (userInForm.getPhotos() != null) {
			userInDB.setPhotos(userInForm.getPhotos());
		}

		userInDB.setFirstName(userInForm.getFirstName());
		userInDB.setLastName(userInForm.getLastName());

		return userRepo.save(userInDB);
	}

}
