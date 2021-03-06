package main.main.Controller;

import main.main.Model.Employee;
import main.main.Model.Vehicle;
import main.main.Service.EmployeeService;
import main.main.Service.UserService;
import main.main.Service.VehicleService;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

@Secured("ROLE_USER")
@Controller
public class VehicleController {
    private final VehicleService vehicleService;
    private final EmployeeService employeeService;
    private final UserService userService;

    public VehicleController(VehicleService vehicleService, EmployeeService employeeService, UserService userService) {
        this.vehicleService = vehicleService;
        this.employeeService = employeeService;
        this.userService = userService;
    }

    @GetMapping("/allVehicles")
    public String showVehicles(Model model, HttpServletRequest request){
        KeycloakAuthenticationToken principal = (KeycloakAuthenticationToken) request.getUserPrincipal();
        model.addAttribute("vehiclesList", vehicleService.showOurCompanyVehicles(userService.getUserById(principal.getAccount().getKeycloakSecurityContext().getIdToken().getSubject())));
        return "vehicle";
    }

    @GetMapping("/addVehicle")
    public String vehicle(Model model, HttpServletRequest request){
        KeycloakAuthenticationToken principal = (KeycloakAuthenticationToken) request.getUserPrincipal();
        model.addAttribute("vehicle", new Vehicle());
        model.addAttribute("employeeList", employeeService.showEmployeesWithoutVehicle(userService.getUserById(principal.getAccount().getKeycloakSecurityContext().getIdToken().getSubject())));
        return "addVehicle";
    }

    @PostMapping("/addVehicle")
    public String addVehicle(@ModelAttribute @Valid Vehicle vehicle, @ModelAttribute Employee employee, BindingResult bindingResult){
        if(bindingResult.hasErrors()) {
            return "addVehicle";
        }else{
            employeeService.updateEmployeeVehicle(employee.getId(), vehicle);
            vehicleService.addVehicle(vehicle, employee);
            return "redirect:/allVehicles";
        }
    }

    @GetMapping("/vehicle/getOne")
    @ResponseBody
    public Optional<Vehicle> getOne(Long Id){
        return vehicleService.getOne(Id);
    }

    @RequestMapping("/vehicle/update")
    public String update(Vehicle vehicle){
        vehicleService.updateVehicle(vehicle);
        return "redirect:/allVehicles";
    }

    @GetMapping("/vehicle/delete")
    public String delete(Long Id){
        vehicleService.deleteVehicle(Id);
        return "redirect:/allVehicles";
    }
}
