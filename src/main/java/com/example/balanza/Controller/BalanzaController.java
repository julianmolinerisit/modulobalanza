package com.example.balanza.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.balanza.Services.BalanzaService;

@Controller
public class BalanzaController {

    @Autowired
    private BalanzaService balanzaService;

    @GetMapping("/")
    public String index() {
        return "index"; // This should render the index.html file from /templates/
    }

    @GetMapping("/connect/{portName}")
    @ResponseBody
    public String connect(@PathVariable String portName) {
        System.out.println("Attempting to connect to port: " + portName);
        balanzaService.connect(portName);
        System.out.println("Connected to the scale at port " + portName);
        return "Connected to the scale at port " + portName;
    }

    @GetMapping("/disconnect")
    @ResponseBody
    public String disconnect() {
        System.out.println("Attempting to disconnect from the scale.");
        balanzaService.disconnect();
        System.out.println("Disconnected from the scale.");
        return "Disconnected from the scale";
    }

    @GetMapping("/weight")
    @ResponseBody
    public String getWeight() {
        String weight = balanzaService.getLastWeight();
        System.out.println("Fetching last weight: " + weight);
        return weight;
    }
}
