package org.taisenki.csso.core.authentication.status;

import java.util.List;

import org.taisenki.csso.core.model.UserLoggedStatus;

/** 
 * 用户登录状态存储器，实现了用户登录状态的存取方法。 <br/> 
 *
 * @date: 2017年11月21日 上午11:44:34
 * @author taisenki 
 * @version  
 * @since 
 */  
public interface UserLoggedStatusStore {
	
	/**
	 * 增加新的用户登录状态。
	 * @param userLoggedStatus 用户登录状态。
	 */
	public void addUserLoggedStatus(UserLoggedStatus userLoggedStatus);
	
	/**
	 * 删除用户登录状态。
	 * @param userId 用户标识。
	 * @param appId 应用标识。
	 */
	public void deleteUserLoggedStatus(String userId, String appId);
	
	/**
	 * 清除某个用户所有的登录状态。
	 * @param userId 用户标识。
	 */
	public void clearUpUserLoggedStatus(String userId);
	
	/**
	 * 查询用户标识对用的用户所有的登录状态。
	 * @param userId 用户标识。
	 * @param 用户登录状态。
	 */
	public List<UserLoggedStatus> findUserLoggedStatus(String userId);
	

}
