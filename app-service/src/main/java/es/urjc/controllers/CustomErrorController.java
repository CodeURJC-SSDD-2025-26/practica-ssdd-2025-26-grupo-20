package es.urjc.controllers;

import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Handles all uncaught errors (404, 403, 500, etc.) and renders the custom
 * error.html page with the same style as the rest of the website.
 */
@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {

        // Read the standard Jakarta Servlet error attributes
        Object statusObj = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object messageObj = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object pathObj = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        int status = 500;
        if (statusObj instanceof Integer) {
            status = (Integer) statusObj;
        }

        String reason;
        switch (status) {
            case 400: reason = "Bad Request"; break;
            case 401: reason = "Unauthorized"; break;
            case 403: reason = "Forbidden"; break;
            case 404: reason = "Not Found"; break;
            case 405: reason = "Method Not Allowed"; break;
            case 500: reason = "Internal Server Error"; break;
            default:  reason = (messageObj != null) ? messageObj.toString() : "Error";
        }

        model.addAttribute("status", status);
        model.addAttribute("error", reason);
        model.addAttribute("path", pathObj);

        // Flags used by the Mustache template to show the right message
        model.addAttribute("is404", status == 404);
        model.addAttribute("is403", status == 403);
        model.addAttribute("is401", status == 401);
        model.addAttribute("is500", status == 500);
        model.addAttribute("isOther",
                status != 404 && status != 403 && status != 401 && status != 500);

        return "error";
    }
}