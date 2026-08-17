package edu.wisc.wud.games.wud_games_website;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.wisc.wud.games.wud_games_website.user_account.UserAccountResource;
import jakarta.servlet.http.HttpServletRequest;


/**
 * Serve Reacts index.html for all requests that are not relevant for the backend.
 */
@Controller
public class ReactForwardController {

    @Autowired
    UserAccountResource userAccountResource;

    @GetMapping("{path:^(?!api|public|css|js|images)[^\\.]*}/**")
    public String handleForward(HttpServletRequest request, Model model) {
        String uri = request.getRequestURI();
        // This is where we can add more logic to determine which path should be forwarded. For example, you could check the URI and forward it to a specific controller or view.
        if (uri.startsWith("/myuser")) {
            model.addAttribute("user_email", "example hard coded value");
            return "myuser";
        }
        
        return "forward:/";
    }

}

