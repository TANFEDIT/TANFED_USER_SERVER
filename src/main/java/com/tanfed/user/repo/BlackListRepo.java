package com.tanfed.user.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tanfed.user.entity.BlackListToken;
@Repository
public interface BlackListRepo extends JpaRepository<BlackListToken, Long> {

	public void deleteByJwt(String jwt);
}
