package com.likeapig.elimination.utils;

import org.bukkit.*;

public class LocationUtils
{
    public static String locationToString(final Location l) {
        final StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf(l.getWorld().getName()) + ":");
        sb.append(String.valueOf(l.getX()) + ":");
        sb.append(String.valueOf(l.getY()) + ":");
        sb.append(String.valueOf(l.getZ()) + ":");
        sb.append(String.valueOf(l.getPitch()) + ":");
        sb.append(l.getYaw());
        return sb.toString();
    }
    
    public static Location stringToLocation(final String s) {
        try {
            final String[] s2 = s.split(":");
            final World w = Bukkit.getWorld(s2[0]);
            final double x = Double.parseDouble(s2[1]);
            final double y = Double.parseDouble(s2[2]);
            final double z = Double.parseDouble(s2[3]);
            final float pitch = Float.parseFloat(s2[4]);
            final float yaw = Float.parseFloat(s2[5]);
            final Location loc = new Location(w, x, y, z, pitch, yaw);
            return loc;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}

