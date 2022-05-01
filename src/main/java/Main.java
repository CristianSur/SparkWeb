import model.Idea;
import model.IdeaDAO;
import model.NotFoundException;
import model.SimpleIdeaDAO;
import spark.ModelAndView;
import spark.Request;
import spark.template.handlebars.HandlebarsTemplateEngine;


import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class Main {
    private static final String FLASH_MESSAGE_KEY = "flash_message";

    public static void main(String[] args) {
        staticFileLocation("/static");
        IdeaDAO dao = new SimpleIdeaDAO();

        before((request, response) -> {
            if (request.cookie("username") != null) {
                request.attribute("username",
                        request.cookie("username"));
            }
        });
        before("/ideas", (request, response) -> {
            if (request.attribute("username") == null) {
                setFlashMessage(request, "Whoops, please sign in first!");
                response.redirect("/");
                halt();
            }
        });

        get("/", (request, response) -> {
            Map<String, String> model = new HashMap<>();
            model.put("username", request.attribute("username"));
            model.put("flashMessage",  captureFlashMessage(request));
            return new ModelAndView(model, "index.hbs");
        }, new HandlebarsTemplateEngine());

        post("/sign-in", (request, response) -> {
            Map<String, String> model = new HashMap<>();
            String username = request.queryParams("username");
            response.cookie("username", username);
            model.put("username", username);
            return new ModelAndView(model, "sign-in.hbs");
        }, new HandlebarsTemplateEngine());

        get("/ideas", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("ideas", dao.findAll());
            model.put("flashMessage", captureFlashMessage(request));
            return new ModelAndView(model, "ideas.hbs");
        }, new HandlebarsTemplateEngine());

        post("/ideas", (request, response) -> {
            String title = request.queryParams("title");
            Idea idea = new Idea(title, request
                    .cookie("username"));
            dao.add(idea);
            response.redirect("/ideas");
            return null;
        });

        get("/ideas/:slug", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("idea", dao.findBySlug(request.params("slug")));
            return new ModelAndView(model, "idea.hbs");
        }, new HandlebarsTemplateEngine());

        post("/ideas/:slug/vote", (request, response) -> {
            Idea idea = dao.findBySlug(request.params("slug"));
            boolean added = idea.addVoter(request.attribute("username"));
            if (added) {
                setFlashMessage(request, "Thanks for your vote!");
            } else {
                setFlashMessage(request, "You already voted!");
            }
            response.redirect("/ideas");
            return null;
        });

        exception(NotFoundException.class, (exception, request, response) -> {
            response.status(404);
            HandlebarsTemplateEngine engine = new HandlebarsTemplateEngine();
            String html = engine.render(
                    new ModelAndView(null, "not-found.hbs"));
            response.body(html);
        });

    }

    private static void setFlashMessage(Request request, String message) {
        request.session().attribute(FLASH_MESSAGE_KEY, message);
    }

    private static String getFlashMessage(Request request) {
        if (request.session(false) == null) {
            return null;
        }
        if (!request.session().attributes().contains(FLASH_MESSAGE_KEY)) {
            return null;
        }
        return (String) request.session().attribute(FLASH_MESSAGE_KEY);
    }

    private static String captureFlashMessage(Request request) {
        String message = getFlashMessage(request);
        if (message != null) {
            request.session().removeAttribute(FLASH_MESSAGE_KEY);
        }
        return message;
    }

}
