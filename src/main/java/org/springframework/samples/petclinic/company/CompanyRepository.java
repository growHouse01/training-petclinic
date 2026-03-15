package org.springframework.samples.petclinic.company;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Integer> {

	Page<Company> findByCompanyNameStartingWith(String companyName, Pageable pageable);

}