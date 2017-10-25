package com.rcodingschool.carrepair.Controllers;


import com.rcodingschool.carrepair.Domain.Part;
import com.rcodingschool.carrepair.Domain.RepairPart;
import com.rcodingschool.carrepair.Model.RepairPartForm;
import com.rcodingschool.carrepair.Services.PartService;
import com.rcodingschool.carrepair.Services.RepairPartService;
import com.rcodingschool.carrepair.Services.RepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class RepairPartsController {

    private static final String REPAIRPART_FORM = "repairPartsForm";

    //the parts of a specific repair
    private static final String REPAIRPART_LIST = "repairPartList";
    //the full list of parts
    private static final String WHOLE_PART_LIST = "wholePartList";
    //for error messages
    private static final String MESSAGE = "errorMessage";

    @Autowired
    private PartService partService;

    @Autowired
    private RepairPartService repairPartService;

    @Autowired
    private RepairService repairService;

    //We will use the @InitBinder annotation and the initBinder method to
    //trim all the user's input from spaces
    //For example if the user enters "    John     " it will be trimmed to "John"
    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @RequestMapping(value = "/repairs/parts/{repairID}", method = RequestMethod.GET)
    public String showRepairPartsView(@PathVariable Long repairID, Model model) {
        Map<String, Object> map = model.asMap();
        //If our Model does not contain a repairPartForm, add a new RepairPartForm()
        List<RepairPart> repairsCurrentPartsList = repairPartService.findAllByRepairID(repairID);
        List<Part> wholePartsList = partService.findAll();
        if (!map.containsKey(REPAIRPART_FORM)) {
            RepairPartForm repairPartForm = new RepairPartForm();
            repairPartForm.setRepairID(repairID);
            model.addAttribute(REPAIRPART_FORM, new RepairPartForm ());
        }
        model.addAttribute(REPAIRPART_LIST, repairsCurrentPartsList);
        model.addAttribute(WHOLE_PART_LIST, wholePartsList);
        return "repairParts";
    }


    //Add a part in a repair

    //The processCreateVehicle() method will map "/admin/vehicle/create" POST requests
    //and eventually it will redirect to "/admin/vehicles"
    @RequestMapping(value = "/repairParts/add", method = RequestMethod.POST)
    public String processAddRepairPart(@Valid @ModelAttribute(REPAIRPART_FORM) RepairPartForm repairPartForm,
                                       BindingResult bindingResult, Model model,
                                       RedirectAttributes redirectAttributes) {

        //If something does not pass our @Valid(ations), then this means that our BindingResult
        //object ".hasErrors()" so we will send the user again to the registration form to correct his mistakes
        if (bindingResult.hasErrors()) {
            //Also we will be adding repairPartForm to RedirectAttributes so that we can keep his valid inputs and reshow them
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult." + REPAIRPART_FORM, bindingResult);
            redirectAttributes.addFlashAttribute(REPAIRPART_FORM, repairPartForm);
            return "redirect:/admin/repairParts";
        }
        try {
            //Ttrying to build a RepairPart object
            //RepairPart repairPart = RepairPartConverter.buildRepairPartObject(repairPartForm);
            //repairPartService.save(repairPart);
            return "redirect:/admin/repairParts";

        } catch (Exception exception) {
            //if an error occurs show it to the user :(
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            return "redirect:/admin/repairParts";
        }
    }

}