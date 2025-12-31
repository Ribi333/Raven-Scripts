/*
    automatically sends "Happy New Year" in the chat in hypixel while in the main lobby during the fireworks show
*/

boolean sent;
boolean lowercase;
String locations;
boolean gotLocations;

void onLoad() {
    modules.registerSlider("chat delay", "ms", 250, 150, 500, 25); // default, min, max, increment
    modules.registerButton("lowercase", false);
    sent = false;
    locations = "";
    gotLocations = false;
}

void onPreUpdate() {
    lowercase = modules.getButton(scriptName, "lowercase");
    String title = client.getTitle();

    if (title.contains("Happy New Year!")) {
        if (!sent) {
            if (client.getPlayer().getTicksExisted() % (int) (modules.getSlider(scriptName, "chat delay") / 50) == 0) {
                if (lowercase) {
                    if (gotLocations) {
                        client.chat("happy new year" + locations + "!");
                        sent = true;
                    } else {
                        client.chat("happy new year!");
                        sent = true;
                    }    
                } else {
                    if (gotLocations) {
                        client.chat("Happy New Year" + locations + "!");
                        sent = true;
                    } else {
                        client.chat("Happy New Year!");
                        sent = true;
                    }
                }
            }    
        }
    } else {
        sent = false;
        gotLocations = false;
        locations = "";
    }
}

boolean onChat(String message) {
    String msg = util.strip(message);
    if (msg.contains("   ") && msg.contains(", ") && !msg.contains("---") && !msg.contains("2") && !msg.contains("0") && !msg.contains("6")) {
        locations = msg;
        gotLocations = true;
    }
    return true;
}

void onDisable() {
    sent = false;
    gotLocations = false;
    locations = "";
}
