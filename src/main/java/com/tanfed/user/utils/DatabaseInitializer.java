package com.tanfed.user.utils;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.tanfed.user.entity.User;

import jakarta.annotation.PostConstruct;

@Component
public class DatabaseInitializer {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@PostConstruct
	public void init() {
		// Hash the password before inserting
		String encodedPassword = passwordEncoder.encode("admin@tanfed");

		String sql = "INSERT INTO users (emp_id, emp_name, designation, dob, doj, joining_date, dor, office_name, department,"
				+ "rc_no, date, mobile_no1, mobile_no2, email_id, aadhar_no, pan_no, password, role, current_address, permanent_address, state, same_as_checked) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		String fetch = "SELECT * FROM users WHERE emp_id = ?";

		List<User> users = jdbcTemplate.query(fetch, new BeanPropertyRowMapper<>(User.class), "admin");

		if (users.isEmpty()) {
			String role = UserRole.SUPERADMIN.name();
			jdbcTemplate.update(sql, "admin", "kevin", "Accountant", "2000-10-02", "2000-10-02", "2000-10-02",
					"2000-10-02", "Head Office", "IT Wing", "asd123ghj", "2000-10-02", 6382649996L, 6382649996L,
					"karthiksnk210@gmail.com", 564534312376L, "jyhvgfz7s67", encodedPassword, role,
					"91, St.marys rd, RA Puram, mandaveli, chennai-600028",
					"91, St.marys rd, RA Puram, mandaveli, chennai-600028", "Tamil Nadu", true);
		}
//		try (Connection connection = dataSource.getConnection()) {
//			ScriptUtils.executeSqlScript(connection, new ClassPathResource("data.sql"));
//			System.out.println("✅ data.sql executed successfully.");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

	}
}
