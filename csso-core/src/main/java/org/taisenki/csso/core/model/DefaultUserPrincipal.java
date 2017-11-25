package org.taisenki.csso.core.model;

import java.util.Map;

/**
 * 默认的用户主体对象。
 * @author Administrator
 *
 */
public class DefaultUserPrincipal extends AbstractPrincipal {

	public DefaultUserPrincipal() {
		super();
	}

	public DefaultUserPrincipal(String id, Map<String, Object> attributes) {
		super(id, attributes);
	}

	
}
