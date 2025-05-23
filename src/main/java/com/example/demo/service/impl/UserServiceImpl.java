package com.example.demo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Util.Hash;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.dto.UserDto;
import com.example.demo.model.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserMapper userMapper;

	// 從資料庫撈出 User 實體
	// 轉換為 UserDto（用 UserMapper 或手動轉換）
	// 回傳給前端使用（安全、不含密碼）
	@Override
	public UserDto getUser(String username) {
		User user = userRepository.getUser(username);
		if (user == null) {
			return null;
		}
		return userMapper.toDto(user);
	}

	// 寫入資料庫用，通常會：先對密碼進行加密（加 salt）
	// 建立 User 實體（不是 UserDto）
	// 呼叫 userRepository.save(user) 儲存資料

	@Override
	public void addUser(String username, String password, String email, Boolean active, String role) {
		String salt = Hash.getSalt();
		String passwordHash = Hash.getHash(password, salt);
		User user = new User(null, username, passwordHash, salt, email, active, role);
		userRepository.save(user);
	}

}