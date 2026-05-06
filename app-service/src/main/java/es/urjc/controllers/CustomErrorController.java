package es.urjc.controllers;

import java.util.Map;

import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public Object handleError(HttpServletRequest request,
                              HttpServletResponse response,
                              Model model) {

        Object statusObj = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object messageObj = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object pathObj    = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        int status = (statusObj instanceof Integer) ? (Integer) statusObj : 500;

        String reason = switch (status) {
            case 400 -> "Bad Request";
            case 401 -> "Unauthorized";
            case 403 -> "Forbidden";
            case 404 -> "Not Found";
            case 405 -> "Method Not Allowed";
            case 500 -> "Internal Server Error";
            default  -> (messageObj != null) ? messageObj.toString() : "Error";
        };

        String path = (pathObj != null) ? pathObj.toString() : "";

        // --- Rutas de la API REST: devolver JSON ---
        if (path.startsWith("/api/")) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(status);
            return Map.of(
                    "status",  status,
                    "error",   reason,
                    "message", messageObj != null ? messageObj.toString() : "",
                    "path",    path
            );
        }

        // --- Resto (web): devolver HTML con Mustache ---
        model.addAttribute("status",  status);
        model.addAttribute("error",   reason);
        model.addAttribute("path",    path);
        model.addAttribute("is404",   status == 404);
        model.addAttribute("is403",   status == 403);
        model.addAttribute("is401",   status == 401);
        model.addAttribute("is500",   status == 500);
        model.addAttribute("isOther", status != 404 && status != 403
                                   && status != 401 && status != 500);

        return "error";
    }
}