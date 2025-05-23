package com.example.demo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Util.Hash;
import com.example.demo.exception.PasswordInvalidException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.dto.UserCert;
import com.example.demo.model.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CertService;

@Service
public class CertServiceImpl implements CertService {
	@Autowired // 自動綁定
	private UserRepository userRepository;

	@Override
	// 這是登入的概念,登入就要拿到一個憑證
	// public UserCert getCert(String username, String password) throws
	// CertException { ->implements可以拋出CertService,也可以拋出子類(Usernotfound)
	public UserCert getCert(String username, String password) throws UserNotFoundException, PasswordInvalidException {
		// 1. 是否有此人
		User user = userRepository.getUser(username);
		if (user == null) {
			throw new UserNotFoundException("查無此人");
			// 自訂UserNotFoundException
		}
		// 2. 密碼 hash 比對,把password跟資料庫salt拿出來
		String passwordHash = Hash.getHash(password, user.getSalt());
		if (!passwordHash.equals(user.getPasswordHash())) {
			throw new PasswordInvalidException("密碼錯誤");
			// 自訂PasswordInvalidException
		}
		// 3. 簽發憑證
		UserCert userCert = new UserCert(user.getUserId(), user.getUsername(), user.getRole());
		return userCert;
	}

}
