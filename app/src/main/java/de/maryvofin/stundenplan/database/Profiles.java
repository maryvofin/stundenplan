package de.maryvofin.stundenplan.database;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by mark on 01.10.2015.
 */
public class Profiles implements Serializable{

    Profile currentProfile;
    List<Profile> profiles = new LinkedList<>();

    public Profiles(Profile currentProfile) {
        this.currentProfile = currentProfile;
        profiles.add(currentProfile);
    }

    public Profile getCurrentProfile() {
        return currentProfile;
    }

    public List<Profile> getProfiles() {
        return profiles;
    }

    public void setCurrentProfile(Profile currentProfile) {
        this.currentProfile = currentProfile;
    }

    public void setProfiles(List<Profile> profiles) {
        this.profiles = profiles;
    }
}
