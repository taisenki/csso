package org.taisenki.csso.core.model;

import java.util.Map;

/** 
 * 用户主体，代表一个用户 <br/> 
 *
 * @date: 2017年11月21日 上午11:08:23
 * @author taisenki 
 * @version  1.0
 * @since 
 */  
public interface Principal {

	public Map<String, Object> getAttributes();

	public String getId();

}