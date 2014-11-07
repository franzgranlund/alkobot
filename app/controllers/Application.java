package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.io.Files;
import play.Logger;
import play.Play;
import play.data.Form;
import play.libs.F;
import play.libs.ws.WS;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;

public class Application extends Controller {

    public static Result index() {
        return ok("alkobot at your service. drunks will be drunks.");
    }

    public static F.Promise<Result> slackRequest() {
        Form<SlackRequest> req = Form.form(SlackRequest.class);
        SlackRequest sr = req.bindFromRequest().get();

        String question = sr.text.toLowerCase().substring(8).trim();
        Logger.info("question: {}", question);
        if (question.startsWith("find ")) {
            // http://www.alko.fi/api/find/products?Language=sv&Page=0&PageSize=20&ProductIds=&Query=lapin+kulta*&SingleGrape=false&Sort=0
            return WS.url("http://www.alko.fi/api/find/products")
                    .setQueryParameter("Language", "sv")
                    .setQueryParameter("Page", "0")
                    .setQueryParameter("PageSize", "20")
                    .setQueryParameter("ProductIds", "")
                    .setQueryParameter("Query", question.substring(5))
                    .setQueryParameter("SingleGrape", "false")
                    .setQueryParameter("Sort", "0").get().<Result>map(r -> {

                        JsonNode alkoResult = r.asJson().findPath("Results");

                        List<Product> products = new ArrayList<>();
                        alkoResult.forEach(p -> products.add(new Product(p)));

                        String slackResponse = (products.isEmpty()) ? "ehm.. wat?" : formatResponse(products);

                        return ok(toJson(new SlackResponse(slackResponse)));
            }).recover(t -> ok(toJson(new SlackResponse("my sources are drunk."))));
        } else if (question.startsWith("nojsa lite")) {
            try {
                String funnies = Play.application().configuration().getString("funnies");
                List<String> lines = Files.readLines(new File(funnies), Charset.forName("utf-8"));
                String joke = lines.get(new Random().nextInt(lines.size()));
                return F.Promise.pure(ok(toJson(new SlackResponse(joke))));
            } catch (IOException e) {
                Logger.error("Could not find funnies {}", e.getMessage());
                return F.Promise.pure(ok(toJson(new SlackResponse("orkar inte."))));
            }
        } else {
            return F.Promise.pure(ok(toJson(new SlackResponse("waat?"))));
        }
    }

    private static String formatResponse(List<Product> ps) {
        return ps.stream().map(p -> {
            return p.price  + " e, " + p.volume + " liter - <http://www.alko.fi" + p.url + "|"+ p.name + ">\n";
        }).collect(Collectors.joining(""));
    }

    public static class Product {
        public final String name;
        public final String price;
        public final String volume;
        public final String url;

        public Product(JsonNode n) {
            price = n.get("Price").asText();
            name = n.get("Name").asText();
            volume = n.get("Volume").asText();
            url = n.get("Url").asText();
        }
    }

    public static class SlackResponse {
        public final String text;
        public SlackResponse(String text) {
            this.text = text;
        }
    }

    public static class SlackRequest {
        public String token;
        public String team_id;
        public String channel_id;
        public String channel_name;
        public String timestamp;
        public String user_id;
        public String user_name;
        public String text;
        public String trigger_word;
    }
}
