package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.exception.CertException;
import com.example.demo.model.dto.UserCert;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.CertService;

import jakarta.servlet.http.HttpSession;

@Controller // Spring MVC 的控制器，會回應 HTTP 請求
@RequestMapping("/rest") // 控制器處理的路徑都會以 /rest 開頭。
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:8002" }, allowCredentials = "true")
//設定 CORS，允許前端網頁（例如用 React 開發的 localhost:5173）跨來源請求，並允許附帶 Cookie（像 session 憑證）
public class LoginRestController {

	@Autowired
	private CertService certService;
	// 自動注入 CertService，用來處理登入邏輯，例如驗證帳密、取得使用者憑證。

	// ApiResponse<Void>,表示這個 API 沒有實際要回傳的資料（data = null），像登入成功、登出成功這種。
	// 如果要回傳資料（如
	// UserDto、List<Product>），你就可以換成其他型別：ResponseEntity<ApiResponse<UserDto>>...

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<Void>> login(@RequestParam String username, @RequestParam String password,
			HttpSession session) {
		try {
			UserCert cert = certService.getCert(username, password);
			session.setAttribute("userCert", cert);
			return ResponseEntity.ok(ApiResponse.success("登入成功", null));
		} catch (CertException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED) // 401是非授權的錯誤,一般都放401
					.body(ApiResponse.error(401, "登入失敗: " + e.getMessage()));
		}
	}

	@GetMapping("/logout")
	public ResponseEntity<ApiResponse<Void>> logout(HttpSession session) {
		if (session.getAttribute("userCert") == null) { // 如果沒有session還要登出就會登出失敗
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(401, "登出失敗: 尚未登入 "));
		}
		session.invalidate(); // 把session清空
		return ResponseEntity.ok(ApiResponse.success("登出成功", null));
	}

	@GetMapping("/check-login") // 現在到底人是不是在登入狀態中
	// 回傳布林值 true/false，表示目前 session 中是否已登入
	// 前端可以定期打這個 API 來確認登入狀態
	public ResponseEntity<ApiResponse<Boolean>> checkLogin(HttpSession session) {
		boolean loggedIn = session.getAttribute("userCert") != null;
		return ResponseEntity.ok(ApiResponse.success("檢查登入", loggedIn));
	}

}