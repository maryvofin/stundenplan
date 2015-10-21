package de.maryvofin.stundenplan.app.database;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.materialdrawer.holder.ImageHolder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.util.ArrayList;
import java.util.Locale;



public class IProfileAdapter implements IProfile<IProfileAdapter> {

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

    @Override
    public StringHolder getName() {
        return new StringHolder(profile.getName());
    }

    @Override
    public IProfileAdapter withEmail(String email) {
        return this;
    }

    @Override
    public StringHolder getEmail() {
        return new StringHolder("");
    }

    @Override
    public IProfileAdapter withIcon(Drawable icon) {
        image = new ImageHolder(icon);
        return this;
    }

    @Override
    public IProfileAdapter withIcon(Bitmap bitmap) {
        image = new ImageHolder(bitmap);
        return this;
    }

    @Override
    public IProfileAdapter withIcon(int iconRes) {
        image = new ImageHolder(iconRes);
        return this;
    }

    @Override
    public IProfileAdapter withIcon(String url) {
        image = new ImageHolder(url);
        return this;
    }

    @Override
    public IProfileAdapter withIcon(Uri uri) {
        image = new ImageHolder(uri);
        return this;
    }

    @Override
    public IProfileAdapter withIcon(IIcon icon) {
        image = new ImageHolder(icon);
        return this;
    }

    @Override
    public ImageHolder getIcon() {
        return image;
    }

    @Override
    public IProfileAdapter withSelectable(boolean selectable) {
        this.selectable = selectable;
        return this;
    }

    @Override
    public boolean isSelectable() {
        return selectable;
    }

    @Override
    public IProfileAdapter withIdentifier(int identifier) {
        return this;
    }

    @Override
    public int getIdentifier() {
        return profile.getUuid().hashCode();
    }

    public static ArrayList<IProfile> generateProfileList() {
        ArrayList<IProfile> list = new ArrayList<>();

        ColorGenerator generator = ColorGenerator.MATERIAL;

        for(Profile p: Database.getInstance().getProfiles().getProfiles()) {
            list.add(new IProfileAdapter(p).withIcon(TextDrawable.builder().buildRect(p.getName().substring(0,1).toUpperCase(Locale.getDefault()), generator.getColor(p.getName()))));
        }

        return list;
    }

    public Profile getProfile() {
        return profile;
    }
}
