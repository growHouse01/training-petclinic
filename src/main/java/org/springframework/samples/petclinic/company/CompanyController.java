package org.springframework.samples.petclinic.company;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CompanyController {

	private final CompanyRepository companys;

	public CompanyController(CompanyRepository companys) {
		this.companys = companys;
	}

	@GetMapping("/companys/find")
	public String initFindForm(Model model) {
		model.addAttribute("company", new Company());
		return "companys/findCompany";
	}

	@GetMapping("/companys")
	public String processFindCompanyForm(@RequestParam(defaultValue = "1") int page, Company company,
			BindingResult result, Model model) {

		String companyName = company.getCompanyName();
		if (companyName == null) {
			companyName = "";

		}

		Page<Company> results = findPaginatedForCompany(page, companyName);

		return addPaginationModel(page, model, results);
	}

	private String addPaginationModel(int page, Model model, Page<Company> paginated) {
		List<Company> listCompanys = paginated.getContent();
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", paginated.getTotalPages());
		model.addAttribute("totalItems", paginated.getTotalElements());
		model.addAttribute("listCompanys", listCompanys);
		return "companys/companyList";
	}

	private Page<Company> findPaginatedForCompany(int page, String companyName) {
		int pageSize = 5;
		Pageable pageable = PageRequest.of(page - 1, pageSize);
		return companys.findByCompanyNameStartingWith(companyName, pageable);
	}

}