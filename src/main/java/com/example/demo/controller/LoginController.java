package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.exception.CertException;
import com.example.demo.model.dto.UserCert;
import com.example.demo.service.CertService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/login")
public class LoginController {

	@Autowired
	private CertService certService;

	@GetMapping
	public String loginPage() {
		return "login"; // 回傳 login.jsp 頁面
	}

	// Model 是傳資料給 JSP 用的
	// HttpSession 是儲存登入狀態的容器
	@PostMapping
	public String checkLogin(@RequestParam String username, @RequestParam String password, Model model,
			HttpSession session) {
		// 取得憑證
		UserCert userCert = null;
		try {
			userCert = certService.getCert(username, password);
		} catch (CertException e) {
			session.invalidate();// 清掉舊的 session
			// 將錯誤資料丟給 error.jsp
			model.addAttribute("message", e.getMessage()); // 顯示錯誤訊息
			e.printStackTrace();
			return "err"; // 跳轉到錯誤頁面 err.jsp
		}

//		登入成功後，把 userCert（登入憑證）存到 session
//
//		代表「此使用者已登入」，後續可以透過這個憑證做身份驗證
//
//		然後 redirect:/room 表示重導到 /room 頁面
		// 將憑證放到 session 變數中, 以利其他程式進行取用與驗證
		session.setAttribute("userCert", userCert);
		return "redirect:/room"; // 重導到首頁
	}

}