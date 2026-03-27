package com.mediusecho.particlehats.util;

public class VersionInfo {

    public int minor = 0;
    public int patch = 0;

    public VersionInfo (int minor, int patch) {
        this.minor = minor;
        this.patch = patch;
    }

    public boolean supports (int minor, int patch) {
        return this.minor > minor || (this.minor >= minor && this.patch >= patch);
    }
}
