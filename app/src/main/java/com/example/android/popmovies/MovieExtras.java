package com.example.android.popmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by berso on 8/4/17.
 */

public class MovieExtras implements Parcelable {

    public String itemType;
    public String name;
    public String url;

    public MovieExtras(String _itemType, String _name, String _url){
        itemType = _itemType;
        name = _name;
        url = _url;
    }

    protected MovieExtras(Parcel in) {
        itemType = in.readString();
        name = in.readString();
        url = in.readString();
    }

    public static final Creator<MovieExtras> CREATOR = new Creator<MovieExtras>() {
        @Override
        public MovieExtras createFromParcel(Parcel in) {
            return new MovieExtras(in);
        }

        @Override
        public MovieExtras[] newArray(int size) {
            return new MovieExtras[size];
        }
    };

    public String getItemType(){ return  itemType;}
    public String getName(){ return name;}
    public String getUrl(){ return url;}


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(itemType);
        dest.writeString(name);
        dest.writeString(url);
    }
}
