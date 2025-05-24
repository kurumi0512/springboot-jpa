package com.example.demo.filter;

import java.io.IOException;

import com.example.demo.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter(urlPatterns = { "/rest/room/*", "/rest/rooms/*" }) // 需要登入才能訪問的路徑,新增修改刪除需要登入才能操作
public class LoginRestFilter extends HttpFilter {

	@Override
	protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String method = request.getMethod();

		// 只開放 GET 查詢
		if ("GET".equalsIgnoreCase(method)) {
			chain.doFilter(request, response);
			return;
		}

		// 新增修改刪除全部都要驗證才能做動作
		HttpSession session = request.getSession();
		// 非 GET 時驗證登入狀態
		if (session != null && session.getAttribute("userCert") != null) {
			chain.doFilter(request, response); // 有登入憑證 → 放行
			// 將請求繼續傳遞給下一個 Filter 或目標資源（Controller、Servlet 等）
		} else {
			// 未登入，回傳 401
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json;charset=UTF-8");
			ApiResponse<?> apiResponse = ApiResponse.error(401, "請先登入");
			// 利用 ObjectMapper 將指定物件轉 json
			ObjectMapper mapper = new ObjectMapper(); // 內建的
			String json = mapper.writeValueAsString(apiResponse);
			response.getWriter().write(json);
		}
	}

}
