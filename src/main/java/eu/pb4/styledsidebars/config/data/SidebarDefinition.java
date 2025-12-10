package eu.pb4.styledsidebars.config.data;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import eu.pb4.predicate.api.GsonPredicateSerializer;
import eu.pb4.predicate.api.MinecraftPredicate;
import eu.pb4.predicate.api.PredicateRegistry;
import eu.pb4.styledsidebars.ModInit;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.util.Tuple;

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


    public record Line(List<Tuple<String, String>> values, @Nullable MinecraftPredicate require) {
        public static Line of(String line) {
            return new Line(List.of(new Tuple<>(line, "")), null);
        }

        public static Line of(String line, MinecraftPredicate predicate) {
            return new Line(List.of(new Tuple<>(line, "")), predicate);
        }

        public static Line of(List<String> line, MinecraftPredicate predicate) {
            return new Line(line.stream().map(x -> new Tuple<>(x, "")).toList(), predicate);
        }

        public static Line of(String left, String right) {
            return new Line(List.of(new Tuple<>(left, right)), null);
        }

        public static Line of(String left, String right, MinecraftPredicate predicate) {
            return new Line(List.of(new Tuple<>(left, right)), predicate);
        }

        public record Serializer(HolderLookup.Provider lookup) implements JsonSerializer<Line>, JsonDeserializer<Line> {
            @Override
            public Line deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                if (json.isJsonPrimitive()) {
                    return Line.of(json.getAsString());
                } else if (json.isJsonArray()) {
                    var arr = json.getAsJsonArray();
                    if (arr.size() >= 2) {
                        return Line.of(arr.get(0).getAsString(), arr.get(1).getAsString());
                    } else if (arr.size() == 1) {
                        return Line.of("", arr.get(0).getAsString());
                    }
                    return Line.of("");
                }

                var obj = json.getAsJsonObject();
                var require = obj.get("require");
                var predicate = require == null ? null : PredicateRegistry.decode(lookup, require);

                var values = obj.get("value");
                if (values == null) {
                    values = obj.get("values");
                }

                List<Tuple<String, String>> list = new ArrayList<>();

                if (values instanceof JsonArray array) {
                    array.forEach(x -> list.add(new Tuple<>(x.getAsString(), "")));
                } else if (values instanceof JsonObject array) {
                    array.asMap().forEach((left, right) -> {
                        list.add(new Tuple<>(left, right.getAsString()));
                    });
                }  else {
                    list.add(new Tuple<>(values.getAsString(), ""));
                }


                return new Line(list, predicate);
            }

            @Override
            public JsonElement serialize(Line src, Type typeOfSrc, JsonSerializationContext context) {
                if (src.require == null && src.values.size() == 1) {
                    if (src.values.get(0).getB().isEmpty()) {
                        return new JsonPrimitive(src.values.get(0).getA());
                    } else {
                        var arr = new JsonArray();
                        arr.add(src.values.get(0).getA());
                        arr.add(src.values.get(0).getB());
                        return arr;
                    }
                } else {
                    var obj = new JsonObject();
                    if (src.values.stream().allMatch(x -> x.getB().isEmpty())) {
                        if (src.values.size() == 1) {
                            obj.add("values", new JsonPrimitive(src.values.get(0).getA()));
                        } else {
                            var ar = new JsonArray();
                            for (var l : src.values) {
                                ar.add(l.getA());
                            }
                            obj.add("values", ar);
                        }
                    } else {
                        var ar = new JsonObject();
                        for (var l : src.values) {
                            ar.addProperty(l.getA(), l.getB());
                        }
                        obj.add("values", ar);
                    }

                    if (src.require != null) {
                        obj.add("require", GsonPredicateSerializer.create(lookup).serialize(src.require, null, null));
                    }
                    return obj;
                }
            }
        }
    }
}
