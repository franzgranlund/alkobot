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
import utils.AlkoProduct;
import utils.Product;
import utils.SystemetProduct;
import views.html.index;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;

public class Application extends Controller {
    private static Random rand = new Random();

    public static Result index() {
        Logger.info("[{}] GET /", request().remoteAddress());
        List<String> urls = new ArrayList<>();
        urls.add("http://www.playframework.com");
        urls.add("http://www.scala-lang.org");
        urls.add("http://http://akka.io");
        urls.add("https://www.haskell.org/haskellwiki/Haskell");

        response().setHeader(VARY, "Accept-Encoding");
        return ok(index.render(urls.get(rand.nextInt(urls.size()))));
    }

    public static F.Promise<Result> slackRequest() {
        Form<SlackRequest> req = Form.form(SlackRequest.class);
        SlackRequest sr = req.bindFromRequest().get();

        String question = sr.text.toLowerCase().substring(8).trim();
        Logger.info("question:[{}] {}", sr.channel_name, question);
        if (question.startsWith("find ")) {
            return WS.url("http://www.alko.fi/api/find/products")
                    .setQueryParameter("Language", "sv")
                    .setQueryParameter("Page", "0")
                    .setQueryParameter("PageSize", "20")
                    .setQueryParameter("ProductIds", "")
                    .setQueryParameter("Query", question.substring(5))
                    .setQueryParameter("SingleGrape", "false")
                    .setQueryParameter("Sort", "0").get().<Result>map(r -> {

                        JsonNode alkoResult = r.asJson().findPath("Results");
                        List<AlkoProduct> alkoProducts = new ArrayList<>();
                        alkoResult.forEach(p -> alkoProducts.add(new AlkoProduct(p)));
                        String slackResponse = (alkoProducts.isEmpty()) ? "ehm.. wat?" : formatResponse(alkoProducts, "e");
                        return ok(toJson(new SlackResponse(slackResponse)));
                    }).recover(t -> ok(toJson(new SlackResponse("my sources are drunk."))));
        } else if (question.startsWith("hitta ")) {
            return WS.url("http://systemetapi.se/product")
                    .setQueryParameter("name", question.substring(6))
                    .get().<Result>map(r -> {

                        JsonNode sbResult = r.asJson();
                        List<SystemetProduct> products = new ArrayList<>();
                        sbResult.forEach(p -> products.add(new SystemetProduct(p)));
                        String slackResponse = (products.isEmpty()) ? "ehm.. wat?" : formatResponse(products, "kr");
                        return ok(toJson(new SlackResponse(slackResponse)));
                    }).recover(t -> ok(toJson(new SlackResponse("my sources are drunk."))));

        } else if (question.startsWith("nojsa lite")) {
            try {
                String funnies = (Play.isDev()) ? Play.application().configuration().getString("dev.funnies") : Play.application().configuration().getString("prod.funnies");
                List<String> lines = Files.readLines(new File(funnies), Charset.forName("utf-8"));
                String joke = lines.get(rand.nextInt(lines.size()));
                return F.Promise.pure(ok(toJson(new SlackResponse(joke))));
            } catch (IOException e) {
                Logger.error("Could not find funnies {}", e.getMessage());
                return F.Promise.pure(ok(toJson(new SlackResponse("orkar inte."))));
            }
        } else {
            return F.Promise.pure(ok(toJson(new SlackResponse("waat?"))));
        }
    }

    private static String formatResponse(List<? extends Product> ps, String currency) {
        return ps.stream().map(p -> {
            return p.getPrice()  + " "+currency+", " + p.getVolume() + " liter - " + p.getUrl() + "\n";
        }).collect(Collectors.joining(""));
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
