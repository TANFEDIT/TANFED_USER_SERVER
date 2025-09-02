package com.tanfed.user.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tanfed.user.entity.Designation;
@Repository
public interface DesignationRepo extends JpaRepository<Designation, Long>{

}
