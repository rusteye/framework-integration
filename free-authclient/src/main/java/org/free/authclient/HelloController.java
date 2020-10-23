package org.free.authclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {
    @Autowired
    TokenTask tokenTask;
    @GetMapping("/index.html")
    public String hello(String code, Model model) {
        model.addAttribute("msg", tokenTask.getData(code));
        return "index";
    }
}