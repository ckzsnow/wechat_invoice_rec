package com.bbz.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

public class SessionFilterService extends OncePerRequestFilter {

	private static final Logger logger = LoggerFactory
			.getLogger(SessionFilterService.class);
	
	private String[] filterUrls;

	public SessionFilterService() {
		filterUrls = new String[] { "/admin/" };
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String uri = request.getRequestURI();
		boolean doFilter = false;
		for (String url : filterUrls) {
			if (uri.indexOf(url) != -1) {
				doFilter = true;
				break;
			}
		}
		if (doFilter) {
			String userId = (String)request.getSession().getAttribute("user_id");
			if (null == userId || userId.isEmpty()) {
				logger.debug("user id is null.");
				boolean isAjaxRequest = isAjaxRequest(request);
				if (isAjaxRequest) {
					response.setCharacterEncoding("UTF-8");
					response.sendError(HttpStatus.UNAUTHORIZED.value(),
							"Unauthorized!");
					return;
				}
				response.sendRedirect("/views/login.html");
				return;
			}
		}
		filterChain.doFilter(request, response);
	}

	public static boolean isAjaxRequest(HttpServletRequest request) {
		String header = request.getHeader("X-Requested-With");
		if (header != null && "XMLHttpRequest".equals(header))
			return true;
		else
			return false;
	}

}