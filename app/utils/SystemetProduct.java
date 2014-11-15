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
        String pn = n.get("product_number").asText();
        return "<http://www.systembolaget.se/Sok-dryck/Dryck/?" +
                "&varuNr=" + pn.substring(0, pn.length()-2)+
                "|" + this.getName() + ">";
    }

    public String getName() {
        return n.get("name").asText() + " " + n.get("name_2").asText();
    }
}