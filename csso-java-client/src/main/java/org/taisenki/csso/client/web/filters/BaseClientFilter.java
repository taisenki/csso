package org.taisenki.csso.client.web.filters;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.taisenki.csso.common.util.BaseUtil;

/**
 * 公共基础的客户端过滤器类，定义了一些公共的方法。
 *
 */
public abstract class BaseClientFilter implements Filter{
	
	/**
	 * csso服务器主机地址。
	 */
	protected String cssoServerHost = "http://localhost:8080/csso-web/";
	
	
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		//初始化参数。
		cssoServerHost = getInitParameterWithDefalutValue(filterConfig, "cssoServerHost", cssoServerHost);
		
		//调用子类的初始化方法。
		doInit(filterConfig);
	}
	
	/**
	 * 由子类实现的初始化方法。
	 * @param filterConfig
	 * @throws ServletException
	 */
	protected abstract void doInit(FilterConfig filterConfig) throws ServletException;

	/**
	 * 获取过滤器参数值，带有默认值，若没有配置，则使用默认值。
	 * @param filterConfig
	 * @param paramName
	 * @param defalutValue
	 * @return
	 */
	protected String getInitParameterWithDefalutValue(FilterConfig filterConfig, String paramName, String defalutValue){
		String value = filterConfig.getInitParameter(paramName);
		if(BaseUtil.isEmpty(value)){
			value = defalutValue;
		}
		return value;
	}
	
	
	/**
	 * Retrieve the first cookie with the given name. Note that multiple
	 * cookies can have the same name but different paths or domains.
	 * @param request current servlet request
	 * @param name cookie name
	 * @return the first cookie with the given name, or {@code null} if none is found
	 */
	public static Cookie getCookie(HttpServletRequest request, String name) {
		Cookie cookies[] = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (name.equals(cookie.getName())) {
					return cookie;
				}
			}
		}
		return null;
	}

}
