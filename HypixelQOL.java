boolean isHypixel;
double avgBps, tickCount, horizontalDist;
int precision;
long firstdroptime = -1;

void onLoad() {
    modules.registerDescription("hypixe qol:");
    modules.registerButton("disable lobby joins", false);
    modules.registerButton("show party list", false);
    //modules.registerButton("cool text", true);
    modules.registerDescription("bps:");
    modules.registerButton("enable bps counter", false);
    modules.registerSlider("average", " ticks", 20, 1, 80, 1);
    modules.registerSlider("precision", " decimals", 2, 0, 8, 1);
    modules.registerDescription("no drop swords:");
    modules.registerButton("enable no sword drop", false);
    modules.registerSlider("double tap delay", "ms", 350, 0, 1000, 25);
    modules.registerButton("wooden sword", false);
    modules.registerButton("stone sword", false);
    modules.registerButton("iron sword", false);
    modules.registerButton("diamond sword", false);
}

boolean onPacketSent(CPacket packet) {
    if (modules.getButton(scriptName, "enable no sword drop")) {
        if (!(packet instanceof C07)) {
            return true;
        }

        C07 c07 = (C07) packet;
        if (!c07.status.startsWith("DROP_")) {
            return true;
        }

        ItemStack item = client.getPlayer().getHeldItem();
        if (item == null) {
            return true;
        }

        String type = item.name;
        if (!type.endsWith("_sword") || !stopdrop (type)) {
            return true;
        }

        long timenow = client.time();
        int dropdelay = (int) modules.getSlider(scriptName, "double tap delay");
        if (firstdroptime == -1 || timenow - firstdroptime > dropdelay) {
            firstdroptime = timenow;
            return false;
        }

        firstdroptime = -1;
        return true;
    }
    return true;
}

void onPreMotion(PlayerState state) {
    Vec3 pos = client.getPlayer().getPosition(), lastPos = client.getPlayer().getLastPosition();
    double deltaX = pos.x - lastPos.x, deltaZ = pos.z - lastPos.z,
                dist = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
    horizontalDist += dist;
    int calcTicks = (int) modules.getSlider(scriptName, "average");
    if (++tickCount >= calcTicks) {
        avgBps = horizontalDist / (calcTicks / 20d);
        resetAvg(false);
    }
    updatePrecision();
}

void onRenderTick(float partialTicks) {
    if (modules.getButton(scriptName, "enable bps counter")) {
        render.text(util.round(client.getPlayer().getBPS(), precision) + " bps", 20, 20, 1, -1, true);
    }
    /*if (modules.getButton(scriptName, "cool text")) {
        render.text("cool text", 470, 292, 1, -2, true); // x, y, size, color (-1 = white; 1 = black)
    }*/
}

boolean onChat(String message) {
    String msg = util.strip(message);
    
    if (modules.getButton(scriptName, "disable lobby joins") && lobbymsg(msg)) {
        return false;
    }

    if (modules.getButton(scriptName, "show party list")) {
        if (msg.contains("joined the party")) {
            client.chat("/party list");
        
        } else if (msg.contains("has left the party")) {
            client.chat("/party list");
        }
    }
    return true;
}

boolean lobbymsg(String msg) {
    return (msg.startsWith(" >>>") && msg.endsWith("lobby! <<<")) || (msg.startsWith("[") && msg.endsWith("lobby!"));
}

boolean hypixelcheck() {
    List<String> scoreboard = world.getScoreboard();
    return (client.getServerIP().toLowerCase().equals("hypixel.net") || client.getServerIP().toLowerCase().endsWith(".hypixel.net") || client.getServerIP().toLowerCase().endsWith(".liquidproxy.net")) && scoreboard != null && util.strip(scoreboard.get(scoreboard.size() - 1)).equals("www.hypixel.net");
}

boolean stopdrop(String type) {
    switch (type) {
        case "wooden_sword":
            return modules.getButton(scriptName, "wooden sword");
        case "stone_sword":
            return modules.getButton(scriptName, "stone sword");
        case "iron_sword":
            return modules.getButton(scriptName, "iron sword");
        case "diamond_sword":
            return modules.getButton(scriptName, "diamond sword");
        default:
            return false;
    }
}

void updatePrecision() {
    precision = (int) modules.getSlider(scriptName, "precision");
}

void resetAvg(boolean bps) {
    tickCount = horizontalDist = 0;
    if (bps) {
        avgBps = 0;
    }
}
