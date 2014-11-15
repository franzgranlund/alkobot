package utils;

import com.fasterxml.jackson.databind.JsonNode;

public class SystemetProduct implements Product {
    final private JsonNode n;

    public SystemetProduct(JsonNode n) {
        this.n = n;
    }

    public String getPrice() {
        return n.get("price").asText();
    }

    public String getVolume() {
        return n.get("volume").asText();
    }

    public String getUrl() {
        return "<http://www.systembolaget.se/Sok-dryck/Dryck/?artikelId=" + n.get("article_id") + "|" + this.getName() + ">";
    }

    public String getName() {
        return n.get("name").asText();
    }
}