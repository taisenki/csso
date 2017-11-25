package org.taisenki.csso.client.web.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.taisenki.csso.common.util.BaseUtil;
import org.taisenki.csso.core.key.KeyService;

/**
 * @author Administrator
 *
 */
public class CssoGeneratePrivateKeyFileFilter extends BaseClientFilter{

	private static Logger logger = LoggerFactory.getLogger(CssoGeneratePrivateKeyFileFilter.class
			.getName());
	private String cssoServerFetchKeyUrl = null;
	//运用标识
	private String appId = null;
	/**
	 * 生成私钥文件类
	 */
	protected String cssoGeneratePrivateKeyFileClass = "com.github.ebnew.csso.client.key.DefaultKeyServiceImpl";
	
	protected KeyService keyService= null; 

	@Override
	public void destroy() {
		if (this.keyService != null)
			try {
				keyService.close();
			} catch (IOException e) {
				logger.error("密钥服务关闭失败：{}", e.getMessage());
			}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filter) throws IOException, ServletException {
		// TODO Auto-generated method stub
		try {
			keyService.findKeyByAppId(appId);
		} catch (Exception e) {
			logger.error("获取应用秘钥失败！", e);
		}
		//过滤器继续执行
		filter.doFilter(request, response);
		
	}

	@Override
	protected void doInit(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		cssoGeneratePrivateKeyFileClass = getInitParameterWithDefalutValue(filterConfig, "appClientDefaultKeyServiceClass", cssoGeneratePrivateKeyFileClass);
		//获取appId参数值
		appId = getInitParameterWithDefalutValue(filterConfig,"cssoClientAppId","1001");
		//获取服务器访问路径参数值
		cssoServerFetchKeyUrl = getInitParameterWithDefalutValue(filterConfig,"cssoServerFetchKeyUrl","http://localhost:8080/csso-web/fetchKey.do");
		//构造登录本应用的处理器对象。
		if(!BaseUtil.isEmpty(cssoGeneratePrivateKeyFileClass)){
			try{
				//实例化
				this.keyService = (KeyService) (Class.forName(cssoGeneratePrivateKeyFileClass)
											.getConstructor(String.class,String.class)).newInstance(cssoServerFetchKeyUrl,appId);	//实现类需无参构造方法
			}catch (Exception e) {
				logger.error("Initialization failed cause of {}", e.getMessage());		//记录初始化类日志
			}
		}
	}
}
