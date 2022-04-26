package mk.ukim.finki.covid19_statistics.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = { "/statistics"})
public class StatisticsController {

    @GetMapping
    public String getStatisticsPage(Model model) {
        model.addAttribute("bodyContent", "statistics");
        return "master-template";
    }
}
