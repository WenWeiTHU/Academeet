/*
 * Copyright 2010-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mobilecourse.backend.model;

import com.mobilecourse.backend.Globals;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;


/**
 * @author Roy Clarkson
 */

@PropertySource(value="classpath:user.properties", ignoreResourceNotFound = true)
@Component
@ConfigurationProperties(prefix = "user")
public class User {

	private int user_id;
	private String username;
	private String password;
	private int user_type;
	private String phone;
//	@Value("${user.signature}")
	private String signature;
//	@Value("${user.avatar}")
	String avatar;

	public User() {

	}

//	@Autowired
	public User(int id, String username, String password, int type) {
		this.user_id = id;
		this.username = username;
		this.password = password;
		this.user_type = type;
		this.signature = Globals.defaultSignature;
		this.avatar = Globals.defaultAvatar;
	}

	@Override
	public String toString() {
		return "User{" +
				"id=" + user_id +
				", username='" + username + '\'' +
				", password='" + password + '\'' +
				", type=" + user_type +
				", signature='" + signature + '\'' +
				", avatar='" + avatar + '\'' +
				'}';
	}

	public int getId() {
		return user_id;
	}

	public void setId(int id) {
		this.user_id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getType() {
		return user_type;
	}

	public void setType(int type) {
		this.user_type = type;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
}
