package de.maryvofin.stundenplan.app.database;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.materialdrawer.holder.ImageHolder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.util.ArrayList;
import java.util.Locale;



public class IProfileAdapter extends ProfileDrawerItem {

    Profile profile;
    ImageHolder image = null;
    boolean selectable = true;

    public IProfileAdapter(Profile profile) {
        this.profile = profile;
    }

    @Override
    public IProfileAdapter withName(String name) {
        profile.setName(name);
        return this;
    }


    public static ArrayList<IProfile> generateProfileList() {
        ArrayList<IProfile> list = new ArrayList<>();

        ColorGenerator generator = ColorGenerator.MATERIAL;

        for(Profile p: Database.getInstance().getProfiles().getProfiles()) {
            String letter = "null";
            try {
                letter = p.getName().substring(0,1).toUpperCase(Locale.getDefault());
            }
            catch (Exception e) {

            }

            list.add(new IProfileAdapter(p).withIcon(TextDrawable.builder()
                            .beginConfig()
                                .width(150)
                                .height(150)
                            .endConfig()
                            .buildRound(letter, generator.getColor(p.getName())))
                .withEmail(p.getName())
                .withName(p.getName())

                .withIdentifier(p.getUuid().hashCode())
            );
        }

        return list;
    }

    public Profile getProfile() {
        return profile;
    }
}
