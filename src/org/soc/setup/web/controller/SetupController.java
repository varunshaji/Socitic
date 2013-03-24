package org.soc.setup.web.controller;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.soc.setup.service.SocVosaoSetupService;
import org.soc.setup.web.model.SetupModel;
import org.soc.shoppe.account.vo.RoleVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SetupController {
	
	Logger log = Logger.getLogger(SetupController.class);
	
	@Inject
	private SocVosaoSetupService socVosaoSetupService;
	
	@RequestMapping(value="/soc/setup.htm",method=RequestMethod.GET)
	public String setupSoc(@ModelAttribute("setupModel") SetupModel setupModel,ModelMap map) {
		String result = null;
		if(!socVosaoSetupService.initStatus()) {
			result = "redirect:security_login.htm";
		}else {
			map.put("setupModel", setupModel);
			result = "setup/setupaccount";
		}
		return result;
	}

	@RequestMapping(value="/soc/setup.htm",method=RequestMethod.POST)
	public String runSocSetup(@ModelAttribute("setupModel") SetupModel setupModel) {
		
		try {
			setupModel.getAccountVO().setEnabled(true);
			RoleVO roleVO = new RoleVO();
			roleVO.setRole("ROLE_ADMIN");
			setupModel.getAccountVO().getRoles().add(roleVO);
			socVosaoSetupService.setupService(setupModel.getAccountVO());
		}catch(Exception exception) {
			log.log(Priority.DEBUG,"Error please",exception);
		}
		return "redirect:security_login.htm";
	}
	
	public SocVosaoSetupService getSocVosaoSetupService() {
		return socVosaoSetupService;
	}

	public void setSocVosaoSetupService(SocVosaoSetupService socVosaoSetupService) {
		this.socVosaoSetupService = socVosaoSetupService;
	}
}
