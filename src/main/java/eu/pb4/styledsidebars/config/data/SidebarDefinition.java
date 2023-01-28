package eu.pb4.styledsidebars.config.data;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import eu.pb4.placeholders.api.TextParserUtils;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.predicate.api.GsonPredicateSerializer;
import eu.pb4.predicate.api.MinecraftPredicate;
import eu.pb4.predicate.api.PredicateRegistry;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SidebarDefinition {
    @SerializedName("require")
    @Nullable
    public MinecraftPredicate require = null;

    @SerializedName("config_name")
    public String configName = "";

    @SerializedName("update_tick_time")
    public int updateRate = 20;

    @SerializedName("page_change")
    public int pageChange = 5;

    @SerializedName("title_change")
    public int titleChange = 10;

    @SerializedName("scroll_speed")
    public int scrollSpeed = 1;

    @SerializedName("scroll_loop")
    public boolean scrollLoop = true;

    @SerializedName("title")
    public List<String> title = List.of("");

    @SerializedName("lines")
    public List<Line> lines;

    @SerializedName("pages")
    public List<List<Line>> pages;

    public void validate() {
        this.scrollSpeed = Math.max(this.scrollSpeed, 1);
        this.updateRate = Math.max(this.updateRate, 1);
        this.pageChange = Math.max(this.pageChange, 1);
        this.titleChange = Math.max(this.titleChange, 1);
    }

    public record Line(List<String> input, @Nullable MinecraftPredicate require) {
        public static Line of(String line) {
            return new Line(List.of(line), null);
        }

        public static Line of(String line, MinecraftPredicate predicate) {
            return new Line(List.of(line), predicate);
        }

        public static Line of(List<String> line, MinecraftPredicate predicate) {
            return new Line(line, predicate);
        }

        public static final class Serializer implements JsonSerializer<Line>, JsonDeserializer<Line> {
            @Override
            public Line deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                if (json.isJsonPrimitive()) {
                    return Line.of(json.getAsString(), null);
                }


                var obj = json.getAsJsonObject();
                var require = obj.get("require");
                var predicate = require == null ? null : PredicateRegistry.decode(require);

                var values = obj.get("value");
                List<String> lines = new ArrayList<>();

                if (values instanceof JsonArray array) {
                    array.forEach(x -> lines.add(x.getAsString()));
                } else {
                    lines.add(values.getAsString());
                }


                return Line.of(lines, predicate);
            }

            @Override
            public JsonElement serialize(Line src, Type typeOfSrc, JsonSerializationContext context) {
                if (src.require == null && src.input.size() == 1) {
                    return new JsonPrimitive(src.input.get(0));
                } else {
                    var obj = new JsonObject();
                    if (src.input.size() == 1) {
                        obj.add("value", new JsonPrimitive(src.input.get(0)));
                    } else {
                        var ar = new JsonArray();
                        for (var l : src.input) {
                            ar.add(l);
                        }
                        obj.add("value", ar);
                    }
                    if (src.require != null) {
                        obj.add("require", GsonPredicateSerializer.INSTANCE.serialize(src.require, null, null));
                    }
                    return obj;
                }
            }
        }
    }
}
