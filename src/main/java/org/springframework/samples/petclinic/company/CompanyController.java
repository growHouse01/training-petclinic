package org.springframework.samples.petclinic.company;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CompanyController {

	private static final String VIEWS_COMPANY_CREATE_OR_UPDATE_FORM = "companys/createOrUpdateCompanyForm";

	private final CompanyRepository companys;

	public CompanyController(CompanyRepository companys) {
		this.companys = companys;
	}

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@ModelAttribute("company")
	public Company findCompany(@PathVariable(name = "companyId", required = false) Integer companyId) {
		return companyId == null ? new Company()
				: this.companys.findById(companyId)
					.orElseThrow(() -> new IllegalArgumentException("Owner not found with id: " + companyId
							+ ". Please ensure the ID is correct " + "and the company exists in the database."));
	}

	@GetMapping("/companys/new")
	public String initCreationForm() {
		return VIEWS_COMPANY_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/companys/new")
	public String processCreationForm(@Valid Company company, BindingResult result,
			RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			redirectAttributes.addFlashAttribute("error", "There was an error in creating the company.");
			return VIEWS_COMPANY_CREATE_OR_UPDATE_FORM;
		}

		this.companys.save(company);
		redirectAttributes.addFlashAttribute("message", "New Companys Created");
		return "redirect:/companys/" + company.getId();
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

	@GetMapping("/companys/{companyId}/edit")
	public String initUpdateCompanyForm() {
		return VIEWS_COMPANY_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/companys/{companyId}/edit")
	public String processUpdateCompanyForm(@Valid Company company, BindingResult result,
			@PathVariable("companyId") int companyId, RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			redirectAttributes.addFlashAttribute("error", "There was an error in updating the company.");
			return VIEWS_COMPANY_CREATE_OR_UPDATE_FORM;
		}

		if (!Objects.equals(company.getId(), companyId)) {
			result.rejectValue("id", "mismatch", "The company ID in the form does not match the URL.");
			redirectAttributes.addFlashAttribute("error", "Company ID mismatch. Please try again.");
			return "redirect:/companys/{companyId}/edit";
		}

		company.setId(companyId);
		this.companys.save(company);
		redirectAttributes.addFlashAttribute("message", "Company Values Updated");
		return "redirect:/companys/{companyId}";

	}

	@GetMapping("/companys/{companyId}")
	public ModelAndView showCompany(@PathVariable("companyId") int companyId) {
		ModelAndView mav = new ModelAndView("companys/companyDetails");
		Optional<Company> optionalCompany = this.companys.findById(companyId);
		Company company = optionalCompany.orElseThrow(() -> new IllegalArgumentException(
				"Company not found with id: " + companyId + ". Please ensure the ID is correct "));
		mav.addObject("company", company);
		return mav;
	}

}