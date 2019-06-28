package com.vyako.smarttbm.configuration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

/*
 * This cls will be set Access-Control_allow_Headers
 * which is used for web MVC
 */

@Component
public class SimpleCORSFilter implements Filter {

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) {
		try {
			HttpServletRequest request = (HttpServletRequest) req;

			HttpServletResponse response = (HttpServletResponse) res;
			// response.setHeader("Access-Control-Allow-Origin", "*");

			response.setHeader("Access-Control-Allow-Origin",
					request.getHeader("Origin"));
			response.setHeader("Access-Control-Allow-Credentials", "true");
			response.setHeader("Access-Control-Allow-Methods",
					"PUT, GET, POST, DELETE, OPTIONS");
			response.setHeader("Access-Control-Max-Age", "3600");
			response.setHeader("Access-Control-Allow-Headers",
					"Content-Type,X-Requested-With,accept,Origin,Access-Control-Request-Method,"
							+ "Access-Control-Request-Headers,Authorization");

			// response.setHeader("Access-Control-Allow-Headers",
			// "X-Requested-With, Authorization,Content-Type");
			// response.setHeader("Access-Control-Expose-Headers",
			// "Access-Control-Allow-Origin,Access-Control-Allow-Credentials,Origin=file://");
			response.setHeader("Content-Type", "application/json,text/plain");
			chain.doFilter(req, res);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init(FilterConfig filterConfig) {
	}

	@Override
	public void destroy() {
	}

}
