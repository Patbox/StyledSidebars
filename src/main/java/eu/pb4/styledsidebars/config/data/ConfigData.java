package eu.pb4.styledsidebars.config.data;

import com.google.gson.annotations.SerializedName;

public class ConfigData {
    @SerializedName("config_version_dont_modify")
    public int version = 1;
    public String _comment = "Before changing anything, see https://github.com/Patbox/StyledSidebars#configuration";
    @SerializedName("default_style")
    public String defaultStyle = "default";

    @SerializedName("messages")
    public Messages messages = new Messages();

    public static final class Messages {
        @SerializedName("changed")
        public String switchMsg = "Your sidebar has been changed to: <gold>${style}</gold>";
        @SerializedName("unknown")
        public String unknownStyle = "<red>This sidebar doesn't exist!</red>";
    }
}
