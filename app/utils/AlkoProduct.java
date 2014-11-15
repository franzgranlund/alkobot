package utils;

import com.fasterxml.jackson.databind.JsonNode;

public class AlkoProduct implements Product {
    final private JsonNode n;

    public AlkoProduct(JsonNode n) {
        this.n = n;
    }

    public String getPrice() {
        return n.get("Price").asText();
    }

    public String getVolume() {
        return n.get("Volume").asText();
    }

    public String getUrl() {
        return "<http://www.alko.fi" + n.get("Url").asText() + "|"+ this.getName() + ">";
    }

    public String getName() {
        return n.get("Name").asText();
    }
}