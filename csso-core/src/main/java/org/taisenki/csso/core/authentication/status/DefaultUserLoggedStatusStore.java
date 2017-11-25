package org.taisenki.csso.core.authentication.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.taisenki.csso.common.util.BaseUtil;
import org.taisenki.csso.core.model.UserLoggedStatus;



public class DefaultUserLoggedStatusStore implements UserLoggedStatusStore {
	
	private static final Object obj = new Object();
	
	/**
	 * 用户登录状态集合，不允许重复状态值。
	 */
	private Map<UserLoggedStatus, Object> loggedStatus = new WeakHashMap<UserLoggedStatus, Object>();
	
	public Set<UserLoggedStatus> getLoggedStatus() {
		return loggedStatus.keySet();
	}

	/**
	 * 用户标识和用户状态列表之间的映射表，相当于一个索引，方便根据用户标识查询所有的登录状态标。
	 * 其中map中的key是用户标识，value是该用户所有的登录状态列表。 
	 */
	private Map<String, List<UserLoggedStatus>> userIdIndexMap = new HashMap<String, List<UserLoggedStatus>>();
	
	public Map<String, List<UserLoggedStatus>> getUserIdIndexMap() {
		return userIdIndexMap;
	}

	@Override
	public synchronized void addUserLoggedStatus(UserLoggedStatus userLoggedStatus) {
		//检查数据的合法性。
		if(userLoggedStatus!=null 
				&& !BaseUtil.isEmpty(userLoggedStatus.getAppId())
				&& !BaseUtil.isEmpty(userLoggedStatus.getUserId())
				){
			//避免数据为空。
			if(userLoggedStatus.getLoggedDate()==null){
				userLoggedStatus.setLoggedDate(new Date());
			}
			if(!this.loggedStatus.containsKey(userLoggedStatus)){
				this.loggedStatus.put(userLoggedStatus, obj);
				List<UserLoggedStatus> list = this.userIdIndexMap.get(userLoggedStatus.getUserId());
				if(list==null){
					list = new ArrayList<UserLoggedStatus>();
					this.userIdIndexMap.put(userLoggedStatus.getUserId(), list);
				}
				list.add(userLoggedStatus);
			}
		}
	}

	@Override
	public synchronized void deleteUserLoggedStatus(String userId, String appId) {
		//检查数据的合法性。
		if(!BaseUtil.isEmpty(userId) && !BaseUtil.isEmpty(appId)){
			UserLoggedStatus status = new UserLoggedStatus(userId, appId);
			this.loggedStatus.remove(status);
			List<UserLoggedStatus> list = this.userIdIndexMap.get(userId);
			if(list!=null){
				list.remove(status);
			}
		}
	}
	
	@Override
	public synchronized void clearUpUserLoggedStatus(String userId) {
		if(!BaseUtil.isEmpty(userId)){
			List<UserLoggedStatus> list = this.userIdIndexMap.get(userId);
			if(list!=null){
				list.clear();
				this.userIdIndexMap.put(userId, null);
			}
		}
	}


	@Override
	public List<UserLoggedStatus> findUserLoggedStatus(String userId) {
		if(!BaseUtil.isEmpty(userId)){
			return this.userIdIndexMap.get(userId);
		}
		return null;
	}
}
