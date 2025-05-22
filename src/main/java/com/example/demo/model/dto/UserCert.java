package com.example.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

// 使用者憑證
// 登入成功之後會得到的憑證資料(只有 Getter)
//User 是資料庫 Entity，裡面可能包含敏感資訊（例如密碼、salt）
//安全考量下不應直接傳給前端或放進 session
//所以我們只回傳必要資訊，使用 UserCert 這個 DTO（資料傳輸物件）
//✅ 「登入成功後的身分證明」
//
//✅ 「只含安全且必要的資料」

@AllArgsConstructor
@Getter
@ToString
public class UserCert {
	private Integer userId; // 使用者 Id
	private String username; // 使用者名稱
	private String role; // 角色權限

}
