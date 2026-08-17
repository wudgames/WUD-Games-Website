package edu.wisc.wud.games.wud_games_website;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;


/**
 * Serve Reacts index.html for all requests that are not relevant for the backend.
 */
@Controller
public class ReactForwardController {

    @GetMapping("{path:^(?!api|public|css|js|images)[^\\.]*}/**")
    public String handleForward(HttpServletRequest request) {
        String uri = request.getRequestURI();
        // This is where we can add more logic to determine which path should be forwarded. For example, you could check the URI and forward it to a specific controller or view.
        if (uri.startsWith("/myuser")) {
            return "myuser";
        }
        
        return "forward:/";
    }

}

